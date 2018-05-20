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
 * anteriormente para la tabla <tt>INSTITUTION</tt> de la base de datos
 * conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "institution")
@NamedQueries({ @NamedQuery(name = "Institution.findAll", query = "SELECT i FROM Institution i") })
public class Institution implements Serializable {

	private static final long serialVersionUID = -1848708485717009679L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "InstitutionSeq", sequenceName = "institution_id_institution_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "InstitutionSeq")
	@Basic(optional = false)
	@Column(name = "id_institution")
	private Integer idInstitution;

	/**
	 * Nombre de la institucion
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "institution_name")
	private String institutionName;

	/**
	 * Código NIT de la institución
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "institution_code")
	private String institutionCode;

	/**
	 * URL del dominio
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "domain_url")
	private String domainUrl;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia de la lista de colecciones de la institución
	 */
	@OneToMany(mappedBy = "idInstitution")
	private List<Collection> collectionList;

	/**
	 * Lista de usuarios de una institución
	 */
	@OneToMany(mappedBy = "idInstitution")
	private List<Users> usersList;

	/**
	 * Constructor original
	 */
	public Institution() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idInstitution
	 *            Llave primaria que identifica al registro
	 */
	public Institution(Integer idInstitution) {
		this.idInstitution = idInstitution;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idInstitution
	 *            Llave primaria que identifica al registro
	 * @param institutionName
	 *            Nombre de la institución
	 * @param institutionCode
	 *            Código NIT de la institución
	 * @param domainUrl
	 *            URL del dominio
	 */
	public Institution(Integer idInstitution, String institutionName, String institutionCode, String domainUrl) {
		this.idInstitution = idInstitution;
		this.institutionName = institutionName;
		this.institutionCode = institutionCode;
		this.domainUrl = domainUrl;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdInstitution() {
		return idInstitution;
	}

	/**
	 * @param idInstitution
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdInstitution(Integer idInstitution) {
		this.idInstitution = idInstitution;
	}

	/**
	 * @return Nombre de la institucion
	 */
	public String getInstitutionName() {
		return institutionName;
	}

	/**
	 * @param institutionName
	 *            Nombre de la institucion a definir
	 */
	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	/**
	 * @return Código NIT de la institución
	 */
	public String getInstitutionCode() {
		return institutionCode;
	}

	/**
	 * @param institutionCode
	 *            Código NIT de la institución a definir
	 */
	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	/**
	 * @return URL del dominio
	 */
	public String getDomainUrl() {
		return domainUrl;
	}

	/**
	 * @param domainUrl
	 *            URL del dominio a definir
	 */
	public void setDomainUrl(String domainUrl) {
		this.domainUrl = domainUrl;
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
	 * @return Referencia de la lista de colecciones de la institución
	 */
	public List<Collection> getCollectionList() {
		return collectionList;
	}

	/**
	 * @param collectionList
	 *            Referencia de la lista de colecciones de la institución a definir
	 */
	public void setCollectionList(List<Collection> collectionList) {
		this.collectionList = collectionList;
	}

	/**
	 * @return Lista de usuarios de una institución
	 */
	public List<Users> getUsersList() {
		return usersList;
	}

	/**
	 * @param usersList
	 *            Lista de usuarios de una institución a definir
	 */
	public void setUsersList(List<Users> usersList) {
		this.usersList = usersList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idInstitution != null ? idInstitution.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Institution)) {
			return false;
		}
		Institution other = (Institution) object;
		return !((this.idInstitution == null && other.idInstitution != null)
				|| (this.idInstitution != null && !this.idInstitution.equals(other.idInstitution)));
	}

	@Override
	public String toString() {
		return this.institutionName;
	}
}
