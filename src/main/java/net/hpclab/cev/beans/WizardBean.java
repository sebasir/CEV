package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import net.hpclab.cev.entities.Catalog;
import net.hpclab.cev.entities.Collection;
import net.hpclab.cev.entities.Institution;
import net.hpclab.cev.entities.Specimen;
import net.hpclab.cev.entities.Taxonomy;
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.Util;

@ManagedBean
@ViewScoped
public class WizardBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private DataBaseService<Specimen> specimenService;
	private DataBaseService<Taxonomy> taxonomyService;
	private DataBaseService<Collection> collectionService;
	private DataBaseService<Catalog> catalogService;
	private List<Institution> allInstitutions;
	private List<Collection> allCollections;
	private List<Catalog> allCatalogs;
	private String selectedInstitution;
	private String selectedCollection;
	private String selectedCatalog;
	private Specimen specimen;
	private Catalog catalog;

	private static final Logger LOGGER = Logger.getLogger(WizardBean.class.getSimpleName());

	public WizardBean() {
		try {
			specimenService = new DataBaseService<>(Specimen.class);
			collectionService = new DataBaseService<>(Collection.class);
			catalogService = new DataBaseService<>(Catalog.class);
			taxonomyService = new DataBaseService<>(Taxonomy.class, Constant.UNLIMITED_QUERY_RESULTS);
			allInstitutions = Util.getInstitutions();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	public void filterCollections() {
		if (selectedInstitution != null && !selectedInstitution.isEmpty()) {
			try {
				Collection filter = new Collection();
				filter.setIdInstitution(new Institution(new Integer(selectedInstitution)));
				allCollections = collectionService.getList(filter);
			} catch (Exception e) {
				showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR, "Error consultando!");
			}
		}
	}

	public void filterCatalog() {
		if (selectedCollection != null && !selectedCollection.isEmpty()) {
			try {
				Catalog filter = new Catalog();
				filter.setIdCollection(new Collection(new Integer(selectedCollection)));
				allCatalogs = catalogService.getList(filter);
			} catch (Exception e) {
				showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR, "Error consultando!");
			}
		}
	}

	@PostConstruct
	public void init() {

	}

	@PreDestroy
	public void destroy() {
		specimenService = null;
	}

	public List<Institution> getAllInstitutions() {
		return allInstitutions;
	}

	public void setAllInstitutions(List<Institution> allInstitutions) {
		this.allInstitutions = allInstitutions;
	}

	public List<Collection> getAllCollections() {
		return allCollections;
	}

	public void setAllCollections(List<Collection> allCollections) {
		this.allCollections = allCollections;
	}

	public List<Catalog> getAllCatalogs() {
		return allCatalogs;
	}

	public void setAllCatalogs(List<Catalog> allCatalogs) {
		this.allCatalogs = allCatalogs;
	}

	public String getSelectedInstitution() {
		return selectedInstitution;
	}

	public void setSelectedInstitution(String selectedInstitution) {
		this.selectedInstitution = selectedInstitution;
	}

	public String getSelectedCollection() {
		return selectedCollection;
	}

	public void setSelectedCollection(String selectedCollection) {
		this.selectedCollection = selectedCollection;
	}

	public String getSelectedCatalog() {
		return selectedCatalog;
	}

	public void setSelectedCatalog(String selectedCatalog) {
		this.selectedCatalog = selectedCatalog;
	}

	public Specimen getSpecimen() {
		return specimen;
	}

	public void setSpecimen(Specimen specimen) {
		this.specimen = specimen;
	}
}
