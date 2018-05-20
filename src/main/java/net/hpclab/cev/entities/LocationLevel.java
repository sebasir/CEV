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
 * anteriormente para la tabla <tt>AUDIT_LOG</tt> de la base de datos conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "location_level")
@NamedQueries({ @NamedQuery(name = "LocationLevel.findAll", query = "SELECT l FROM LocationLevel l") })
public class LocationLevel implements Serializable {

	private static final long serialVersionUID = 2691092896015284215L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "LocationLevelSeq", sequenceName = "location_level_id_loclevel_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LocationLevelSeq")
	@Basic(optional = false)
	@Column(name = "id_loclevel")
	private Integer idLoclevel;

	/**
	 * Nombre del nivel de ubicación
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "loclevel_name")
	private String loclevelName;

	/**
	 * Rango del nivel de ubicación
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "loclevel_rank")
	private int loclevelRank;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia a las ubicaciones de este nivel
	 */
	@OneToMany(mappedBy = "idLoclevel")
	private List<Location> locationList;

	/**
	 * Constructor original
	 */
	public LocationLevel() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idLoclevel
	 *            Llave primaria que identifica al registro
	 */
	public LocationLevel(Integer idLoclevel) {
		this.idLoclevel = idLoclevel;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idLoclevel
	 *            Llave primaria que identifica al registro
	 * @param loclevelName
	 *            Nombre del nivel de ubicación
	 * @param loclevelRank
	 *            Rango del nivel de ubicación
	 */
	public LocationLevel(Integer idLoclevel, String loclevelName, int loclevelRank) {
		this.idLoclevel = idLoclevel;
		this.loclevelName = loclevelName;
		this.loclevelRank = loclevelRank;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdLoclevel() {
		return idLoclevel;
	}

	/**
	 * @param idLoclevel
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdLoclevel(Integer idLoclevel) {
		this.idLoclevel = idLoclevel;
	}

	/**
	 * @return Nombre del nivel de ubicación
	 */
	public String getLoclevelName() {
		return loclevelName;
	}

	/**
	 * @param loclevelName
	 *            Nombre del nivel de ubicación a definir
	 */
	public void setLoclevelName(String loclevelName) {
		this.loclevelName = loclevelName;
	}

	/**
	 * @return Rango del nivel de ubicación
	 */
	public int getLoclevelRank() {
		return loclevelRank;
	}

	/**
	 * @param loclevelRank
	 *            Rango del nivel de ubicación a definir
	 */
	public void setLoclevelRank(int loclevelRank) {
		this.loclevelRank = loclevelRank;
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
	 * @return Referencia a las ubicaciones de este nivel
	 */
	public List<Location> getLocationList() {
		return locationList;
	}

	/**
	 * @param locationList
	 *            Referencia a las ubicaciones de este nivel a definir
	 */
	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idLoclevel != null ? idLoclevel.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof LocationLevel)) {
			return false;
		}
		LocationLevel other = (LocationLevel) object;
		return !((this.idLoclevel == null && other.idLoclevel != null)
				|| (this.idLoclevel != null && !this.idLoclevel.equals(other.idLoclevel)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.LocationLevel[ idLoclevel=" + idLoclevel + " ]";
	}
}
