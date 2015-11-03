package net.hpclab.entities;

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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "taxonomy")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Taxonomy.findAll", query = "SELECT t FROM Taxonomy t"),
    @NamedQuery(name = "Taxonomy.findByIdTaxonomy", query = "SELECT t FROM Taxonomy t WHERE t.idTaxonomy = :idTaxonomy"),
    @NamedQuery(name = "Taxonomy.findByTaxonomyName", query = "SELECT t FROM Taxonomy t WHERE t.taxonomyName = :taxonomyName"),
    @NamedQuery(name = "Taxonomy.findOrdered", query = "SELECT t FROM Taxonomy t ORDER BY t.idTaxlevel.taxlevelRank DESC")
})
public class Taxonomy implements entNaming, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_taxonomy")
    private Integer idTaxonomy;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "taxonomy_name")
    private String taxonomyName;
    @OneToMany(mappedBy = "idTaxonomy")
    private List<Specimen> specimenList;
    @JoinColumn(name = "id_taxlevel", referencedColumnName = "id_taxlevel")
    @ManyToOne
    private TaxonomyLevel idTaxlevel;
    @OneToMany(mappedBy = "idContainer")
    private List<Taxonomy> taxonomyList;
    @JoinColumn(name = "id_container", referencedColumnName = "id_taxonomy")
    @ManyToOne
    private Taxonomy idContainer;

    public Taxonomy() {
    }

    public Taxonomy(Integer idTaxonomy) {
	this.idTaxonomy = idTaxonomy;
    }

    public Taxonomy(Integer idTaxonomy, String taxonomyName) {
	this.idTaxonomy = idTaxonomy;
	this.taxonomyName = taxonomyName;
    }

    public Integer getIdTaxonomy() {
	return idTaxonomy;
    }

    public void setIdTaxonomy(Integer idTaxonomy) {
	this.idTaxonomy = idTaxonomy;
    }

    public String getTaxonomyName() {
	return taxonomyName;
    }

    public void setTaxonomyName(String taxonomyName) {
	this.taxonomyName = taxonomyName;
    }

    @XmlTransient
    public List<Specimen> getSpecimenList() {
	return specimenList;
    }

    public void setSpecimenList(List<Specimen> specimenList) {
	this.specimenList = specimenList;
    }

    public TaxonomyLevel getIdTaxlevel() {
	return idTaxlevel;
    }

    public void setIdTaxlevel(TaxonomyLevel idTaxlevel) {
	this.idTaxlevel = idTaxlevel;
    }

    @XmlTransient
    public List<Taxonomy> getTaxonomyList() {
	return taxonomyList;
    }

    public void setTaxonomyList(List<Taxonomy> taxonomyList) {
	this.taxonomyList = taxonomyList;
    }

    public Taxonomy getIdContainer() {
	return idContainer;
    }

    public void setIdContainer(Taxonomy idContainer) {
	this.idContainer = idContainer;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (idTaxonomy != null ? idTaxonomy.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	if (!(object instanceof Taxonomy))
	    return false;
	
	Taxonomy other = (Taxonomy) object;
	return (this.idTaxonomy != null || other.idTaxonomy == null) && (this.idTaxonomy == null || this.idTaxonomy.equals(other.idTaxonomy));
    }

    @Override
    public String toString() {
	return "net.hpclab.entities.Taxonomy[ idTaxonomy=" + idTaxonomy + " ]";
    }

    @Override
    public String getEntityName() {
	return "Clasificación";
    }

    @Override
    public String getDescription() {
	return getTaxonomyName();
    }
}
