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
@Table(name = "sample_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SampleType.findAll", query = "SELECT s FROM SampleType s"),
    @NamedQuery(name = "SampleType.findByIdSaty", query = "SELECT s FROM SampleType s WHERE s.idSaty = :idSaty"),
    @NamedQuery(name = "SampleType.findBySatyName", query = "SELECT s FROM SampleType s WHERE s.satyName = :satyName")})
public class SampleType implements entNaming, Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_saty")
    private Integer idSaty;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "saty_name")
    private String satyName;
    @OneToMany(mappedBy = "idSaty")
    private List<Specimen> specimenList;

    public SampleType() {
    }

    public SampleType(Integer idSaty) {
	   this.idSaty = idSaty;
    }

    public SampleType(Integer idSaty, String satyName) {
	   this.idSaty = idSaty;
	   this.satyName = satyName;
    }

    public Integer getIdSaty() {
	   return idSaty;
    }

    public void setIdSaty(Integer idSaty) {
	   this.idSaty = idSaty;
    }

    public String getSatyName() {
	   return satyName;
    }

    public void setSatyName(String satyName) {
	   this.satyName = satyName;
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
	   hash += (idSaty != null ? idSaty.hashCode() : 0);
	   return hash;
    }

    @Override
    public boolean equals(Object object) {
	   if (!(object instanceof SampleType)) {
		  return false;
	   }
	   SampleType other = (SampleType) object;
	   return (this.idSaty != null || other.idSaty == null) && (this.idSaty == null || this.idSaty.equals(other.idSaty));
    }

    @Override
    public String toString() {
	   return "net.hpclab.entities.SampleType[ idSaty=" + idSaty + " ]";
    }

    @Override
    public String getEntityName() {
	   return "Tipo de Muestra";
    }

    @Override
    public String getDescription() {
	   return getSatyName();
    }
}
