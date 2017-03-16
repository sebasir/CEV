package net.hpclab.cev.entities;

import net.hpclab.cev.enums.AuditEnum;
import net.hpclab.cev.enums.AuditEnumConverter;
import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "audit_log")
@NamedQueries({
    @NamedQuery(name = "AuditLog.findAll", query = "SELECT a FROM AuditLog a")})
@TypeDef(name = "AuditEnumConverter", typeClass = AuditEnumConverter.class)
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "AuditLogSeq", sequenceName = "audit_log_id_aulog_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AuditLogSeq")
    @Column(name = "id_aulog")
    private Integer idAulog;
    @Column(name = "aulog_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date aulogTime;
    @Column(name = "aulog_ip_address")
    private String aulogIpAddress;

    @Column(name = "aulog_action")
    @Type(type = "AuditEnumConverter")
    private AuditEnum aulogAction;

    @Column(name = "aulog_target")
    private String aulogTarget;
    @JoinColumn(name = "id_module", referencedColumnName = "id_module")
    @ManyToOne
    private Modules idModule;
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    @ManyToOne(optional = false)
    private Users idUser;

    public AuditLog() {
    }

    public AuditLog(Integer idAulog) {
        this.idAulog = idAulog;
    }

    public AuditLog(Integer idAulog, String aulogTarget) {
        this.idAulog = idAulog;
        this.aulogTarget = aulogTarget;
    }

    public Integer getIdAulog() {
        return idAulog;
    }

    public void setIdAulog(Integer idAulog) {
        this.idAulog = idAulog;
    }

    public Date getAulogTime() {
        return aulogTime;
    }

    public void setAulogTime(Date aulogTime) {
        this.aulogTime = aulogTime;
    }

    public String getAulogIpAddress() {
        return aulogIpAddress;
    }

    public void setAulogIpAddress(String aulogIpAddress) {
        this.aulogIpAddress = aulogIpAddress;
    }

    public AuditEnum getAulogAction() {
        return aulogAction;
    }

    public void setAulogAction(AuditEnum aulogAction) {
        this.aulogAction = aulogAction;
    }

    public String getAulogTarget() {
        return aulogTarget;
    }

    public void setAulogTarget(String aulogTarget) {
        this.aulogTarget = aulogTarget;
    }

    public Modules getIdModule() {
        return idModule;
    }

    public void setIdModule(Modules idModule) {
        this.idModule = idModule;
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
        hash += (idAulog != null ? idAulog.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AuditLog)) {
            return false;
        }
        AuditLog other = (AuditLog) object;
        return !((this.idAulog == null && other.idAulog != null) || (this.idAulog != null && !this.idAulog.equals(other.idAulog)));
    }

    @Override
    public String toString() {
        return "net.hpclab.entities.AuditLog[ idAulog=" + idAulog + " ]";
    }
}
