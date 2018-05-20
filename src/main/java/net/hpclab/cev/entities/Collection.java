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
 * anteriormente para la tabla <tt>AUDIT_LOG</tt> de la base de datos conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "collection")
@NamedQueries({ @NamedQuery(name = "Collection.findAll", query = "SELECT c FROM Collection c") })
public class Collection implements Serializable {

	private static final long serialVersionUID = 1967542873786951060L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "CollectionSeq", sequenceName = "collection_id_collection_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CollectionSeq")
	@Basic(optional = false)
	@Column(name = "id_collection")
	private Integer idCollection;

	/**
	 * Nombre de la colección
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "collection_name")
	private String collectionName;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia a los catalogos que contiene la colección
	 */
	@OneToMany(mappedBy = "idCollection")
	private List<Catalog> catalogList;

	/**
	 * Referencia a la institución que contiene este catalogo
	 */
	@JoinColumn(name = "id_institution", referencedColumnName = "id_institution")
	@ManyToOne
	private Institution idInstitution;

	/**
	 * Constructor original
	 */
	public Collection() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idCollection
	 *            Llave primaria que identifica al registro
	 */
	public Collection(Integer idCollection) {
		this.idCollection = idCollection;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idCollection
	 *            Llave primaria que identifica al registro
	 * @param collectionName
	 *            Nombre de la colección
	 */
	public Collection(Integer idCollection, String collectionName) {
		this.idCollection = idCollection;
		this.collectionName = collectionName;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdCollection() {
		return idCollection;
	}

	/**
	 * @param idCollection
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdCollection(Integer idCollection) {
		this.idCollection = idCollection;
	}

	/**
	 * @return Nombre de la colección
	 */
	public String getCollectionName() {
		return collectionName;
	}

	/**
	 * @param collectionName
	 *            Nombre de la colección a definir
	 */
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
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
	 * @return Referencia a los catalogos que contiene la colección
	 */
	public List<Catalog> getCatalogList() {
		return catalogList;
	}

	/**
	 * @param catalogList
	 *            Referencia a los catalogos que contiene la colección a definir
	 */
	public void setCatalogList(List<Catalog> catalogList) {
		this.catalogList = catalogList;
	}

	/**
	 * @return Referencia a la institución que contiene este catalogo
	 */
	public Institution getIdInstitution() {
		return idInstitution;
	}

	/**
	 * @param idInstitution
	 *            Referencia a la institución que contiene este catalogo a definir
	 */
	public void setIdInstitution(Institution idInstitution) {
		this.idInstitution = idInstitution;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idCollection != null ? idCollection.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Collection)) {
			return false;
		}
		Collection other = (Collection) object;
		return !((this.idCollection == null && other.idCollection != null)
				|| (this.idCollection != null && !this.idCollection.equals(other.idCollection)));
	}

	@Override
	public String toString() {
		return this.collectionName;
	}
}
