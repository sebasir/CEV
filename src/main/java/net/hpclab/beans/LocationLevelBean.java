package net.hpclab.beans;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import net.hpclab.entities.LocationLevel;

@ManagedBean
@SessionScoped
public class LocationLevelBean extends Utilsbean implements Serializable {

    private static final long serialVersionUID = 1L;
    private LocationLevel locationLevel;
    private String selectedCont;

    public LocationLevelBean() {
    }

    @PostConstruct
    public void init() {
    }

    public String persist() {
        try {
            //setLocationLevel(locationLevelSession.persist(getLocationLevel()));
            if (getLocationLevel() != null && getLocationLevel().getIdLoclevel() != null) {
                FacesContext.getCurrentInstance().addMessage(null, showMessage(locationLevel, Actions.createSuccess));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, showMessage(locationLevel, Actions.createError));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(locationLevel, Actions.createError));
        }

        return findAllLocationLevels();
    }

    public void delete() {
        try {
            //locationLevelSession.delete(getLocationLevel());
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getLocationLevel(), Actions.deleteSuccess));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getLocationLevel(), Actions.deleteError));
        }
    }

    public void prepareCreate() {
        setLocationLevel(new LocationLevel());
    }

    public void edit() {
        try {
            //setLocationLevel(locationLevelSession.merge(getLocationLevel()));
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getLocationLevel(), Actions.updateSuccess));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(getLocationLevel(), Actions.updateError));
        }
    }

    public String displayList() {
        findAllLocationLevels();
        return "specimen";
    }

    public String findAllLocationLevels() {
        //setAllLocationLevels(locationLevelSession.listAll());
        return null;
    }

    public LocationLevel getLocationLevel() {
        return locationLevel;
    }

    public void setLocationLevel(LocationLevel locationLevel) {
        this.locationLevel = locationLevel;
    }

    public List<LocationLevel> getAllLocationLevels() {
        return allLocationLevels;
    }

    public void setAllLocationLevels(List<LocationLevel> allLocationLevels) {
        this.allLocationLevels = allLocationLevels;
    }

    public String getSelectedCont() {
        return selectedCont;
    }

    public void setSelectedCont(String selectedCont) {
        this.selectedCont = selectedCont;
    }
}
