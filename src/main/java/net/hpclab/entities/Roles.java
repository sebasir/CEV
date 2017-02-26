/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hpclab.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Sebasir
 */
@Entity
@Table(name = "roles")
@NamedQueries({
    @NamedQuery(name = "Roles.findAll", query = "SELECT r FROM Roles r")})
public class Roles implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_role")
    private Integer idRole;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "role_name")
    private String roleName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "role_descr")
    private String roleDescr;
    @Column(name = "role_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date roleCreated;
    @Column(name = "role_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date roleModified;
    @Size(max = 2147483647)
    @Column(name = "status")
    private String status;
    @OneToMany(mappedBy = "idRole")
    private List<RolesModules> rolesModulesList;
    @OneToMany(mappedBy = "idRole")
    private List<RolesUsers> rolesUsersList;

    public Roles() {
    }

    public Roles(Integer idRole) {
        this.idRole = idRole;
    }

    public Roles(Integer idRole, String roleName, String roleDescr) {
        this.idRole = idRole;
        this.roleName = roleName;
        this.roleDescr = roleDescr;
    }

    public Integer getIdRole() {
        return idRole;
    }

    public void setIdRole(Integer idRole) {
        this.idRole = idRole;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDescr() {
        return roleDescr;
    }

    public void setRoleDescr(String roleDescr) {
        this.roleDescr = roleDescr;
    }

    public Date getRoleCreated() {
        return roleCreated;
    }

    public void setRoleCreated(Date roleCreated) {
        this.roleCreated = roleCreated;
    }

    public Date getRoleModified() {
        return roleModified;
    }

    public void setRoleModified(Date roleModified) {
        this.roleModified = roleModified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RolesModules> getRolesModulesList() {
        return rolesModulesList;
    }

    public void setRolesModulesList(List<RolesModules> rolesModulesList) {
        this.rolesModulesList = rolesModulesList;
    }

    public List<RolesUsers> getRolesUsersList() {
        return rolesUsersList;
    }

    public void setRolesUsersList(List<RolesUsers> rolesUsersList) {
        this.rolesUsersList = rolesUsersList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRole != null ? idRole.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Roles)) {
            return false;
        }
        Roles other = (Roles) object;
        if ((this.idRole == null && other.idRole != null) || (this.idRole != null && !this.idRole.equals(other.idRole))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.hpclab.entities.Roles[ idRole=" + idRole + " ]";
    }
    
}
