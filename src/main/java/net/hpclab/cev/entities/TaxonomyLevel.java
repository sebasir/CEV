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

@Entity
@Table(name = "taxonomy_level")
@NamedQueries({ @NamedQuery(name = "TaxonomyLevel.findAll", query = "SELECT t FROM TaxonomyLevel t") })
public class TaxonomyLevel implements Serializable {

	private static final long serialVersionUID = 6196505189712738658L;
	@Id
	@SequenceGenerator(name = "TaxonomyLevelSeq", sequenceName = "taxonomy_level_id_taxlevel_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TaxonomyLevelSeq")
	@Basic(optional = false)
	@Column(name = "id_taxlevel")
	private Integer idTaxlevel;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "taxlevel_name")
	private String taxlevelName;
	@Basic(optional = false)
	@NotNull
	@Column(name = "taxlevel_rank")
	private int taxlevelRank;

	@Size(max = 32)
	@Column(name = "status")
	private String status;

	@OneToMany(mappedBy = "idTaxlevel")
	private List<Taxonomy> taxonomyList;

	public TaxonomyLevel() {
	}

	public TaxonomyLevel(Integer idTaxlevel) {
		this.idTaxlevel = idTaxlevel;
	}

	public TaxonomyLevel(Integer idTaxlevel, String taxlevelName, int taxlevelRank) {
		this.idTaxlevel = idTaxlevel;
		this.taxlevelName = taxlevelName;
		this.taxlevelRank = taxlevelRank;
	}

	public Integer getIdTaxlevel() {
		return idTaxlevel;
	}

	public void setIdTaxlevel(Integer idTaxlevel) {
		this.idTaxlevel = idTaxlevel;
	}

	public String getTaxlevelName() {
		return taxlevelName;
	}

	public void setTaxlevelName(String taxlevelName) {
		this.taxlevelName = taxlevelName;
	}

	public int getTaxlevelRank() {
		return taxlevelRank;
	}

	public void setTaxlevelRank(int taxlevelRank) {
		this.taxlevelRank = taxlevelRank;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Taxonomy> getTaxonomyList() {
		return taxonomyList;
	}

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
