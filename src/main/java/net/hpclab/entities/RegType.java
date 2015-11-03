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
@Table(name = "reg_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegType.findAll", query = "SELECT r FROM RegType r"),
    @NamedQuery(name = "RegType.findByIdRety", query = "SELECT r FROM RegType r WHERE r.idRety = :idRety"),
    @NamedQuery(name = "RegType.findByRetyName", query = "SELECT r FROM RegType r WHERE r.retyName = :retyName")})
public class RegType implements entNaming, Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_rety")
    private Integer idRety;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "rety_name")
    private String retyName;
    @OneToMany(mappedBy = "idRety")
    private List<Specimen> specimenList;

    public RegType() {
    }

    public RegType(Integer idRety) {
	   this.idRety = idRety;
    }

    public RegType(Integer idRety, String retyName) {
	   this.idRety = idRety;
	   this.retyName = retyName;
    }

    public Integer getIdRety() {
	   return idRety;
    }

    public void setIdRety(Integer idRety) {
	   this.idRety = idRety;
    }

    public String getRetyName() {
	   return retyName;
    }

    public void setRetyName(String retyName) {
	   this.retyName = retyName;
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
	   hash += (idRety != null ? idRety.hashCode() : 0);
	   return hash;
    }

    @Override
    public boolean equals(Object object) {
	   // TODO: Warning - this method won't work in the case the id fields are not set
	   if (!(object instanceof RegType)) {
		  return false;
	   }
	   RegType other = (RegType) object;
	   if ((this.idRety == null && other.idRety != null) || (this.idRety != null && !this.idRety.equals(other.idRety))) {
		  return false;
	   }
	   return true;
    }

    @Override
    public String toString() {
	   return "net.hpclab.entities.RegType[ idRety=" + idRety + " ]";
    }

    @Override
    public String getEntityName() {
	   return "Tipo de Registro";
    }

    @Override
    public String getDescription() {
	   return getRetyName();
    }
}
