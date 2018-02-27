package net.hpclab.cev.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "location")
@NamedQueries({ @NamedQuery(name = "Location.findAll", query = "SELECT l FROM Location l"),
		@NamedQuery(name = "Location.findByIdLocation", query = "SELECT l FROM Location l WHERE l.idLocation = :idLocation"),
		@NamedQuery(name = "Location.findByLocationName", query = "SELECT l FROM Location l WHERE l.locationName = :locationName"),
		@NamedQuery(name = "Location.findByLatitude", query = "SELECT l FROM Location l WHERE l.latitude = :latitude"),
		@NamedQuery(name = "Location.findByLongitude", query = "SELECT l FROM Location l WHERE l.longitude = :longitude"),
		@NamedQuery(name = "Location.findByAltitude", query = "SELECT l FROM Location l WHERE l.altitude = :altitude"),
		@NamedQuery(name = "Location.findByRadio", query = "SELECT l FROM Location l WHERE l.radio = :radio"),
		@NamedQuery(name = "Location.findOrderedDesc", query = "SELECT l FROM Location l ORDER BY l.idLoclevel.loclevelRank DESC"),
		@NamedQuery(name = "Location.findOrderedAsc", query = "SELECT l FROM Location l ORDER BY l.idLoclevel.loclevelRank") })

public class Location implements Serializable {

	private static final long serialVersionUID = -2404716476915939338L;
	@Id
	@SequenceGenerator(name = "LocationSeq", sequenceName = "location_id_location_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LocationSeq")
	@Basic(optional = false)
	@Column(name = "id_location")
	private Integer idLocation;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "location_name")
	private String locationName;
	@Column(name = "latitude")
	private Double latitude;
	@Column(name = "longitude")
	private Double longitude;
	@Column(name = "altitude")
	private Double altitude;
	@Column(name = "radio")
	private Double radio;

	@Size(max = 32)
	@Column(name = "status")
	private String status;

	@OneToMany(mappedBy = "idLocation")
	private List<Specimen> specimenList;
	@OneToMany(mappedBy = "idContainer")
	private List<Location> locationList;
	@JoinColumn(name = "id_container", referencedColumnName = "id_location")
	@ManyToOne
	private Location idContainer;
	@JoinColumn(name = "id_loclevel", referencedColumnName = "id_loclevel")
	@ManyToOne
	private LocationLevel idLoclevel;

	public Location() {
	}

	public Location(Integer idLocation) {
		this.idLocation = idLocation;
	}

	public Location(Integer idLocation, String locationName) {
		this.idLocation = idLocation;
		this.locationName = locationName;
	}

	public Integer getIdLocation() {
		return idLocation;
	}

	public void setIdLocation(Integer idLocation) {
		this.idLocation = idLocation;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public Double getRadio() {
		return radio;
	}

	public void setRadio(Double radio) {
		this.radio = radio;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Specimen> getSpecimenList() {
		return specimenList;
	}

	public void setSpecimenList(List<Specimen> specimenList) {
		this.specimenList = specimenList;
	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	public Location getIdContainer() {
		return idContainer;
	}

	public void setIdContainer(Location idContainer) {
		this.idContainer = idContainer;
	}

	public LocationLevel getIdLoclevel() {
		return idLoclevel;
	}

	public void setIdLoclevel(LocationLevel idLoclevel) {
		this.idLoclevel = idLoclevel;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idLocation != null ? idLocation.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Location)) {
			return false;
		}
		Location other = (Location) object;
		return !((this.idLocation == null && other.idLocation != null)
				|| (this.idLocation != null && !this.idLocation.equals(other.idLocation)));
	}

	@Override
	public String toString() {
		return this.locationName;
	}
}
