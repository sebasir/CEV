package net.hpclab.beans;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.hpclab.entities.Catalog;
import net.hpclab.entities.Collection;
import net.hpclab.sessions.CatalogSession;

@ManagedBean
@SessionScoped
public class CatalogBean extends Utilsbean implements Serializable {

    @Inject
    private CatalogSession catalogSession;

    private static final long serialVersionUID = 1L;
    private Catalog catalog;
    private List<Catalog> allCatalogs;
    private String selectedCont;

    public CatalogBean() {
	   catalogSession = new CatalogSession();
    }

    @PostConstruct
    public void init() {
    }

    public String persist() {
	   try {
		  catalog.setIdCollection(new Collection(new Integer(getSelectedCont())));
		  setCatalog(catalogSession.persist(getCatalog()));
		  if (getCatalog() != null && getCatalog().getIdCatalog() != null)
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(catalog, Actions.createSuccess));
		  else
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(catalog, Actions.createError));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(catalog, Actions.createError));
	   }

	   return findAllCatalogs();
    }

    public void delete() {
	   try {
		  catalogSession.delete(getCatalog());
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(catalog, Actions.deleteSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(catalog, Actions.deleteError));
	   }
    }

    public void prepareCreate() {
	   setCatalog(new Catalog());
    }

    public void edit() {
	   try {
		  setCatalog(catalogSession.merge(getCatalog()));
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(catalog, Actions.updateSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(catalog, Actions.updateError));
	   }
    }

    public String displayList() {
	   findAllCatalogs();
	   return "specimen";
    }

    public String findAllCatalogs() {
	   setAllCatalogs(catalogSession.listAll());
	   return null;
    }

    public Catalog getCatalog() {
	   return catalog;
    }

    public void setCatalog(Catalog catalog) {
	   this.catalog = catalog;
    }

    public List<Catalog> getAllCatalogs() {
	   return allCatalogs;
    }

    public void setAllCatalogs(List<Catalog> allCatalogs) {
	   this.allCatalogs = allCatalogs;
    }

    public String getSelectedCont() {
	   return selectedCont;
    }

    public void setSelectedCont(String selectedCont) {
	   this.selectedCont = selectedCont;
    }
}
