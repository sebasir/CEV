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
 * anteriormente para la tabla <tt>USERS</tt> de la base de datos conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */

@Entity
@Table(name = "users")
@NamedQueries({ @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
		@NamedQuery(name = "Users.authenticate", query = "SELECT u FROM Users u WHERE u.userEmail = :userEmail AND u.userPassword = :userPassword") })
public class Users implements Serializable {

	private static final long serialVersionUID = 8897715219996469489L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "UsersSeq", sequenceName = "users_id_user_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UsersSeq")
	@Basic(optional = false)
	@Column(name = "id_user")
	private Integer idUser;

	/**
	 * Numero de identificación del usuario
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "user_id_number")
	private String userIdNumber;

	/**
	 * Nombre del usuario
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "user_names")
	private String userNames;

	/**
	 * Apellidos del usuario
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "user_lastnames")
	private String userLastnames;

	/**
	 * Correo del usuario
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "user_email")
	private String userEmail;

	/**
	 * Fecha de creación de usuario
	 */
	@Column(name = "user_created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date userCreated;

	/**
	 * Fecha de ultima modificacion del usuario
	 */
	@Column(name = "user_modified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date userModified;

	/**
	 * Fecha de último ingreso
	 */
	@Column(name = "user_last_login")
	@Temporal(TemporalType.TIMESTAMP)
	private Date userLastLogin;

	/**
	 * Contraseña del usuario
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "user_password")
	private String userPassword;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia a las acciones del usuario
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "idUser")
	private List<AuditLog> auditLogList;

	/**
	 * Referencia a los autores que el usuario representa
	 */
	@OneToMany(mappedBy = "idUser")
	private List<Author> authorList;

	/**
	 * Referencia a los modulos del usuario
	 */
	@OneToMany(mappedBy = "idUser")
	private List<ModulesUsers> modulesUsersList;

	/**
	 * Referencia a la institución a la que pertenece el usuario
	 */
	@JoinColumn(name = "id_institution", referencedColumnName = "id_institution")
	@ManyToOne
	private Institution idInstitution;

	/**
	 * Referencia a los roles del usuario
	 */
	@OneToMany(mappedBy = "idUser")
	private List<RolesUsers> rolesUsersList;

	/**
	 * Referencia a los especímenes que el usuario creo
	 */
	@OneToMany(mappedBy = "idUser")
	private List<Specimen> specimenList;

	/**
	 * Constructor original
	 */
	public Users() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idUser
	 *            Llave primaria que identifica al registro
	 */
	public Users(Integer idUser) {
		this.idUser = idUser;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idUser
	 *            Llave primaria que identifica al registro
	 * @param userIdNumber
	 *            Numero de identificación del usuario
	 * @param userNames
	 *            Nombre del usuario
	 * @param userLastnames
	 *            Apellidos del usuario
	 * @param userEmail
	 *            Correo del usuario
	 * @param userPassword
	 *            Contraseña del usuario
	 */
	public Users(Integer idUser, String userIdNumber, String userNames, String userLastnames, String userEmail,
			String userPassword) {
		this.idUser = idUser;
		this.userIdNumber = userIdNumber;
		this.userNames = userNames;
		this.userLastnames = userLastnames;
		this.userEmail = userEmail;
		this.userPassword = userPassword;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdUser() {
		return idUser;
	}

	/**
	 * @param idUser
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}

	/**
	 * @return Numero de identificación del usuario
	 */
	public String getUserIdNumber() {
		return userIdNumber;
	}

	/**
	 * @param userIdNumber
	 *            Numero de identificación del usuario a definir
	 */
	public void setUserIdNumber(String userIdNumber) {
		this.userIdNumber = userIdNumber;
	}

	/**
	 * @return Nombre del usuario
	 */
	public String getUserNames() {
		return userNames;
	}

	/**
	 * @param userNames
	 *            Nombre del usuario a definir
	 */
	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}

	/**
	 * @return Apellidos del usuario
	 */
	public String getUserLastnames() {
		return userLastnames;
	}

	/**
	 * @param userLastnames
	 *            Apellidos del usuario a definir
	 */
	public void setUserLastnames(String userLastnames) {
		this.userLastnames = userLastnames;
	}

	/**
	 * @return Correo del usuario
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * @param userEmail
	 *            Correo del usuario a definir
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/**
	 * @return Fecha de creación de usuario
	 */
	public Date getUserCreated() {
		return userCreated;
	}

	/**
	 * @param userCreated
	 *            Fecha de creación de usuario a definir
	 */
	public void setUserCreated(Date userCreated) {
		this.userCreated = userCreated;
	}

	/**
	 * @return Fecha de ultima modificacion del usuario
	 */
	public Date getUserModified() {
		return userModified;
	}

	/**
	 * @param userModified
	 *            Fecha de ultima modificacion del usuario a definir
	 */
	public void setUserModified(Date userModified) {
		this.userModified = userModified;
	}

	/**
	 * @return Fecha de último ingreso
	 */
	public Date getUserLastLogin() {
		return userLastLogin;
	}

	/**
	 * @param userLastLogin
	 *            Fecha de último ingreso a definir
	 */
	public void setUserLastLogin(Date userLastLogin) {
		this.userLastLogin = userLastLogin;
	}

	/**
	 * @return Contraseña del usuario
	 */
	public String getUserPassword() {
		return userPassword;
	}

	/**
	 * @param userPassword
	 *            Contraseña del usuario a definir
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	/**
	 * @return Estado del registro en la base de datos, referenciando a la
	 *         enumeración <tt>StatusEnum</tt>
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            Estado del registro en la base de datos, referenciando a la
	 *            enumeración <tt>StatusEnum</tt> a definir
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return Referencia a las acciones del usuario
	 */
	public List<AuditLog> getAuditLogList() {
		return auditLogList;
	}

	/**
	 * @param auditLogList
	 *            Referencia a las acciones del usuario a definir
	 */
	public void setAuditLogList(List<AuditLog> auditLogList) {
		this.auditLogList = auditLogList;
	}

	/**
	 * @return Referencia a los autores que el usuario representa
	 */
	public List<Author> getAuthorList() {
		return authorList;
	}

	/**
	 * @param authorList
	 *            Referencia a los autores que el usuario representa a definir
	 */
	public void setAuthorList(List<Author> authorList) {
		this.authorList = authorList;
	}

	/**
	 * @return Referencia a los modulos del usuario
	 */
	public List<ModulesUsers> getModulesUsersList() {
		return modulesUsersList;
	}

	/**
	 * @param modulesUsersList
	 *            Referencia a los modulos del usuario a definir
	 */
	public void setModulesUsersList(List<ModulesUsers> modulesUsersList) {
		this.modulesUsersList = modulesUsersList;
	}

	/**
	 * @return Referencia a la institución a la que pertenece el usuario
	 */
	public Institution getIdInstitution() {
		return idInstitution;
	}

	/**
	 * @param idInstitution
	 *            Referencia a la institución a la que pertenece el usuario
	 */
	public void setIdInstitution(Institution idInstitution) {
		this.idInstitution = idInstitution;
	}

	/**
	 * @return Referencia a los roles del usuario
	 */
	public List<RolesUsers> getRolesUsersList() {
		return rolesUsersList;
	}

	/**
	 * @param rolesUsersList
	 *            Referencia a los roles del usuario a definir
	 */
	public void setRolesUsersList(List<RolesUsers> rolesUsersList) {
		this.rolesUsersList = rolesUsersList;
	}

	/**
	 * @return Referencia a los especímenes que el usuario creo
	 */
	public List<Specimen> getSpecimenList() {
		return specimenList;
	}

	/**
	 * @param specimenList
	 *            Referencia a los especímenes que el usuario creo a definir
	 */
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