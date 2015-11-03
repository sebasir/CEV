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
@Table(name = "catalog")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Catalog.findAll", query = "SELECT c FROM Catalog c"),
    @NamedQuery(name = "Catalog.findByIdCatalog", query = "SELECT c FROM Catalog c WHERE c.idCatalog = :idCatalog"),
    @NamedQuery(name = "Catalog.findByCatalogName", query = "SELECT c FROM Catalog c WHERE c.catalogName = :catalogName")})
public class Catalog implements entNaming, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_catalog")
    private Integer idCatalog;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "catalog_name")
    private String catalogName;
    @JoinColumn(name = "id_collection", referencedColumnName = "id_collection")
    @ManyToOne
    private Collection idCollection;
    @OneToMany(mappedBy = "idCatalog")
    private List<Specimen> specimenList;

    public Catalog() {
    }

    public Catalog(Integer idCatalog) {
	this.idCatalog = idCatalog;
    }

    public Catalog(Integer idCatalog, String catalogName) {
	this.idCatalog = idCatalog;
	this.catalogName = catalogName;
    }

    public Integer getIdCatalog() {
	return idCatalog;
    }

    public void setIdCatalog(Integer idCatalog) {
	this.idCatalog = idCatalog;
    }

    public String getCatalogName() {
	return catalogName;
    }

    public void setCatalogName(String catalogName) {
	this.catalogName = catalogName;
    }

    public Collection getIdCollection() {
	return idCollection;
    }

    public void setIdCollection(Collection idCollection) {
	this.idCollection = idCollection;
    }

    @XmlTransient
    public List<Specimen> getSpecimenList() {
	return specimenList;
    }

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
	if (!(object instanceof Catalog))
	    return false;

	Catalog other = (Catalog) object;
	return (this.idCatalog != null || other.idCatalog == null) && (this.idCatalog == null || this.idCatalog.equals(other.idCatalog));
    }

    @Override
    public String toString() {
	return "net.hpclab.entities.Catalog[ idCatalog=" + idCatalog + " ]";
    }

    @Override
    public String getEntityName() {
	return "Catálogo";
    }

    @Override
    public String getDescription() {
	return getCatalogName();
    }
}
