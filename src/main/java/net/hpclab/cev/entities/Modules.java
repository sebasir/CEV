package net.hpclab.cev.entities;

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

@Entity
@Table(name = "modules")
@NamedQueries({
    @NamedQuery(name = "Modules.findAll", query = "SELECT m FROM Modules m")
    , @NamedQuery(name = "Modules.findByIdModule", query = "SELECT m FROM Modules m WHERE m.idModule = :idModule")
    , @NamedQuery(name = "Modules.findByModuleName", query = "SELECT m FROM Modules m WHERE m.moduleName = :moduleName")
    , @NamedQuery(name = "Modules.findByModuleDescr", query = "SELECT m FROM Modules m WHERE m.moduleDescr = :moduleDescr")
    , @NamedQuery(name = "Modules.findByModuleOrder", query = "SELECT m FROM Modules m WHERE m.moduleOrder = :moduleOrder")
    , @NamedQuery(name = "Modules.findByModulePage", query = "SELECT m FROM Modules m WHERE m.modulePage = :modulePage")
    , @NamedQuery(name = "Modules.findByModuleIcon", query = "SELECT m FROM Modules m WHERE m.moduleIcon = :moduleIcon")
    , @NamedQuery(name = "Modules.findByStatus", query = "SELECT m FROM Modules m WHERE m.status = :status")})
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
    @Basic(optional = false)
    @NotNull
    @Column(name = "module_order")
    private int moduleOrder;
    @Size(max = 32)
    @Column(name = "module_page")
    private String modulePage;
    @Size(max = 16)
    @Column(name = "module_icon")
    private String moduleIcon;
    @Size(max = 32)
    @Column(name = "status")
    private String status;
    @OneToMany(mappedBy = "idContainer")
    private List<Modules> modulesList;
    @JoinColumn(name = "id_container", referencedColumnName = "id_module")
    @ManyToOne
    private Modules idContainer;

    public Modules() {
    }

    public Modules(Integer idModule) {
        this.idModule = idModule;
    }

    public Modules(Integer idModule, String moduleName, String moduleDescr, int moduleOrder) {
        this.idModule = idModule;
        this.moduleName = moduleName;
        this.moduleDescr = moduleDescr;
        this.moduleOrder = moduleOrder;
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

    public int getModuleOrder() {
        return moduleOrder;
    }

    public void setModuleOrder(int moduleOrder) {
        this.moduleOrder = moduleOrder;
    }

    public String getModulePage() {
        return modulePage;
    }

    public void setModulePage(String modulePage) {
        this.modulePage = modulePage;
    }

    public String getModuleIcon() {
        return moduleIcon;
    }

    public void setModuleIcon(String moduleIcon) {
        this.moduleIcon = moduleIcon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        return !((this.idModule == null && other.idModule != null) || (this.idModule != null && !this.idModule.equals(other.idModule)));
    }

    @Override
    public String toString() {
        return "net.hpclab.cev.entities.Modules[ idModule=" + idModule + " ]";
    }

}
