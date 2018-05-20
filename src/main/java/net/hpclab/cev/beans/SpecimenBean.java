/*
 * Colección Entomológica Virtual
 * Universidad Central
 * High Performance Computing Laboratory
 * Grupo COMMONS.
 * 
 * Sebastián Motavita Medellín
 * 
 * 2017 - 2018
 */

package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import net.hpclab.cev.entities.Author;
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
import net.hpclab.cev.services.ObjectRetriever;
import net.hpclab.cev.services.ParseExceptionService;

/**
 * Este servicio permite la interacción con el servicio de
 * <tt>DataBaseService</tt> para la gestión de especímenes registrador en el
 * sistema. Principalmente expone métodos de creación, edición, consulta y
 * eliminación, validando la posibilidad de estas operaciones contra el servicio
 * de <tt>AccessesService</tt>, el cual valida el usuario que realiza la
 * operación.
 * 
 * @author Sebasir
 * @since 1.0
 * @see DataBaseService
 * @see Specimen
 */

@ManagedBean
@ViewScoped
public class SpecimenBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = -4282355371253031023L;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>Specimen</tt>, lo cual permite extender todas las operaciones del
	 * servicio para esta clase.
	 */
	private DataBaseService<Specimen> specimenService;

	/**
	 * Lista de especímenes a buscar
	 */
	private List<Specimen> searchSpecimens;

	/**
	 * Objeto que permite la edición de especímnenes
	 */
	private Specimen specimen;

	/**
	 * Objeto que permite la consulta de especímnenes
	 */
	private Specimen searchSpecimen;

	/**
	 * Objeto que permite la elección de una ubicación
	 */
	private Location location;

	/**
	 * Objeto que permite la elección de una clasificación taxonómica
	 */
	private Taxonomy taxonomy;

	/**
	 * Objeto que permite la elección de un tipo de registro
	 */
	private RegType regType;

	/**
	 * Objeto que permite la elección de un tipo de muestra
	 */
	private SampleType sampleType;

	/**
	 * Objeto que permite la elección de un catálogo
	 */
	private Catalog catalog;

	/**
	 * Objeto que permite la elección de una colección
	 */
	private Collection collection;

	/**
	 * Cadena de texto que guarda la clave primaria de un espécimen
	 */
	private String specimenDetail;

	/**
	 * Cadena de texto que guarda la clave primaria de un catálogo
	 */
	private String selectedCatalog;

	/**
	 * Cadena de texto que guarda la clave primaria de una colección
	 */
	private String selectedCollection;

	/**
	 * Cadena de texto que guarda la clave primaria de un tipo de registro
	 */
	private String selectedRegType;

	/**
	 * Cadena de texto que guarda la clave primaria de un tipo de muestra
	 */
	private String selectedSampleType;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(SpecimenBean.class.getSimpleName());

	/**
	 * Constructor que permite inicializar los servicios de <tt>DataBaseService</tt>
	 */
	public SpecimenBean() {
		try {
			specimenService = new DataBaseService<>(Specimen.class);
			limpiarFiltros();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Permite limpiar los filtros
	 */
	public void limpiarFiltros() {
		specimen = new Specimen();
		searchSpecimen = new Specimen();
		searchSpecimens = null;
	}

	/**
	 * @return El paginador de la consulta de especímenes
	 */
	public DataBaseService<Specimen>.Pager getPager() {
		return specimenService.getPager();
	}

	/**
	 * Permite guardar un objeto de tipo espécimen que se haya definido en la
	 * interfáz validando el permiso de escritura
	 */
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

	/**
	 * Permite editar un objeto de tipo espécimen que se haya definido en la
	 * interfáz validando el permiso de modificación
	 */
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

	/**
	 * Permite eliminar un espécimen, validando el permiso de eliminación
	 */
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

	/**
	 * Permite realizar la búsqueda de especímenes dados unos parámetros de búsqueda
	 */
	public void search() {
		try {
			searchSpecimens = specimenService.getList(searchSpecimen);
			if (specimenService.getPager().getNumberOfResults() == 0)
				showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR, "No se encontraron datos");
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}
	}

	/**
	 * Permite consultar una lista de clasificaciones taxonómicas dado un texto
	 * 
	 * @param query
	 *            Cadena de texto a buscar en los nombres de las clasificaciones
	 * @return Lista de clasificaciones que coiciden los nombres con la cadena de
	 *         texto
	 */
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

	/**
	 * Realiza la preparación de una edición de un especímen en el Wizard
	 * 
	 * @param specimen
	 *            Objeto de espécimen a actualizar
	 */
	public void prepareUpdate(Specimen specimen) {
		this.specimen = specimen;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		TaxonomyBean taxonomyBean = getExternalBean(facesContext, TaxonomyBean.class);
		LocationBean locationBean = getExternalBean(facesContext, LocationBean.class);
		AuthorBean authorBean = getExternalBean(facesContext, AuthorBean.class);
		CollectionBean collectionBean = getExternalBean(facesContext, CollectionBean.class);

		taxonomyBean.limpiarFiltros();
		taxonomyBean
				.setTaxonomy(ObjectRetriever.getObjectFromId(Taxonomy.class, specimen.getIdTaxonomy().getIdTaxonomy()));
		taxonomyBean.createTree();

		locationBean.limpiarFiltros();
		locationBean
				.setLocation(ObjectRetriever.getObjectFromId(Location.class, specimen.getIdLocation().getIdLocation()));
		locationBean.createTree();

		authorBean.restartAuthorTypes();

		authorBean.setCollector(specimen.getIdCollector() != null ? specimen.getIdCollector().getIdAuthor() : null);
		authorBean.setDeterminer(specimen.getIdDeterminer() != null ? specimen.getIdDeterminer().getIdAuthor() : null);
		authorBean.setSpecificEpiteth(
				specimen.getIdEpithetAuthor() != null ? specimen.getIdEpithetAuthor().getIdAuthor() : null);

		collectionBean.limpiarFiltros();
		collectionBean.setCatalog(specimen.getIdCatalog());
		collectionBean.setRegTypeId(specimen.getIdRety() != null ? specimen.getIdRety().getIdRety() : null);
		collectionBean.setSampleTypeId(specimen.getIdSaty() != null ? specimen.getIdSaty().getIdSaty() : null);
		collectionBean.createTree();
	}

	/**
	 * Permite reiniciar los pasos del wizard para la creación o modificación de
	 * especímenes
	 */
	public void restartSpecimenForm() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		TaxonomyBean taxonomyBean = getExternalBean(facesContext, TaxonomyBean.class);
		LocationBean locationBean = getExternalBean(facesContext, LocationBean.class);
		AuthorBean authorBean = getExternalBean(facesContext, AuthorBean.class);
		CollectionBean collectionBean = getExternalBean(facesContext, CollectionBean.class);

		limpiarFiltros();
		taxonomyBean.limpiarFiltros();
		locationBean.limpiarFiltros();
		authorBean.restartAuthorTypes();
		collectionBean.limpiarFiltros();
	}

	/**
	 * @return Objeto que permite la edición de especímnenes
	 */
	public Specimen getSpecimen() {
		return specimen;
	}

	/**
	 * @param specimen
	 *            Objeto que permite la edición de especímnenes a definir
	 */
	public void setSpecimen(Specimen specimen) {
		this.specimen = specimen;
	}

	/**
	 * @return Objeto que permite la elección de una ubicación
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            Objeto que permite la elección de una ubicación a definir
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return Objeto que permite la elección de una clasificación taxonómica
	 */
	public Taxonomy getTaxonomy() {
		return taxonomy;
	}

	/**
	 * @param taxonomy
	 *            Objeto que permite la elección de una clasificación taxonómica a
	 *            definir
	 */
	public void setTaxonomy(Taxonomy taxonomy) {
		this.taxonomy = taxonomy;
	}

	/**
	 * @return Objeto que permite la elección de un tipo de registro
	 */
	public RegType getRegType() {
		return regType;
	}

	/**
	 * @param regType
	 *            Objeto que permite la elección de un tipo de registro a definir
	 */
	public void setRegType(RegType regType) {
		this.regType = regType;
	}

	/**
	 * @return Objeto que permite la elección de un tipo de muestra
	 */
	public SampleType getSampleType() {
		return sampleType;
	}

	/**
	 * @param sampleType
	 *            Objeto que permite la elección de un tipo de muestra a definir
	 */
	public void setSampleType(SampleType sampleType) {
		this.sampleType = sampleType;
	}

	/**
	 * @return Lista de especímenes a buscar
	 */
	public List<Specimen> getSearchSpecimens() {
		return searchSpecimens;
	}

	/**
	 * @return Objeto que permite la consulta de especímnenes
	 */
	public Specimen getSearchSpecimen() {
		return searchSpecimen;
	}

	/**
	 * @return Permite el acceso a todas las clasificaciones taxonómicas
	 */
	public List<Taxonomy> getAllTaxonomys() {
		return DataWarehouse.getInstance().allTaxonomys;
	}

	/**
	 * @return Permite el acceso a todas las ubicaciones
	 */
	public List<Location> getAllLocations() {
		return DataWarehouse.getInstance().allLocations;
	}

	/**
	 * @return Cadena de texto que guarda la clave primaria de una colección
	 */
	public String getSelectedCollection() {
		return selectedCollection;
	}

	/**
	 * @param selectedCollection
	 *            Cadena de texto que guarda la clave primaria de una colección a
	 *            definir
	 */
	public void setSelectedCollection(String selectedCollection) {
		this.selectedCollection = selectedCollection;
	}

	/**
	 * @return Cadena de texto que guarda la clave primaria de un tipo de registro
	 */
	public String getSelectedRegType() {
		return selectedRegType;
	}

	/**
	 * @param selectedRegType
	 *            Cadena de texto que guarda la clave primaria de un tipo de
	 *            registro a definir
	 */
	public void setSelectedRegType(String selectedRegType) {
		this.selectedRegType = selectedRegType;
	}

	/**
	 * @return Cadena de texto que guarda la clave primaria de un tipo de muestra
	 */
	public String getSelectedSampleType() {
		return selectedSampleType;
	}

	/**
	 * @param selectedSampleType
	 *            Cadena de texto que guarda la clave primaria de un tipo de muestra
	 *            a definir
	 */
	public void setSelectedSampleType(String selectedSampleType) {
		this.selectedSampleType = selectedSampleType;
	}

	/**
	 * @return Cadena de texto que guarda la clave primaria de un catálogo
	 */
	public String getSelectedCatalog() {
		return selectedCatalog;
	}

	/**
	 * @param selectedCatalog
	 *            Cadena de texto que guarda la clave primaria de un catálogo a
	 *            definir
	 */
	public void setSelectedCatalog(String selectedCatalog) {
		this.selectedCatalog = selectedCatalog;
	}

	/**
	 * @return Objeto que permite la elección de un catálogo
	 */
	public Catalog getCatalog() {
		return catalog;
	}

	/**
	 * @param catalog
	 *            Objeto que permite la elección de un catálogo a definir
	 */
	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * @return Objeto que permite la elección de una colección
	 */
	public Collection getCollection() {
		return collection;
	}

	/**
	 * @param collection
	 *            Objeto que permite la elección de una colección a definir
	 */
	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	/**
	 * Permite definir el objeto de espécimen a partir de una cadena de texto que
	 * guarda la clave primaria de un espécimen
	 */
	private void setSpecimenfromId() {
		this.specimen = getSpecimen(specimenDetail);
	}

	/**
	 * Permite buscar un espécimen desde una cadena de texto que guarda la clave
	 * primaria de un espécimen
	 * 
	 * @param id
	 *            Cadena de texto que guarda la clave primaria de un espécimen
	 * @return Objeto del espécimen
	 */
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

	/**
	 * @return Cadena de texto que guarda la clave primaria de un espécimen
	 */
	public String getSpecimenDetail() {
		return specimenDetail;
	}

	/**
	 * @param specimenDetail
	 *            Cadena de texto que guarda la clave primaria de un espécimen a
	 *            definir
	 */
	public void setSpecimenDetail(String specimenDetail) {
		this.specimenDetail = specimenDetail;
		setSpecimenfromId();
	}

	/**
	 * Permite representar todos los datos de un espécimen en una cadena de texto
	 * que representa un objeto JSON
	 * 
	 * @return Cadena de texto representando la información de un espécimen
	 */
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

			Integer idAuthor = null;
			Author author = null;

			if (specimen.getIdDeterminer() != null) {
				idAuthor = specimen.getIdDeterminer().getIdAuthor();
				if (idAuthor != null)
					author = ObjectRetriever.getObjectFromId(Author.class, idAuthor);
			}

			taxData.put("specificEpithet", specimen.getSpecificEpithet() == null ? "" : specimen.getSpecificEpithet());
			taxData.put("commonName", specimen.getCommonName());
			taxData.put("determiner", author != null ? author.getAuthorName() : "");

			author = null;
			if (specimen.getIdEpithetAuthor() != null) {
				idAuthor = specimen.getIdEpithetAuthor().getIdAuthor();
				if (idAuthor != null)
					author = ObjectRetriever.getObjectFromId(Author.class, idAuthor);
			}

			taxData.put("authorEpithet", author != null ? author.getAuthorName() : "");
			taxData.put("idenComment", specimen.getIdenComment() == null ? "" : specimen.getIdenComment());
			taxData.put("idenDate", formatDate(specimen.getIdenDate()));

			author = null;
			if (specimen.getIdCollector() != null) {
				idAuthor = specimen.getIdCollector().getIdAuthor();
				if (idAuthor != null)
					author = ObjectRetriever.getObjectFromId(Author.class, idAuthor);
			}

			Location location = specimen.getIdLocation();
			if (location != null && location.getIdLocation() != null)
				location = ObjectRetriever.getObjectFromId(Location.class, location.getIdLocation());

			if (location != null) {
				locData.put("lat", location.getLatitude());
				locData.put("lon", location.getLongitude());
				locData.put("alt", location.getAltitude());
			}
			locData.put("collector", author != null ? author.getAuthorName() : "");
			locData.put("collectDate", formatDate(specimen.getCollectDate()));
			locData.put("collectComment", specimen.getCollectComment() == null ? "" : specimen.getCollectComment());

			colData.put("regType", specimen.getIdRety().getRetyName());
			colData.put("samType", specimen.getIdSaty().getSatyName());
			colData.put("companyName",
					specimen.getIdCatalog().getIdCollection().getIdInstitution().getInstitutionName());
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
