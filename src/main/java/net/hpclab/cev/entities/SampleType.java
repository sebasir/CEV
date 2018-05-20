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
 * anteriormente para la tabla <tt>SAMPLE_TYPE</tt> de la base de datos
 * conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "sample_type")
@NamedQueries({ @NamedQuery(name = "SampleType.findAll", query = "SELECT s FROM SampleType s") })
public class SampleType implements Serializable {

	private static final long serialVersionUID = 820087698379012622L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "SampleTypeSeq", sequenceName = "sample_type_id_saty_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SampleTypeSeq")
	@Basic(optional = false)
	@Column(name = "id_saty")
	private Integer idSaty;

	/**
	 * Nombre del tipo de ejemplar
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "saty_name")
	private String satyName;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia los especímenes que tiene este tipo de ejemplar
	 */
	@OneToMany(mappedBy = "idSaty")
	private List<Specimen> specimenList;

	/**
	 * Constructor original
	 */
	public SampleType() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idSaty
	 *            Llave primaria que identifica al registro
	 */
	public SampleType(Integer idSaty) {
		this.idSaty = idSaty;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idSaty
	 *            Llave primaria que identifica al registro
	 * @param satyName
	 *            Nombre del tipo de ejemplar
	 */
	public SampleType(Integer idSaty, String satyName) {
		this.idSaty = idSaty;
		this.satyName = satyName;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdSaty() {
		return idSaty;
	}

	/**
	 * @param idSaty
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdSaty(Integer idSaty) {
		this.idSaty = idSaty;
	}

	/**
	 * @return Nombre del tipo de ejemplar
	 */
	public String getSatyName() {
		return satyName;
	}

	/**
	 * @param satyName
	 *            Nombre del tipo de ejemplar a definir
	 */
	public void setSatyName(String satyName) {
		this.satyName = satyName;
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
	 * @return Referencia los especímenes que tiene este tipo de ejemplar
	 */
	public List<Specimen> getSpecimenList() {
		return specimenList;
	}

	/**
	 * @param specimenList
	 *            Referencia los especímenes que tiene este tipo de ejemplar a
	 *            definir
	 */
	public void setSpecimenList(List<Specimen> specimenList) {
		this.specimenList = specimenList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idSaty != null ? idSaty.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof SampleType)) {
			return false;
		}
		SampleType other = (SampleType) object;
		return !((this.idSaty == null && other.idSaty != null)
				|| (this.idSaty != null && !this.idSaty.equals(other.idSaty)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.SampleType[ idSaty=" + idSaty + " ]";
	}
}
