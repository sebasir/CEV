package net.hpclab.cev.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
@NamedQueries({ @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
		@NamedQuery(name = "Users.authenticate", query = "SELECT u FROM Users u WHERE u.userEmail = :userEmail AND u.userPassword = :userPassword") })
public class Users implements Serializable {

	private static final long serialVersionUID = 8897715219996469489L;
	@Id
	@SequenceGenerator(name = "UsersSeq", sequenceName = "users_id_user_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UsersSeq")
	@Basic(optional = false)
	@Column(name = "id_user")
	private Integer idUser;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "user_id_number")
	private String userIdNumber;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "user_names")
	private String userNames;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "user_lastnames")
	private String userLastnames;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "user_email")
	private String userEmail;
	@Column(name = "user_created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date userCreated;
	@Column(name = "user_modified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date userModified;
	@Column(name = "user_last_login")
	@Temporal(TemporalType.TIMESTAMP)
	private Date userLastLogin;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "user_password")
	private String userPassword;

	@Size(max = 32)
	@Column(name = "status")
	private String status;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "idUser")
	private List<AuditLog> auditLogList;
	@OneToMany(mappedBy = "idUser")
	private List<Author> authorList;
	@OneToMany(mappedBy = "idUser")
	private List<ModulesUsers> modulesUsersList;
	@JoinColumn(name = "id_institution", referencedColumnName = "id_institution")
	@ManyToOne
	private Institution idInstitution;
	@OneToMany(mappedBy = "idUser")
	private List<RolesUsers> rolesUsersList;
	@OneToMany(mappedBy = "idUser")
	private List<Specimen> specimenList;

	public Users() {
	}

	public Users(Integer idUser) {
		this.idUser = idUser;
	}

	public Users(Integer idUser, String userIdNumber, String userNames, String userLastnames, String userEmail,
			String userPassword) {
		this.idUser = idUser;
		this.userIdNumber = userIdNumber;
		this.userNames = userNames;
		this.userLastnames = userLastnames;
		this.userEmail = userEmail;
		this.userPassword = userPassword;
	}

	public Integer getIdUser() {
		return idUser;
	}

	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}

	public String getUserIdNumber() {
		return userIdNumber;
	}

	public void setUserIdNumber(String userIdNumber) {
		this.userIdNumber = userIdNumber;
	}

	public String getUserNames() {
		return userNames;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}

	public String getUserLastnames() {
		return userLastnames;
	}

	public void setUserLastnames(String userLastnames) {
		this.userLastnames = userLastnames;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Date getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(Date userCreated) {
		this.userCreated = userCreated;
	}

	public Date getUserModified() {
		return userModified;
	}

	public void setUserModified(Date userModified) {
		this.userModified = userModified;
	}

	public Date getUserLastLogin() {
		return userLastLogin;
	}

	public void setUserLastLogin(Date userLastLogin) {
		this.userLastLogin = userLastLogin;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
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

	public List<Author> getAuthorList() {
		return authorList;
	}

	public void setAuthorList(List<Author> authorList) {
		this.authorList = authorList;
	}

	public List<ModulesUsers> getModulesUsersList() {
		return modulesUsersList;
	}

	public void setModulesUsersList(List<ModulesUsers> modulesUsersList) {
		this.modulesUsersList = modulesUsersList;
	}

	public Institution getIdInstitution() {
		return idInstitution;
	}

	public void setIdInstitution(Institution idInstitution) {
		this.idInstitution = idInstitution;
	}

	public List<RolesUsers> getRolesUsersList() {
		return rolesUsersList;
	}

	public void setRolesUsersList(List<RolesUsers> rolesUsersList) {
		this.rolesUsersList = rolesUsersList;
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
		hash += (idUser != null ? idUser.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Users)) {
			return false;
		}
		Users other = (Users) object;
		return !((this.idUser == null && other.idUser != null)
				|| (this.idUser != null && !this.idUser.equals(other.idUser)));
	}

	@Override
	public String toString() {
		return this.userNames + " " + userLastnames;
	}
}