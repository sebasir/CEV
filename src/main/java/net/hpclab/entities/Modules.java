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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "modules")
@NamedQueries({
    @NamedQuery(name = "Modules.findAll", query = "SELECT m FROM Modules m")})
public class Modules implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_module")
    private Integer idModule;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "module_name")
    private String moduleName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "module_descr")
    private String moduleDescr;
    @Size(max = 2147483647)
    @Column(name = "status")
    private String status;
    @OneToMany(mappedBy = "idModule")
    private List<AuditLog> auditLogList;
    @OneToMany(mappedBy = "idModule")
    private List<ModulesUsers> modulesUsersList;
    @OneToMany(mappedBy = "idContainer")
    private List<Modules> modulesList;
    @JoinColumn(name = "id_container", referencedColumnName = "id_module")
    @ManyToOne
    private Modules idContainer;
    @OneToMany(mappedBy = "idModule")
    private List<RolesModules> rolesModulesList;

    public Modules() {
    }

    public Modules(Integer idModule) {
        this.idModule = idModule;
    }

    public Modules(Integer idModule, String moduleName, String moduleDescr) {
        this.idModule = idModule;
        this.moduleName = moduleName;
        this.moduleDescr = moduleDescr;
    }

    public Integer getIdModule() {
        return idModule;
    }

    public void setIdModule(Integer idModule) {
        this.idModule = idModule;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleDescr() {
        return moduleDescr;
    }

    public void setModuleDescr(String moduleDescr) {
        this.moduleDescr = moduleDescr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AuditLog> getAuditLogList() {
        return auditLogList;
    }

    public void setAuditLogList(List<AuditLog> auditLogList) {
        this.auditLogList = auditLogList;
    }

    public List<ModulesUsers> getModulesUsersList() {
        return modulesUsersList;
    }

    public void setModulesUsersList(List<ModulesUsers> modulesUsersList) {
        this.modulesUsersList = modulesUsersList;
    }

    public List<Modules> getModulesList() {
        return modulesList;
    }

    public void setModulesList(List<Modules> modulesList) {
        this.modulesList = modulesList;
    }

    public Modules getIdContainer() {
        return idContainer;
    }

    public void setIdContainer(Modules idContainer) {
        this.idContainer = idContainer;
    }

    public List<RolesModules> getRolesModulesList() {
        return rolesModulesList;
    }

    public void setRolesModulesList(List<RolesModules> rolesModulesList) {
        this.rolesModulesList = rolesModulesList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idModule != null ? idModule.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Modules)) {
            return false;
        }
        Modules other = (Modules) object;
        if ((this.idModule == null && other.idModule != null) || (this.idModule != null && !this.idModule.equals(other.idModule))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.hpclab.entities.Modules[ idModule=" + idModule + " ]";
    }
    
}
