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
@Table(name = "modules")
@NamedQueries({
    @NamedQuery(name = "Modules.findAll", query = "SELECT m FROM Modules m")})
@TypeDef(name = "StatusEnumConverter", typeClass = StatusEnumConverter.class)
public class Modules implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "ModulesSeq", sequenceName = "modules_id_module_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ModulesSeq")
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
    @Type(type = "StatusEnumConverter")
    private StatusEnum status;

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

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
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
        if (!(object instanceof Modules)) {
            return false;
        }
        Modules other = (Modules) object;
        return !((this.idModule == null && other.idModule != null) || (this.idModule != null && !this.idModule.equals(other.idModule)));
    }

    @Override
    public String toString() {
        return "net.hpclab.entities.Modules[ idModule=" + idModule + " ]";
    }
}
