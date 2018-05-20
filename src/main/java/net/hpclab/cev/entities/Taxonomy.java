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
import javax.persistence.CascadeType;
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
import javax.persistence.OneToOne;
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
 * anteriormente para la tabla <tt>TAXONOMY</tt> de la base de datos conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */

@Entity
@Table(name = "taxonomy")
@NamedQueries({ @NamedQuery(name = "Taxonomy.findAll", query = "SELECT t FROM Taxonomy t"),
		@NamedQuery(name = "Taxonomy.findOrderedAsc", query = "SELECT t FROM Taxonomy t ORDER BY t.idTaxlevel.taxlevelRank") })
public class Taxonomy implements Serializable {

	private static final long serialVersionUID = -1458545746427975715L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "TaxonomySeq", sequenceName = "taxonomy_id_taxonomy_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TaxonomySeq")
	@Basic(optional = false)
	@Column(name = "id_taxonomy")
	private Integer idTaxonomy;

	/**
	 * Nombre de la clasificación taxonómica
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "taxonomy_name")
	private String taxonomyName;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia a las clasificaciones taxnomicas que contiente esta
	 */
	@OneToMany(mappedBy = "idContainer")
	private List<Taxonomy> taxonomyList;

	/**
	 * Referencia a la clasificación taxonómica que la contiene
	 */
	@JoinColumn(name = "id_container", referencedColumnName = "id_taxonomy")
	@ManyToOne(cascade = { CascadeType.PERSIST })
	private Taxonomy idContainer;

	/**
	 * Referencia al nivel de clasificación
	 */
	@JoinColumn(name = "id_taxlevel", referencedColumnName = "id_taxlevel")
	@ManyToOne
	private TaxonomyLevel idTaxlevel;

	/**
	 * Referencia al espécimen que identifica esta
	 */
	@OneToOne(mappedBy = "idTaxonomy")
	private Specimen specimen;

	/**
	 * Constructor original
	 */
	public Taxonomy() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idTaxonomy
	 *            Llave primaria que identifica al registro
	 */
	public Taxonomy(Integer idTaxonomy) {
		this.idTaxonomy = idTaxonomy;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idTaxonomy
	 *            Llave primaria que identifica al registro
	 * @param taxonomyName
	 *            Nombre de la clasificación taxonómica
	 */
	public Taxonomy(Integer idTaxonomy, String taxonomyName) {
		this.idTaxonomy = idTaxonomy;
		this.taxonomyName = taxonomyName;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdTaxonomy() {
		return idTaxonomy;
	}

	/**
	 * @param idTaxonomy
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdTaxonomy(Integer idTaxonomy) {
		this.idTaxonomy = idTaxonomy;
	}

	/**
	 * @return Nombre de la clasificación taxonómica
	 */
	public String getTaxonomyName() {
		return taxonomyName;
	}

	/**
	 * @param taxonomyName
	 *            Nombre de la clasificación taxonómica a definir
	 */
	public void setTaxonomyName(String taxonomyName) {
		this.taxonomyName = taxonomyName;
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
	 * @return Referencia a las clasificaciones taxnomicas que contiente esta
	 */
	public List<Taxonomy> getTaxonomyList() {
		return taxonomyList;
	}

	/**
	 * @param taxonomyList
	 *            Referencia a las clasificaciones taxnomicas que contiente esta a
	 *            definir
	 */
	public void setTaxonomyList(List<Taxonomy> taxonomyList) {
		this.taxonomyList = taxonomyList;
	}

	/**
	 * @return Referencia a la clasificación taxonómica que la contiene
	 */
	public Taxonomy getIdContainer() {
		return idContainer;
	}

	/**
	 * @param idContainer
	 *            Referencia a la clasificación taxonómica que la contiene a definir
	 */
	public void setIdContainer(Taxonomy idContainer) {
		this.idContainer = idContainer;
	}

	/**
	 * @return Referencia al nivel de clasificación
	 */
	public TaxonomyLevel getIdTaxlevel() {
		return idTaxlevel;
	}

	/**
	 * @param idTaxlevel
	 *            Referencia al nivel de clasificación a definir
	 */
	public void setIdTaxlevel(TaxonomyLevel idTaxlevel) {
		this.idTaxlevel = idTaxlevel;
	}

	/**
	 * @return Referencia al espécimen que identifica esta
	 */
	public Specimen getSpecimen() {
		return specimen;
	}

	/**
	 * @param specimen
	 *            Referencia al espécimen que identifica esta a definir
	 */
	public void setSpecimen(Specimen specimen) {
		this.specimen = specimen;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idTaxonomy != null ? idTaxonomy.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Taxonomy)) {
			return false;
		}
		Taxonomy other = (Taxonomy) object;
		return !((this.idTaxonomy == null && other.idTaxonomy != null)
				|| (this.idTaxonomy != null && !this.idTaxonomy.equals(other.idTaxonomy)));
	}

	@Override
	public String toString() {
		return this.taxonomyName;
	}
}
