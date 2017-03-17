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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "roles_users")
@NamedQueries({
    @NamedQuery(name = "RolesUsers.findAll", query = "SELECT r FROM RolesUsers r")
    ,
    @NamedQuery(name = "RolesUsers.findByKey", query = "SELECT r FROM RolesUsers r WHERE r.idRole.idRole = :idRole AND r.idUser.idUser = :idUser")})
@TypeDef(name = "StatusEnumConverter", typeClass = StatusEnumConverter.class)
public class RolesUsers implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "RolesUsersSeq", sequenceName = "roles_users_id_rous_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RolesUsersSeq")
    @Basic(optional = false)
    @Column(name = "id_rous")
    private Integer idRous;
    @Basic(optional = false)
    @NotNull
    @Column(name = "access_level")
    private int accessLevel;

    @Size(max = 2147483647)
    @Column(name = "status")
    @Type(type = "StatusEnumConverter")
    private StatusEnum status;

    @JoinColumn(name = "id_role", referencedColumnName = "id_role")
    @ManyToOne
    private Roles idRole;
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    @ManyToOne
    private Users idUser;

    public RolesUsers() {
    }

    public RolesUsers(Integer idRous) {
        this.idRous = idRous;
    }

    public RolesUsers(Integer idRous, int accessLevel) {
        this.idRous = idRous;
        this.accessLevel = accessLevel;
    }

    public Integer getIdRous() {
        return idRous;
    }

    public void setIdRous(Integer idRous) {
        this.idRous = idRous;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public Roles getIdRole() {
        return idRole;
    }

    public void setIdRole(Roles idRole) {
        this.idRole = idRole;
    }

    public Users getIdUser() {
        return idUser;
    }

    public void setIdUser(Users idUser) {
        this.idUser = idUser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRous != null ? idRous.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RolesUsers)) {
            return false;
        }
        RolesUsers other = (RolesUsers) object;
        return !((this.idRous == null && other.idRous != null) || (this.idRous != null && !this.idRous.equals(other.idRous)));
    }

    @Override
    public String toString() {
        return "net.hpclab.entities.RolesUsers[ idRous=" + idRous + " ]";
    }
}