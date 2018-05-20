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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
 * anteriormente para la tabla <tt>ROLES</tt> de la base de datos conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "roles")
@NamedQueries({ @NamedQuery(name = "Roles.findAll", query = "SELECT r FROM Roles r") })
public class Roles implements Serializable {

	private static final long serialVersionUID = -8690418191923777256L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "RolesSeq", sequenceName = "roles_id_role_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RolesSeq")
	@Basic(optional = false)
	@Column(name = "id_role")
	private Integer idRole;

	/**
	 * Nombre del rol
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "role_name")
	private String roleName;

	/**
	 * Descripción del rol
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 256)
	@Column(name = "role_descr")
	private String roleDescr;

	/**
	 * Fecha de creación del rol
	 */
	@Column(name = "role_created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date roleCreated;

	/**
	 * Fecha de última modificación
	 */
	@Column(name = "role_modified")
	@Temporal(TemporalType.TIMESTAMP)
	private Date roleModified;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia la relacion de módulos con este rol
	 */
	@OneToMany(mappedBy = "idRole")
	private List<RolesModules> rolesModulesList;

	/**
	 * Referencia la relacion de usuarios con este rol
	 */
	@OneToMany(mappedBy = "idRole")
	private List<RolesUsers> rolesUsersList;

	/**
	 * Constructor original
	 */
	public Roles() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idRole
	 *            Llave primaria que identifica al registro
	 */
	public Roles(Integer idRole) {
		this.idRole = idRole;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idRole
	 *            Llave primaria que identifica al registro
	 * @param roleName
	 *            Nombre del rol
	 * @param roleDescr
	 *            Descripción del rol
	 */
	public Roles(Integer idRole, String roleName, String roleDescr) {
		this.idRole = idRole;
		this.roleName = roleName;
		this.roleDescr = roleDescr;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdRole() {
		return idRole;
	}

	/**
	 * @param idRole
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdRole(Integer idRole) {
		this.idRole = idRole;
	}

	/**
	 * @return Nombre del rol
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName
	 *            Nombre del rol a definir
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return Descripción del rol
	 */
	public String getRoleDescr() {
		return roleDescr;
	}

	/**
	 * @param roleDescr
	 *            Descripción del rol a definir
	 */
	public void setRoleDescr(String roleDescr) {
		this.roleDescr = roleDescr;
	}

	/**
	 * @return Fecha de creación del rol
	 */
	public Date getRoleCreated() {
		return roleCreated;
	}

	/**
	 * @param roleCreated
	 *            Fecha de creación del rol a definir
	 */
	public void setRoleCreated(Date roleCreated) {
		this.roleCreated = roleCreated;
	}

	/**
	 * @return Fecha de última modificación
	 */
	public Date getRoleModified() {
		return roleModified;
	}

	/**
	 * @param roleModified
	 *            Fecha de última modificación a definir
	 * 
	 */
	public void setRoleModified(Date roleModified) {
		this.roleModified = roleModified;
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
	 * @return Referencia la relacion de módulos con este rol
	 */
	public List<RolesModules> getRolesModulesList() {
		return rolesModulesList;
	}

	/**
	 * @param rolesModulesList
	 *            Referencia la relacion de módulos con este rol a definir
	 */
	public void setRolesModulesList(List<RolesModules> rolesModulesList) {
		this.rolesModulesList = rolesModulesList;
	}

	/**
	 * @return Referencia la relacion de usuarios con este rol
	 */
	public List<RolesUsers> getRolesUsersList() {
		return rolesUsersList;
	}

	/**
	 * @param rolesUsersList
	 *            Referencia la relacion de usuarios con este rol
	 */
	public void setRolesUsersList(List<RolesUsers> rolesUsersList) {
		this.rolesUsersList = rolesUsersList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idRole != null ? idRole.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Roles)) {
			return false;
		}
		Roles other = (Roles) object;
		return !((this.idRole == null && other.idRole != null)
				|| (this.idRole != null && !this.idRole.equals(other.idRole)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.Roles[ idRole=" + idRole + " ]";
	}
}
