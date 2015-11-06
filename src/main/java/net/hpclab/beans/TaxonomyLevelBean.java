package net.hpclab.beans;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.hpclab.entities.TaxonomyLevel;
import net.hpclab.sessions.TaxonomyLevelSession;

@ManagedBean
@SessionScoped
public class TaxonomyLevelBean extends Utilsbean implements Serializable {

    @Inject
    private TaxonomyLevelSession taxonomyLevelSession;

    private static final long serialVersionUID = 1L;
    private TaxonomyLevel taxonomyLevel;
    private List<TaxonomyLevel> allTaxonomyLevels;
    private String selectedCont;

    public TaxonomyLevelBean() {
	   taxonomyLevelSession = new TaxonomyLevelSession();
    }

    @PostConstruct
    public void init() {
    }

    public String persist() {
	   try {
		  setTaxonomyLevel(taxonomyLevelSession.persist(getTaxonomyLevel()));
		  if (getTaxonomyLevel() != null && getTaxonomyLevel().getIdTaxlevel()!= null)
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(taxonomyLevel, Actions.createSuccess));
		  else
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(taxonomyLevel, Actions.createError));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(taxonomyLevel, Actions.createError));
	   }

	   return findAllTaxonomyLevels();
    }

    public void delete() {
	   try {
		  taxonomyLevelSession.delete(getTaxonomyLevel());
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomyLevel(), Actions.deleteSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomyLevel(), Actions.deleteError));
	   }
    }

    public void prepareCreate() {
	   setTaxonomyLevel(new TaxonomyLevel());
    }

    public void edit() {
	   try {
		  setTaxonomyLevel(taxonomyLevelSession.merge(getTaxonomyLevel()));
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomyLevel(), Actions.updateSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomyLevel(), Actions.updateError));
	   }
    }

    public String displayList() {
	   findAllTaxonomyLevels();
	   return "specimen";
    }

    public String findAllTaxonomyLevels() {
	   setAllTaxonomyLevels(taxonomyLevelSession.listAll());
	   return null;
    }

    public TaxonomyLevel getTaxonomyLevel() {
	   return taxonomyLevel;
    }

    public void setTaxonomyLevel(TaxonomyLevel taxonomyLevel) {
	   this.taxonomyLevel = taxonomyLevel;
    }

    public List<TaxonomyLevel> getAllTaxonomyLevels() {
	   return allTaxonomyLevels;
    }

    public void setAllTaxonomyLevels(List<TaxonomyLevel> allTaxonomyLevels) {
	   this.allTaxonomyLevels = allTaxonomyLevels;
    }

    public String getSelectedCont() {
	   return selectedCont;
    }

    public void setSelectedCont(String selectedCont) {
	   this.selectedCont = selectedCont;
    }
}
