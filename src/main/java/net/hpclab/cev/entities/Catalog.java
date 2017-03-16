package net.hpclab.cev.entities;

import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.enums.StatusEnumConverter;
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
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "catalog")
@NamedQueries({
    @NamedQuery(name = "Catalog.findAll", query = "SELECT c FROM Catalog c")})
@TypeDef(name = "StatusEnumConverter", typeClass = StatusEnumConverter.class)
public class Catalog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "CatalogSeq", sequenceName = "catalog_id_catalog_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CatalogSeq")
    @Basic(optional = false)
    @Column(name = "id_catalog")
    private Integer idCatalog;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "catalog_name")
    private String catalogName;

    @Size(max = 2147483647)
    @Column(name = "status")
    @Type(type = "StatusEnumConverter")
    private StatusEnum status;

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

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public Collection getIdCollection() {
        return idCollection;
    }

    public void setIdCollection(Collection idCollection) {
        this.idCollection = idCollection;
    }

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
        if (!(object instanceof Catalog)) {
            return false;
        }
        Catalog other = (Catalog) object;
        return !((this.idCatalog == null && other.idCatalog != null) || (this.idCatalog != null && !this.idCatalog.equals(other.idCatalog)));
    }

    @Override
    public String toString() {
        return "net.hpclab.entities.Catalog[ idCatalog=" + idCatalog + " ]";
    }
}
