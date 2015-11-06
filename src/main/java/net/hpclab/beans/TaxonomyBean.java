package net.hpclab.beans;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.hpclab.entities.Taxonomy;
import net.hpclab.sessions.TaxonomySession;

@ManagedBean
@SessionScoped
public class TaxonomyBean extends Utilsbean implements Serializable {

    @Inject
    private TaxonomySession taxonomySession;

    private static final long serialVersionUID = 1L;
    private Taxonomy taxonomy;
    private List<Taxonomy> allTaxonomys;
    private String selectedCont;

    public TaxonomyBean() {
	   taxonomySession = new TaxonomySession();
    }

    @PostConstruct
    public void init() {
    }

    public String persist() {
	   try {
		  getTaxonomy().setIdContainer(new Taxonomy(new Integer(getSelectedCont())));
		  setTaxonomy(taxonomySession.persist(getTaxonomy()));
		  if (getTaxonomy() != null && getTaxonomy().getIdTaxonomy() != null)
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(taxonomy, Actions.createSuccess));
		  else
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(taxonomy, Actions.createError));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(taxonomy, Actions.createError));
	   }

	   return findAllTaxonomys();
    }

    public void delete() {
	   try {
		  taxonomySession.delete(getTaxonomy());
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomy(), Actions.deleteSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomy(), Actions.deleteError));
	   }
    }

    public void prepareCreate() {
	   setTaxonomy(new Taxonomy());
    }

    public void edit() {
	   try {
		  setTaxonomy(taxonomySession.merge(getTaxonomy()));
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomy(), Actions.updateSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomy(), Actions.updateError));
	   }
    }

    public String displayList() {
	   findAllTaxonomys();
	   return "specimen";
    }

    public String findAllTaxonomys() {
	   setAllTaxonomys(taxonomySession.listAll());
	   return null;
    }

    public Taxonomy getTaxonomy() {
	   return taxonomy;
    }

    public void setTaxonomy(Taxonomy taxonomy) {
	   this.taxonomy = taxonomy;
    }

    public List<Taxonomy> getAllTaxonomys() {
	   return allTaxonomys;
    }

    public void setAllTaxonomys(List<Taxonomy> allTaxonomys) {
	   this.allTaxonomys = allTaxonomys;
    }

    public String getSelectedCont() {
	   return selectedCont;
    }

    public void setSelectedCont(String selectedCont) {
	   this.selectedCont = selectedCont;
    }
}
