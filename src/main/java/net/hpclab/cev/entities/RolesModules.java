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
 * anteriormente para la tabla <tt>ROLES_MODULES</tt> de la base de datos
 * conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "roles_modules")
@NamedQueries({ @NamedQuery(name = "RolesModules.findAll", query = "SELECT r FROM RolesModules r"),
		@NamedQuery(name = "RolesModules.findByKey", query = "SELECT r FROM RolesModules r WHERE r.idModule.idModule = :idModule AND r.idRole.idRole = :idRole") })
public class RolesModules implements Serializable {

	private static final long serialVersionUID = 3622251626675534915L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "RolesModulesSeq", sequenceName = "roles_modules_id_romo_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RolesModulesSeq")
	@Basic(optional = false)
	@Column(name = "id_romo")
	private Integer idRomo;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia al modulo que relaciona con el rol
	 */
	@JoinColumn(name = "id_module", referencedColumnName = "id_module")
	@ManyToOne
	private Modules idModule;

	/**
	 * Referencia al rol que relaciona con el modulo
	 */
	@JoinColumn(name = "id_role", referencedColumnName = "id_role")
	@ManyToOne
	private Roles idRole;

	/**
	 * Nivel de acceso según la representación decimal
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "access_level")
	private Integer accessLevel;

	/**
	 * Constructor original
	 */
	public RolesModules() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idRomo
	 *            Llave primaria que identifica al registro
	 */
	public RolesModules(Integer idRomo) {
		this.idRomo = idRomo;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdRomo() {
		return idRomo;
	}

	/**
	 * @param idRomo
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdRomo(Integer idRomo) {
		this.idRomo = idRomo;
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
	 * @return Referencia al modulo que relaciona con el rol
	 */
	public Modules getIdModule() {
		return idModule;
	}

	/**
	 * @param idModule
	 *            Referencia al modulo que relaciona con el rol a definir
	 */
	public void setIdModule(Modules idModule) {
		this.idModule = idModule;
	}

	/**
	 * @return Referencia al rol que relaciona con el modulo
	 */
	public Roles getIdRole() {
		return idRole;
	}

	/**
	 * @param idRole
	 *            Referencia al rol que relaciona con el modulo a definir
	 */
	public void setIdRole(Roles idRole) {
		this.idRole = idRole;
	}

	/**
	 * @return Nivel de acceso según la representación decimal
	 */
	public Integer getAccessLevel() {
		return accessLevel;
	}

	/**
	 * @param accessLevel
	 *            Nivel de acceso según la representación decimal a definir
	 */
	public void setAccessLevel(Integer accessLevel) {
		this.accessLevel = accessLevel;
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
		return !((this.idModule == null && other.idModule != null)
				|| (this.idModule != null && !this.idModule.equals(other.idModule)))
				&& !((this.idRole == null && other.idRole != null)
						|| (this.idRole != null && !this.idRole.equals(other.idRole)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.RolesModules[ idRomo=" + idRomo + " ]";
	}
}
