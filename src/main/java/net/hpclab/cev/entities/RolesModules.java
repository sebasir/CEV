/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hpclab.cev.entities;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author Sebasir
 */
@Entity
@Table(name = "roles_modules")
@NamedQueries({
    @NamedQuery(name = "RolesModules.findAll", query = "SELECT r FROM RolesModules r"),
    @NamedQuery(name = "RolesModules.findByKey", query = "SELECT r FROM RolesModules r WHERE r.idModule.idModule = :idModule AND r.idRole.idRole = :idRole")})
public class RolesModules implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_romo")
    private Integer idRomo;
    @Size(max = 2147483647)
    @Column(name = "status")
    private String status;
    @JoinColumn(name = "id_module", referencedColumnName = "id_module")
    @ManyToOne
    private Modules idModule;
    @JoinColumn(name = "id_role", referencedColumnName = "id_role")
    @ManyToOne
    private Roles idRole;

    public RolesModules() {
    }

    public RolesModules(Integer idRomo) {
        this.idRomo = idRomo;
    }

    public Integer getIdRomo() {
        return idRomo;
    }

    public void setIdRomo(Integer idRomo) {
        this.idRomo = idRomo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Modules getIdModule() {
        return idModule;
    }

    public void setIdModule(Modules idModule) {
        this.idModule = idModule;
    }

    public Roles getIdRole() {
        return idRole;
    }

    public void setIdRole(Roles idRole) {
        this.idRole = idRole;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRomo != null ? idRomo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RolesModules)) {
            return false;
        }
        RolesModules other = (RolesModules) object;
        if ((this.idRomo == null && other.idRomo != null) || (this.idRomo != null && !this.idRomo.equals(other.idRomo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.hpclab.entities.RolesModules[ idRomo=" + idRomo + " ]";
    }
    
}
