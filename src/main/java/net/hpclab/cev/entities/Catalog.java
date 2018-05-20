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
 * anteriormente para la tabla <tt>CATALOG</tt> de la base de datos conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "catalog")
@NamedQueries({ @NamedQuery(name = "Catalog.findAll", query = "SELECT c FROM Catalog c") })
public class Catalog implements Serializable {

	private static final long serialVersionUID = -588572996975638318L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "CatalogSeq", sequenceName = "catalog_id_catalog_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CatalogSeq")
	@Basic(optional = false)
	@Column(name = "id_catalog")
	private Integer idCatalog;

	/**
	 * Nombre del catalogo
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "catalog_name")
	private String catalogName;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia a la colección donde está contenido el catálogo
	 */
	@JoinColumn(name = "id_collection", referencedColumnName = "id_collection")
	@ManyToOne
	private Collection idCollection;

	/**
	 * Referencia la lista de especímenes del catálogo
	 */
	@OneToMany(mappedBy = "idCatalog")
	private List<Specimen> specimenList;

	/**
	 * Constructor original
	 */
	public Catalog() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idCatalog
	 *            Llave primaria que identifica al registro
	 */
	public Catalog(Integer idCatalog) {
		this.idCatalog = idCatalog;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idCatalog
	 *            Llave primaria que identifica al registro
	 * @param catalogName
	 *            Nombre del catálogo
	 */
	public Catalog(Integer idCatalog, String catalogName) {
		this.idCatalog = idCatalog;
		this.catalogName = catalogName;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdCatalog() {
		return idCatalog;
	}

	/**
	 * @param idCatalog
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdCatalog(Integer idCatalog) {
		this.idCatalog = idCatalog;
	}

	/**
	 * @return Nombre del catalogo
	 */
	public String getCatalogName() {
		return catalogName;
	}

	/**
	 * @param catalogName
	 *            Nombre del catalogo a definir
	 */
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
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
	 * @return Referencia a la colección donde está contenido el catálogo
	 */
	public Collection getIdCollection() {
		return idCollection;
	}

	/**
	 * @param idCollection
	 *            Referencia a la colección donde está contenido el catálogo a
	 *            definir
	 */
	public void setIdCollection(Collection idCollection) {
		this.idCollection = idCollection;
	}

	/**
	 * @return Referencia la lista de especímenes del catálogo
	 */
	public List<Specimen> getSpecimenList() {
		return specimenList;
	}

	/**
	 * @param specimenList
	 *            Referencia la lista de especímenes del catálogo a definir
	 */
	public void setSpecimenList(List<Specimen> specimenList) {
		this.specimenList = specimenList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idCatalog != null ? idCatalog.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Catalog)) {
			return false;
		}
		Catalog other = (Catalog) object;
		return !((this.idCatalog == null && other.idCatalog != null)
				|| (this.idCatalog != null && !this.idCatalog.equals(other.idCatalog)));
	}

	@Override
	public String toString() {
		return this.catalogName;
	}
}
