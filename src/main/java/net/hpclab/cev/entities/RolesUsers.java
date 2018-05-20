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
 * anteriormente para la tabla <tt>ROLES_USERS</tt> de la base de datos
 * conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "roles_users")
@NamedQueries({ @NamedQuery(name = "RolesUsers.findAll", query = "SELECT r FROM RolesUsers r"),
		@NamedQuery(name = "RolesUsers.findByKey", query = "SELECT r FROM RolesUsers r WHERE r.idRole.idRole = :idRole AND r.idUser.idUser = :idUser") })
public class RolesUsers implements Serializable {

	private static final long serialVersionUID = -8261058445815970905L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "RolesUsersSeq", sequenceName = "roles_users_id_rous_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RolesUsersSeq")
	@Basic(optional = false)
	@Column(name = "id_rous")
	private Integer idRous;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia al rol que relaciona con el usuario
	 */
	@JoinColumn(name = "id_role", referencedColumnName = "id_role")
	@ManyToOne
	private Roles idRole;

	/**
	 * Referencia al usuario que relaciona con el rol
	 */
	@JoinColumn(name = "id_user", referencedColumnName = "id_user")
	@ManyToOne
	private Users idUser;

	/**
	 * Constructor original
	 */
	public RolesUsers() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idRous
	 *            Llave primaria que identifica al registro
	 */
	public RolesUsers(Integer idRous) {
		this.idRous = idRous;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdRous() {
		return idRous;
	}

	/**
	 * @param idRous
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdRous(Integer idRous) {
		this.idRous = idRous;
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
	 * @return Referencia al rol que relaciona con el usuario
	 */
	public Roles getIdRole() {
		return idRole;
	}

	/**
	 * @param idRole
	 *            Referencia al rol que relaciona con el usuario a definir
	 */
	public void setIdRole(Roles idRole) {
		this.idRole = idRole;
	}

	/**
	 * @return Referencia al usuario que relaciona con el rol
	 */
	public Users getIdUser() {
		return idUser;
	}

	/**
	 * @param idUser
	 *            Referencia al usuario que relaciona con el rol a definir
	 */
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
		if (!(object instanceof RolesUsers)) {
			return false;
		}
		RolesUsers other = (RolesUsers) object;
		return !((this.idRole == null && other.idRole != null)
				|| (this.idRole != null && !this.idRole.equals(other.idRole)))
				&& !((this.idUser == null && other.idUser != null)
						|| (this.idUser != null && !this.idUser.equals(other.idUser)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.RolesUsers[ idRous=" + idRous + " ]";
	}
}
