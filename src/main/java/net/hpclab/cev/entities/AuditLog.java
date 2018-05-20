/*
 * Colección Entomológica Virtual
 * Universidad Central
 * High Performance Computing Laboratory
 * Grupo COMMONS.
 * 
 * Sebastián Motavita Medellín
 * 
 * 2017 - 2018
 */

package net.hpclab.cev.entities;

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
import javax.validation.constraints.Size;

/**
 * Entidad mapeada desde el servicio de ORM usado y que homologa la tabla que
 * referencia en la anotación <tt>Table</tt>, a través de la opción de generar
 * entidades a partir de una conexión desde un motor JPA. Esta clase es un POJO
 * (Plain Old Java Object) extendido para su uso en administradores de entidades
 * JPA, y que permiten realizar operaciones como <tt>persist</tt>,
 * <tt>merge</tt>, <tt>delete</tt>, desde un proveedor de persistencia.
 * 
 * <p>
 * Esta entidad en particular, permite homologar las operaciones citadas
 * anteriormente para la tabla <tt>AUDIT_LOG</tt> de la base de datos conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */

@Entity
@Table(name = "audit_log")
@NamedQueries({ @NamedQuery(name = "AuditLog.findAll", query = "SELECT a FROM AuditLog a") })
public class AuditLog implements Serializable {

	private static final long serialVersionUID = 3929521724459965933L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "AuditLogSeq", sequenceName = "audit_log_id_aulog_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AuditLogSeq")
	@Column(name = "id_aulog")
	private Integer idAulog;

	/**
	 * Fecha en la que se realiza una operación
	 */
	@Column(name = "aulog_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date aulogTime;

	/**
	 * Dirección IP con la que un usuario realiza alguna operación
	 */
	@Column(name = "aulog_ip_address")
	private String aulogIpAddress;

	/**
	 * Operación realizada, y que deriva desde la enumeración <tt>AuditEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "aulog_action")
	private String aulogAction;

	/**
	 * Tabla objetivo de la operación que se registra
	 */
	@Column(name = "aulog_target")
	private String aulogTarget;

	/**
	 * Módulo que referencia la operación
	 */
	@JoinColumn(name = "id_module", referencedColumnName = "id_module")
	@ManyToOne
	private Modules idModule;

	/**
	 * Usuario que realiza la operación
	 */
	@JoinColumn(name = "id_user", referencedColumnName = "id_user")
	@ManyToOne(optional = false)
	private Users idUser;

	/**
	 * Constructor original
	 */
	public AuditLog() {
	}

	/**
	 * Contructor con argumentos de llave primaria
	 * 
	 * @param idAulog
	 *            Llave primaria que identifica al registro
	 */
	public AuditLog(Integer idAulog) {
		this.idAulog = idAulog;
	}

	/**
	 * Contructor con argumentos de llave primaria y el objetivo de la operación
	 * 
	 * @param idAulog
	 *            Llave primaria que identifica al registro
	 * @param aulogTarget
	 *            Objetivo de la operación
	 */
	public AuditLog(Integer idAulog, String aulogTarget) {
		this.idAulog = idAulog;
		this.aulogTarget = aulogTarget;
	}

	/**
	 * @return La llave primaria del objeto
	 */
	public Integer getIdAulog() {
		return idAulog;
	}

	/**
	 * @param idAulog
	 *            Llave primaria a definir
	 */
	public void setIdAulog(Integer idAulog) {
		this.idAulog = idAulog;
	}

	/**
	 * @return Fecha de la operación
	 */
	public Date getAulogTime() {
		return aulogTime;
	}

	/**
	 * @param aulogTime
	 *            Fecha de la operación a definir
	 */
	public void setAulogTime(Date aulogTime) {
		this.aulogTime = aulogTime;
	}

	/**
	 * @return Dirección IP con la que un usuario realiza alguna operación
	 */
	public String getAulogIpAddress() {
		return aulogIpAddress;
	}

	/**
	 * @param aulogIpAddress
	 *            Dirección IP a definir con la que un usuario realiza alguna
	 *            operación
	 */
	public void setAulogIpAddress(String aulogIpAddress) {
		this.aulogIpAddress = aulogIpAddress;
	}

	/**
	 * @return Operación realizada, y que deriva desde la enumeración
	 *         <tt>AuditEnum</tt>
	 */
	public String getAulogAction() {
		return aulogAction;
	}

	/**
	 * @param aulogAction
	 *            Operación realizada a definir, y que deriva desde la enumeración
	 *            <tt>AuditEnum</tt>
	 */
	public void setAulogAction(String aulogAction) {
		this.aulogAction = aulogAction;
	}

	/**
	 * @return Tabla objetivo de la operación que se registra
	 */
	public String getAulogTarget() {
		return aulogTarget;
	}

	/**
	 * @param aulogTarget
	 *            Tabla objetivo de la operación que se registraa definir
	 */
	public void setAulogTarget(String aulogTarget) {
		this.aulogTarget = aulogTarget;
	}

	/**
	 * @return Módulo que referencia la operación
	 */
	public Modules getIdModule() {
		return idModule;
	}

	/**
	 * @param idModule
	 *            Módulo que referencia la operación a definir
	 */
	public void setIdModule(Modules idModule) {
		this.idModule = idModule;
	}

	/**
	 * @return Usuario que realiza la operación
	 */
	public Users getIdUser() {
		return idUser;
	}

	/**
	 * @param idUser
	 *            Usuario que realiza la operación a definir
	 */
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
		return !((this.idAulog == null && other.idAulog != null)
				|| (this.idAulog != null && !this.idAulog.equals(other.idAulog)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.AuditLog[ idAulog=" + idAulog + " ]";
	}
}
