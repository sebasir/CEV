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
 * anteriormente para la tabla <tt>TAXONOMY_LEVEL</tt> de la base de datos
 * conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */

@Entity
@Table(name = "taxonomy_level")
@NamedQueries({ @NamedQuery(name = "TaxonomyLevel.findAll", query = "SELECT t FROM TaxonomyLevel t") })
public class TaxonomyLevel implements Serializable {

	private static final long serialVersionUID = 6196505189712738658L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "TaxonomyLevelSeq", sequenceName = "taxonomy_level_id_taxlevel_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TaxonomyLevelSeq")
	@Basic(optional = false)
	@Column(name = "id_taxlevel")
	private Integer idTaxlevel;

	/**
	 * Nombre del nivel de clasificación
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "taxlevel_name")
	private String taxlevelName;

	/**
	 * Nivel de clasificación
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "taxlevel_rank")
	private int taxlevelRank;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia a las clasificaciones de este nivel
	 */
	@OneToMany(mappedBy = "idTaxlevel")
	private List<Taxonomy> taxonomyList;

	/**
	 * Constructor original
	 */
	public TaxonomyLevel() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idTaxlevel
	 *            Llave primaria que identifica al registro
	 */
	public TaxonomyLevel(Integer idTaxlevel) {
		this.idTaxlevel = idTaxlevel;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idTaxlevel
	 *            Llave primaria que identifica al registro
	 * @param taxlevelName
	 *            Nombre del nivel de clasificación
	 * @param taxlevelRank
	 *            Nivel de clasificación
	 */
	public TaxonomyLevel(Integer idTaxlevel, String taxlevelName, int taxlevelRank) {
		this.idTaxlevel = idTaxlevel;
		this.taxlevelName = taxlevelName;
		this.taxlevelRank = taxlevelRank;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdTaxlevel() {
		return idTaxlevel;
	}

	/**
	 * @param idTaxlevel
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdTaxlevel(Integer idTaxlevel) {
		this.idTaxlevel = idTaxlevel;
	}

	/**
	 * @return Nombre del nivel de clasificación
	 */
	public String getTaxlevelName() {
		return taxlevelName;
	}

	/**
	 * @param taxlevelName
	 *            Nombre del nivel de clasificación a definir
	 */
	public void setTaxlevelName(String taxlevelName) {
		this.taxlevelName = taxlevelName;
	}

	/**
	 * @return Nivel de clasificación
	 */
	public int getTaxlevelRank() {
		return taxlevelRank;
	}

	/**
	 * @param taxlevelRank
	 *            Nivel de clasificación
	 */
	public void setTaxlevelRank(int taxlevelRank) {
		this.taxlevelRank = taxlevelRank;
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
	 * @return Referencia a las clasificaciones de este nivel
	 */
	public List<Taxonomy> getTaxonomyList() {
		return taxonomyList;
	}

	/**
	 * @param taxonomyList
	 *            Referencia a las clasificaciones de este nivel a definir
	 */
	public void setTaxonomyList(List<Taxonomy> taxonomyList) {
		this.taxonomyList = taxonomyList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idTaxlevel != null ? idTaxlevel.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof TaxonomyLevel)) {
			return false;
		}
		TaxonomyLevel other = (TaxonomyLevel) object;
		return !((this.idTaxlevel == null && other.idTaxlevel != null)
				|| (this.idTaxlevel != null && !this.idTaxlevel.equals(other.idTaxlevel)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.TaxonomyLevel[ idTaxlevel=" + idTaxlevel + " ]";
	}
}
