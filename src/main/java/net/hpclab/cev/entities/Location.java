package net.hpclab.cev.entities;

import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.enums.StatusEnumConverter;
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
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "location")
@NamedQueries({
    @NamedQuery(name = "Location.findAll", query = "SELECT l FROM Location l")})
@TypeDef(name = "StatusEnumConverter", typeClass = StatusEnumConverter.class)
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;
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
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    @Column(name = "altitude")
    private Double altitude;
    @Column(name = "radio")
    private Double radio;

    @Size(max = 2147483647)
    @Column(name = "status")
    @Type(type = "StatusEnumConverter")
    private StatusEnum status;

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

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Location)) {
            return false;
        }
        Location other = (Location) object;
        return !((this.idLocation == null && other.idLocation != null) || (this.idLocation != null && !this.idLocation.equals(other.idLocation)));
    }

    @Override
    public String toString() {
        return "net.hpclab.entities.Location[ idLocation=" + idLocation + " ]";
    }
}