package net.hpclab.beans;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.hpclab.entities.Collection;

@ManagedBean
@SessionScoped
public class CollectionBean extends UtilsBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private Collection collection;
    private List<Collection> allCollections;
    private String selectedCont;

    public CollectionBean() {
    }

    @PostConstruct
    public void init() {
    }

    public String persist() {
        try {
            //setCollection(collectionSession.persist(getCollection()));
            if (getCollection() != null && getCollection().getIdCollection() != null) {
                FacesContext.getCurrentInstance().addMessage(null, showMessage(collection, Operations.CREATE_SUCCESS));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, showMessage(collection, Operations.CREATE_ERROR));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(collection, Operations.CREATE_ERROR));
        }

        return findAllCollections();
    }

    public void delete() {
        try {
            //collectionSession.delete(getCollection());
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getCollection(), Operations.DELETE_SUCCESS));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getCollection(), Operations.DELETE_ERROR));
        }
    }

    public void prepareCreate() {
        setCollection(new Collection());
    }

    public void edit() {
        try {
            //setCollection(collectionSession.merge(getCollection()));
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getCollection(), Operations.UPDATE_SUCCESS));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getCollection(), Operations.UPDATE_ERROR));
        }
    }

    public String displayList() {
        findAllCollections();
        return "specimen";
    }

    public String findAllCollections() {
        //setAllCollections(collectionSession.listAll());
        return null;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public List<Collection> getAllCollections() {
        return allCollections;
    }

    public void setAllCollections(List<Collection> allCollections) {
        this.allCollections = allCollections;
    }

    public String getSelectedCont() {
        return selectedCont;
    }

    public void setSelectedCont(String selectedCont) {
        this.selectedCont = selectedCont;
    }
}
