package net.hpclab.cev.entities;

import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.enums.StatusEnumConverter;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "roles_modules")
@NamedQueries({
    @NamedQuery(name = "RolesModules.findAll", query = "SELECT r FROM RolesModules r")
    ,
    @NamedQuery(name = "RolesModules.findByKey", query = "SELECT r FROM RolesModules r WHERE r.idModule.idModule = :idModule AND r.idRole.idRole = :idRole")})
@TypeDef(name = "StatusEnumConverter", typeClass = StatusEnumConverter.class)
public class RolesModules implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "RolesModulesSeq", sequenceName = "roles_modules_id_romo_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RolesModulesSeq")
    @Basic(optional = false)
    @Column(name = "id_romo")
    private Integer idRomo;

    @Size(max = 2147483647)
    @Column(name = "status")
    @Type(type = "StatusEnumConverter")
    private StatusEnum status;

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

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
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
        if (!(object instanceof RolesModules)) {
            return false;
        }
        RolesModules other = (RolesModules) object;
        return !((this.idRomo == null && other.idRomo != null) || (this.idRomo != null && !this.idRomo.equals(other.idRomo)));
    }

    @Override
    public String toString() {
        return "net.hpclab.entities.RolesModules[ idRomo=" + idRomo + " ]";
    }
}
