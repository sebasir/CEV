package net.hpclab.beans;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.hpclab.entities.Location;
import net.hpclab.sessions.LocationSession;

@ManagedBean
@SessionScoped
public class LocationBean extends Utilsbean implements Serializable {

    @Inject
    private LocationSession locationSession;

    private static final long serialVersionUID = 1L;
    private Location location;
    private List<Location> allLocations;
    private String selectedCont;

    public LocationBean() {
	   locationSession = new LocationSession();
    }

    @PostConstruct
    public void init() {
    }

    public String persist() {
	   try {
		  getLocation().setIdContainer(new Location(new Integer(getSelectedCont())));
		  setLocation(locationSession.persist(getLocation()));
		  if (getLocation() != null && getLocation().getIdLocation() != null)
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(location, Actions.createSuccess));
		  else
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(location, Actions.createError));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(location, Actions.createError));
	   }

	   return findAllLocations();
    }

    public void delete() {
	   try {
		  locationSession.delete(getLocation());
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getLocation(), Actions.deleteSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getLocation(), Actions.deleteError));
	   }
    }

    public void prepareCreate() {
	   setLocation(new Location());
    }

    public void edit() {
	   try {
		  setLocation(locationSession.merge(getLocation()));
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getLocation(), Actions.updateSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getLocation(), Actions.updateError));
	   }
    }

    public String displayList() {
	   findAllLocations();
	   return "specimen";
    }

    public String findAllLocations() {
	   setAllLocations(locationSession.listAll());
	   return null;
    }

    public Location getLocation() {
	   return location;
    }

    public void setLocation(Location location) {
	   this.location = location;
    }

    public List<Location> getAllLocations() {
	   return allLocations;
    }

    public void setAllLocations(List<Location> allLocations) {
	   this.allLocations = allLocations;
    }

    public String getSelectedCont() {
	   return selectedCont;
    }

    public void setSelectedCont(String selectedCont) {
	   this.selectedCont = selectedCont;
    }
}
