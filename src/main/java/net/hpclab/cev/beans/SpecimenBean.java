package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import net.hpclab.cev.entities.Catalog;
import net.hpclab.cev.entities.Collection;
import net.hpclab.cev.entities.Location;
import net.hpclab.cev.entities.RegType;
import net.hpclab.cev.entities.SampleType;
import net.hpclab.cev.entities.Specimen;
import net.hpclab.cev.entities.Taxonomy;
import net.hpclab.cev.enums.ModulesEnum;
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.services.AccessService;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.DataWarehouse;
import net.hpclab.cev.services.ParseExceptionService;

@ManagedBean
@ViewScoped
public class SpecimenBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = -4282355371253031023L;
	private DataBaseService<Specimen> specimenService;
	private List<Specimen> searchSpecimen;
	private Specimen specimen;
	private Location location;
	private Taxonomy taxonomy;
	private RegType regType;
	private SampleType sampleType;
	private Catalog catalog;
	private Collection collection;
	private PanelGrid specimenForm;
	private boolean create;
	private String specimenDetail;
	private String selectedCatalog;
	private String selectedCollection;
	private String selectedRegType;
	private String selectedSampleType;

	private static final Logger LOGGER = Logger.getLogger(SpecimenBean.class.getSimpleName());

	public SpecimenBean() {
		try {
			specimenService = new DataBaseService<>(Specimen.class);
			limpiarFiltros();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	public void limpiarFiltros() {
		specimen = new Specimen();
		searchSpecimen = null;
	}

	public DataBaseService<Specimen>.Pager getPager() {
		return specimenService.getPager();
	}

	public void persist() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.CREATE_ERROR;
		String transactionMessage = specimen.getCommonName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.SPECIMEN, getUsers(facesContext),
					Constant.AccessLevel.INSERT)) {
				showAccessMessage(facesContext, OutcomeEnum.INSERT_NOT_GRANTED);
				return;
			}

			TaxonomyBean taxonomyBean = getExternalBean(facesContext, TaxonomyBean.class);
			LocationBean locationBean = getExternalBean(facesContext, LocationBean.class);
			AuthorBean authorBean = getExternalBean(facesContext, AuthorBean.class);
			CollectionBean collectionBean = getExternalBean(facesContext, CollectionBean.class);

			if (taxonomyBean == null || locationBean == null || authorBean == null || collectionBean == null) {
				transactionMessage = "Error obteniendo valores del formulario";
				throw new Exception(transactionMessage);
			}

			specimen.setIdTaxonomy(taxonomyBean.getTaxonomy());
			specimen.setIdLocation(locationBean.getLocation());
			specimen.setIdCatalog(collectionBean.getCatalog());
			specimen.setIdRety(collectionBean.getRegType());
			specimen.setIdSaty(collectionBean.getSampleType());
			specimen.setIdCollector(authorBean.getAuthorCollector());
			specimen.setIdDeterminer(authorBean.getAuthorDeterminer());
			specimen.setIdEpithetAuthor(authorBean.getAuthorSpecificEpithet());
			specimen.setIdUser(getUsers(facesContext));
			specimen.setStatus(StatusEnum.ACTIVO.get());
			specimen = specimenService.persist(specimen);
			DataWarehouse.getInstance().allSpecimens.add(specimen);
			outcomeEnum = OutcomeEnum.CREATE_SUCCESS;

			limpiarFiltros();
			taxonomyBean.limpiarFiltros();
			locationBean.limpiarFiltros();
			authorBean.restartAuthorTypes();
			collectionBean.limpiarFiltros();
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error persisting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public void edit() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.UPDATE_ERROR;
		String transactionMessage = specimen.getCommonName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.SPECIMEN, getUsers(facesContext),
					Constant.AccessLevel.UPDATE)) {
				showAccessMessage(facesContext, OutcomeEnum.UPDATE_NOT_GRANTED);
				return;
			}

			TaxonomyBean taxonomyBean = getExternalBean(facesContext, TaxonomyBean.class);
			LocationBean locationBean = getExternalBean(facesContext, LocationBean.class);
			AuthorBean authorBean = getExternalBean(facesContext, AuthorBean.class);
			CollectionBean collectionBean = getExternalBean(facesContext, CollectionBean.class);

			if (taxonomyBean == null || locationBean == null || authorBean == null || collectionBean == null) {
				transactionMessage = "Error obteniendo valores del formulario";
				throw new Exception(transactionMessage);
			}

			specimen.setIdTaxonomy(taxonomy);
			specimen.setIdLocation(location);
			specimen.setIdCatalog(catalog);
			specimen.setIdRety(regType);
			specimen.setIdSaty(sampleType);
			Specimen tempSpecimen = specimenService.merge(specimen);
			DataWarehouse.getInstance().allSpecimens.remove(specimen);
			DataWarehouse.getInstance().allSpecimens.add(tempSpecimen);
			outcomeEnum = OutcomeEnum.UPDATE_SUCCESS;

			limpiarFiltros();
			taxonomyBean.limpiarFiltros();
			locationBean.limpiarFiltros();
			authorBean.restartAuthorTypes();
			collectionBean.limpiarFiltros();
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error editing: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public void delete() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.DELETE_ERROR;
		String transactionMessage = specimen.getCommonName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.SPECIMEN, getUsers(facesContext),
					Constant.AccessLevel.DELETE)) {
				showAccessMessage(facesContext, OutcomeEnum.UPDATE_NOT_GRANTED);
				return;
			}

			specimenService.delete(specimen);
			DataWarehouse.getInstance().allSpecimens.remove(specimen);
			outcomeEnum = OutcomeEnum.DELETE_SUCCESS;
			limpiarFiltros();
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error deleting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public void search() {
		try {
			searchSpecimen = specimenService.getList(specimen);
			if (specimenService.getPager().getNumberOfResults() == 0)
				showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR, "No se encontraron datos");
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}
	}

	public List<Taxonomy> taxonomysQuery(String query) {
		ArrayList<Taxonomy> taxs = new ArrayList<>();
		for (Taxonomy t : DataWarehouse.getInstance().allTaxonomys) {
			if (t.getTaxonomyName().toLowerCase().contains(query.toLowerCase())
					&& taxs.size() < Constant.QUERY_MAX_RESULTS) {
				taxs.add(t);
			}
		}
		return taxs;
	}

	public void prepareUpdate(Specimen onEdit) {
		RequestContext context = RequestContext.getCurrentInstance();
		context.reset("specimenWizardForm");
		specimen = onEdit;
		selectedCatalog = specimen.getIdCatalog().getIdCatalog().toString();
		selectedCollection = specimen.getIdCatalog().getIdCollection().getIdCollection().toString();
		selectedRegType = specimen.getIdRety().getIdRety().toString();
		selectedSampleType = specimen.getIdSaty().getIdSaty().toString();
	}

	public Specimen getSpecimen() {
		return specimen;
	}

	public void setSpecimen(Specimen specimen) {
		this.specimen = specimen;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Taxonomy getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(Taxonomy taxonomy) {
		this.taxonomy = taxonomy;
	}

	public RegType getRegType() {
		return regType;
	}

	public void setRegType(RegType regType) {
		this.regType = regType;
	}

	public SampleType getSampleType() {
		return sampleType;
	}

	public void setSampleType(SampleType sampleType) {
		this.sampleType = sampleType;
	}

	public PanelGrid getDetailPanel() {
		return null;
	}

	public PanelGrid getSpecimenForm() {
		return specimenForm;
	}

	public void setSpecimenForm(PanelGrid specimenForm) {
		this.specimenForm = specimenForm;
	}

	public List<Specimen> getSearchSpecimen() {
		return searchSpecimen;
	}

	public List<Taxonomy> getAllTaxonomys() {
		return DataWarehouse.getInstance().allTaxonomys;
	}

	public List<Location> getAllLocations() {
		return DataWarehouse.getInstance().allLocations;
	}

	public String getSelectedCollection() {
		return selectedCollection;
	}

	public void setSelectedCollection(String selectedCollection) {
		this.selectedCollection = selectedCollection;
	}

	public String getSelectedRegType() {
		return selectedRegType;
	}

	public void setSelectedRegType(String selectedRegType) {
		this.selectedRegType = selectedRegType;
	}

	public String getSelectedSampleType() {
		return selectedSampleType;
	}

	public void setSelectedSampleType(String selectedSampleType) {
		this.selectedSampleType = selectedSampleType;
	}

	public String getSelectedCatalog() {
		return selectedCatalog;
	}

	public void setSelectedCatalog(String selectedCatalog) {
		this.selectedCatalog = selectedCatalog;
	}

	public Catalog getCatalog() {
		return catalog;
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public String onFlowProcess(FlowEvent event) {
		return event.getNewStep();
	}

	public String getHeader() {
		return specimen != null && specimen.getIdSpecimen() != null ? "Editar a " + specimen.getCommonName()
				: "Registrar nuevo espécimen";
	}

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	private void setSpecimenfromId() {
		this.specimen = getSpecimen(specimenDetail);
	}

	private Specimen getSpecimen(String id) {
		Integer idSpecimen;
		try {
			idSpecimen = new Integer(id);
			for (Specimen s : DataWarehouse.getInstance().allSpecimens) {
				if (s.getIdSpecimen().equals(idSpecimen)) {
					return s;
				}
			}
		} catch (NumberFormatException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		return null;
	}

	public String getSpecimenDetail() {
		return specimenDetail;
	}

	public void setSpecimenDetail(String specimenDetail) {
		this.specimenDetail = specimenDetail;
		setSpecimenfromId();
	}

	public String printJSON() {
		JSONObject specimenInfo = new JSONObject();
		if (specimen == null || specimen.getIdSpecimen() == null) {
			specimenInfo.put("error", "No hay espécimen seleccionado");
		} else {
			JSONArray taxArray = new JSONArray();
			JSONArray locArray = new JSONArray();
			JSONObject taxData = new JSONObject();
			JSONObject locData = new JSONObject();
			JSONObject colData = new JSONObject();
			Taxonomy tax = specimen.getIdTaxonomy();
			ArrayList<Taxonomy> taxList = new ArrayList<>();
			while (tax != null) {
				taxList.add(0, tax);
				tax = tax.getIdContainer();
			}

			Location loc = specimen.getIdLocation();
			ArrayList<Location> locList = new ArrayList<>();
			while (loc != null) {
				locList.add(0, loc);
				loc = loc.getIdContainer();
			}
			JSONObject data;
			for (Taxonomy t : taxList) {
				data = new JSONObject();
				data.put("label", t.getIdTaxlevel().getTaxlevelName());
				data.put("value", t.getTaxonomyName());
				taxArray.put(data);
			}

			for (Location l : locList) {
				data = new JSONObject();
				data.put("label", l.getIdLoclevel().getLoclevelName());
				data.put("value", l.getLocationName());
				locArray.put(data);
			}

			taxData.put("specificEpithet", specimen.getSpecificEpithet() == null ? "" : specimen.getSpecificEpithet());
			taxData.put("commonName", specimen.getCommonName());
			taxData.put("idenComment", specimen.getIdenComment() == null ? "" : specimen.getIdenComment());
			taxData.put("idenDate", formatDate(specimen.getIdenDate()));
			locData.put("lat", specimen.getIdLocation().getLatitude());
			locData.put("lon", specimen.getIdLocation().getLongitude());
			locData.put("alt", specimen.getIdLocation().getAltitude());
			locData.put("collectDate", formatDate(specimen.getCollectDate()));
			locData.put("collectComment", specimen.getCollectComment() == null ? "" : specimen.getCollectComment());
			colData.put("regType", specimen.getIdRety().getRetyName());
			colData.put("samType", specimen.getIdSaty().getSatyName());
			colData.put("collectionName", specimen.getIdCatalog().getIdCollection().getCollectionName());
			colData.put("idBioreg", specimen.getIdBioreg());
			colData.put("catalogName", specimen.getIdCatalog().getCatalogName());
			specimenInfo.put("taxArray", taxArray);
			specimenInfo.put("locArray", locArray);
			specimenInfo.put("taxData", taxData);
			specimenInfo.put("locData", locData);
			specimenInfo.put("colData", colData);
		}
		return specimenInfo.toString();
	}
}
