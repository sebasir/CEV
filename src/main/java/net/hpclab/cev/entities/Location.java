/*
 * Colección Entomológica Virtual
 * Universidad Central
 * High Performance Computing Laboratory
 * Grupo COMMONS.
 * 
 * Sebastián Motavita Medellín
 * 
 * 2017 - 2018
 */
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

/**
 * Entidad mapeada desde el servicio de ORM usado y que homologa la tabla que
 * referencia en la anotación <tt>Table</tt>, a través de la opción de generar
 * entidades a partir de una conexión desde un motor JPA. Esta clase es un POJO
 * (Plain Old Java Object) extendido para su uso en administradores de entidades
 * JPA, y que permiten realizar operaciones como <tt>persist</tt>,
 * <tt>merge</tt>, <tt>delete</tt>, desde un proveedor de persistencia.
 * 
 * <p>
 * Esta entidad en particular, permite homologar las operaciones citadas
 * anteriormente para la tabla <tt>LOCATION</tt> de la base de datos conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
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

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "LocationSeq", sequenceName = "location_id_location_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LocationSeq")
	@Basic(optional = false)
	@Column(name = "id_location")
	private Integer idLocation;

	/**
	 * Nombre de la ubicación
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "location_name")
	private String locationName;

	/**
	 * Latitud de la ubicación
	 */
	@Column(name = "latitude")
	private Double latitude;

	/**
	 * Longitud de la ubicación
	 */
	@Column(name = "longitude")
	private Double longitude;

	/**
	 * Altitud de la ubicación
	 */
	@Column(name = "altitude")
	private Double altitude;

	/**
	 * Radio de covertura de la ubicación
	 */
	@Column(name = "radio")
	private Double radio;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia los especímenes que tiene esta ubicación
	 */
	@OneToMany(mappedBy = "idLocation")
	private List<Specimen> specimenList;

	/**
	 * Referencia las ubicaciones contenidas en esta ubicación
	 */
	@OneToMany(mappedBy = "idContainer")
	private List<Location> locationList;

	/**
	 * Referencia a la ubicacion que contiene esta ubicación
	 */
	@JoinColumn(name = "id_container", referencedColumnName = "id_location")
	@ManyToOne
	private Location idContainer;

	/**
	 * Referencia el nivel de ubicación de este objeto
	 */
	@JoinColumn(name = "id_loclevel", referencedColumnName = "id_loclevel")
	@ManyToOne
	private LocationLevel idLoclevel;

	/**
	 * Constructor original
	 */
	public Location() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idLocation
	 *            Llave primaria que identifica al registro
	 */
	public Location(Integer idLocation) {
		this.idLocation = idLocation;
	}

	/**
	 * Constructor con el nombre del registro y la llave primaria
	 * 
	 * @param idLocation
	 *            Llave primaria que identifica al registro
	 * @param locationName
	 *            Nombre de la ubicación
	 */
	public Location(Integer idLocation, String locationName) {
		this.idLocation = idLocation;
		this.locationName = locationName;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdLocation() {
		return idLocation;
	}

	/**
	 * @param idLocation
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdLocation(Integer idLocation) {
		this.idLocation = idLocation;
	}

	/**
	 * @return Nombre de la ubicación
	 */
	public String getLocationName() {
		return locationName;
	}

	/**
	 * @param locationName
	 *            Nombre de la ubicación a definir
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	/**
	 * @return Latitud de la ubicación
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            Latitud de la ubicación a definir
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return Longitud de la ubicación
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            Longitud de la ubicación a definir
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return Altitud de la ubicación
	 */
	public Double getAltitude() {
		return altitude;
	}

	/**
	 * @param altitude
	 *            Altitud de la ubicación a definir
	 */
	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	/**
	 * @return Radio de covertura de la ubicación
	 */
	public Double getRadio() {
		return radio;
	}

	/**
	 * @param radio
	 *            Radio de covertura de la ubicación a definir
	 * 
	 */
	public void setRadio(Double radio) {
		this.radio = radio;
	}

	/**
	 * @return Estado del registro en la base de datos, referenciando a la
	 *         enumeración <tt>StatusEnum</tt>
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            Estado del registro en la base de datos, referenciando a la
	 *            enumeración <tt>StatusEnum</tt> a definir
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return Referencia los especímenes que tiene esta ubicación
	 */
	public List<Specimen> getSpecimenList() {
		return specimenList;
	}

	/**
	 * @param specimenList
	 *            Referencia los especímenes que tiene esta ubicación a definir
	 */
	public void setSpecimenList(List<Specimen> specimenList) {
		this.specimenList = specimenList;
	}

	/**
	 * @return Referencia las ubicaciones contenidas en esta ubicación
	 */
	public List<Location> getLocationList() {
		return locationList;
	}

	/**
	 * @param locationList
	 *            Referencia las ubicaciones contenidas en esta ubicación a definir
	 */
	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	/**
	 * @return Referencia a la ubicacion que contiene esta ubicación
	 */
	public Location getIdContainer() {
		return idContainer;
	}

	/**
	 * @param idContainer
	 *            Referencia a la ubicacion que contiene esta ubicación a definir
	 */
	public void setIdContainer(Location idContainer) {
		this.idContainer = idContainer;
	}

	/**
	 * @return Referencia el nivel de ubicación de este objeto
	 */
	public LocationLevel getIdLoclevel() {
		return idLoclevel;
	}

	/**
	 * @param idLoclevel
	 *            Referencia el nivel de ubicación de este objeto a definir
	 */
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
