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
 * anteriormente para la tabla <tt>REG_TYPE</tt> de la base de datos conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "reg_type")
@NamedQueries({ @NamedQuery(name = "RegType.findAll", query = "SELECT r FROM RegType r") })
public class RegType implements Serializable {

	private static final long serialVersionUID = 156552950093292297L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "RegTypeSeq", sequenceName = "reg_type_id_rety_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RegTypeSeq")
	@Basic(optional = false)
	@Column(name = "id_rety")
	private Integer idRety;

	/**
	 * Nombre del tipo de registro
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "rety_name")
	private String retyName;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia la lista de especímenes que tienen este tipo de registro
	 */
	@OneToMany(mappedBy = "idRety")
	private List<Specimen> specimenList;

	/**
	 * Constructor original
	 */
	public RegType() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idRety
	 *            Llave primaria que identifica al registro
	 */
	public RegType(Integer idRety) {
		this.idRety = idRety;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idRety
	 *            Llave primaria que identifica al registro
	 * @param retyName
	 *            Nombre del tipo de registro
	 */
	public RegType(Integer idRety, String retyName) {
		this.idRety = idRety;
		this.retyName = retyName;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdRety() {
		return idRety;
	}

	/**
	 * @param idRety
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdRety(Integer idRety) {
		this.idRety = idRety;
	}

	/**
	 * @return Nombre del tipo de registro
	 */
	public String getRetyName() {
		return retyName;
	}

	/**
	 * @param retyName
	 *            Nombre del tipo de registro a definir
	 */
	public void setRetyName(String retyName) {
		this.retyName = retyName;
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
	 * @return Referencia la lista de especímenes que tienen este tipo de registro
	 */
	public List<Specimen> getSpecimenList() {
		return specimenList;
	}

	/**
	 * @param specimenList
	 *            Referencia la lista de especímenes que tienen este tipo de
	 *            registro a definir
	 */
	public void setSpecimenList(List<Specimen> specimenList) {
		this.specimenList = specimenList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idRety != null ? idRety.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof RegType)) {
			return false;
		}
		RegType other = (RegType) object;
		return !((this.idRety == null && other.idRety != null)
				|| (this.idRety != null && !this.idRety.equals(other.idRety)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.RegType[ idRety=" + idRety + " ]";
	}
}
