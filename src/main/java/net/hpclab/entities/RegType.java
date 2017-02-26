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
@Table(name = "reg_type")
@NamedQueries({
    @NamedQuery(name = "RegType.findAll", query = "SELECT r FROM RegType r")})
public class RegType implements Serializable {

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
    @Size(max = 2147483647)
    @Column(name = "status")
    private String status;
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
    
}
