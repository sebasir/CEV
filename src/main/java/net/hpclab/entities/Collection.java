package net.hpclab.entities;

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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "collection")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Collection.findAll", query = "SELECT c FROM Collection c"),
    @NamedQuery(name = "Collection.findByIdCollection", query = "SELECT c FROM Collection c WHERE c.idCollection = :idCollection"),
    @NamedQuery(name = "Collection.findByCollectionName", query = "SELECT c FROM Collection c WHERE c.collectionName = :collectionName"),
    @NamedQuery(name = "Collection.findByCompanyName", query = "SELECT c FROM Collection c WHERE c.companyName = :companyName")
})
public class Collection implements entNaming, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_collection")
    private Integer idCollection;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "collection_name")
    private String collectionName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "company_name")
    private String companyName;
    @OneToMany(mappedBy = "idCollection")
    private List<Catalog> catalogList;

    public Collection() {
    }

    public Collection(Integer idCollection) {
	this.idCollection = idCollection;
    }

    public Collection(Integer idCollection, String collectionName, String companyName) {
	this.idCollection = idCollection;
	this.collectionName = collectionName;
	this.companyName = companyName;
    }

    public Integer getIdCollection() {
	return idCollection;
    }

    public void setIdCollection(Integer idCollection) {
	this.idCollection = idCollection;
    }

    public String getCollectionName() {
	return collectionName;
    }

    public void setCollectionName(String collectionName) {
	this.collectionName = collectionName;
    }

    public String getCompanyName() {
	return companyName;
    }

    public void setCompanyName(String companyName) {
	this.companyName = companyName;
    }

    @XmlTransient
    public List<Catalog> getCatalogList() {
	return catalogList;
    }

    public void setCatalogList(List<Catalog> catalogList) {
	this.catalogList = catalogList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (idCollection != null ? idCollection.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	if (!(object instanceof Collection))
	    return false;
	Collection other = (Collection) object;
	return (this.idCollection != null || other.idCollection == null) && (this.idCollection == null || this.idCollection.equals(other.idCollection));
    }

    @Override
    public String toString() {
	return "net.hpclab.entities.Collection[ idCollection=" + idCollection + " ]";
    }

    @Override
    public String getEntityName() {
	return "Colección";
    }

    @Override
    public String getDescription() {
	return getCollectionName() + " de " + getCompanyName();
    }

}
