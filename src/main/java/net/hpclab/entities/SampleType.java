/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author Sebasir
 */
@Entity
@Table(name = "sample_type")
@NamedQueries({
    @NamedQuery(name = "SampleType.findAll", query = "SELECT s FROM SampleType s")})
public class SampleType implements Serializable {

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
    @Size(max = 2147483647)
    @Column(name = "status")
    private String status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        hash += (idSaty != null ? idSaty.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SampleType)) {
            return false;
        }
        SampleType other = (SampleType) object;
        if ((this.idSaty == null && other.idSaty != null) || (this.idSaty != null && !this.idSaty.equals(other.idSaty))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.hpclab.entities.SampleType[ idSaty=" + idSaty + " ]";
    }
    
}
