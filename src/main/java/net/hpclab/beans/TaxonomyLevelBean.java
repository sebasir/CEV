package net.hpclab.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.hpclab.entities.TaxonomyLevel;

@ManagedBean
@SessionScoped
public class TaxonomyLevelBean extends UtilsBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private TaxonomyLevel taxonomyLevel;
    private String selectedCont;

    public TaxonomyLevelBean() {
    }

    @PostConstruct
    public void init() {
        System.out.println("Inicializando lista 'TaxonomyLevels'");
        if (allTaxonomyLevels == null) {
            allTaxonomyLevels = new ArrayList<TaxonomyLevel>();
        }
    }

    public void persist() {
        try {
            //setTaxonomyLevel(taxonomyLevelSession.persist(getTaxonomyLevel()));
            if (getTaxonomyLevel() != null && getTaxonomyLevel().getIdTaxlevel() != null) {
                FacesContext.getCurrentInstance().addMessage(null, showMessage(taxonomyLevel, Actions.createSuccess));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, showMessage(taxonomyLevel, Actions.createError));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(taxonomyLevel, Actions.createError));
        }
    }

    public void delete() {
        try {
            //taxonomyLevelSession.delete(getTaxonomyLevel());
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
            //setTaxonomyLevel(taxonomyLevelSession.merge(getTaxonomyLevel()));
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomyLevel(), Actions.updateSuccess));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomyLevel(), Actions.updateError));
        }
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

    public String getSelectedCont() {
        return selectedCont;
    }

    public void setSelectedCont(String selectedCont) {
        this.selectedCont = selectedCont;
    }
}
