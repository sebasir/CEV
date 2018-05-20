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
 * anteriormente para la tabla <tt>MODULES_USERS</tt> de la base de datos conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "modules_users")
@NamedQueries({ @NamedQuery(name = "ModulesUsers.findAll", query = "SELECT m FROM ModulesUsers m"),
		@NamedQuery(name = "ModulesUsers.findByKey", query = "SELECT m FROM ModulesUsers m WHERE m.idModule.idModule = :idModule AND m.idUser.idUser = :idUser") })
public class ModulesUsers implements Serializable {

	private static final long serialVersionUID = 7522648274857970442L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "ModulesUsersSeq", sequenceName = "modules_users_id_mous_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ModulesUsersSeq")
	@Basic(optional = false)
	@Column(name = "id_mous")
	private Integer idMous;

	/**
	 * Nivel de acceso según la representación decimal
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "access_level")
	private Integer accessLevel;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia al módulo que relaciona
	 */
	@JoinColumn(name = "id_module", referencedColumnName = "id_module")
	@ManyToOne
	private Modules idModule;

	/**
	 * Referencia al usuario que relaciona
	 */
	@JoinColumn(name = "id_user", referencedColumnName = "id_user")
	@ManyToOne
	private Users idUser;

	/**
	 * Constructor original
	 */
	public ModulesUsers() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idMous
	 *            Llave primaria que identifica al registro
	 */
	public ModulesUsers(Integer idMous) {
		this.idMous = idMous;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idMous
	 *            Llave primaria que identifica al registro
	 * @param accessLevel
	 *            Nivel de acceso según la representación decimal
	 */
	public ModulesUsers(Integer idMous, Integer accessLevel) {
		this.idMous = idMous;
		this.accessLevel = accessLevel;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdMous() {
		return idMous;
	}

	/**
	 * @param idMous
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdMous(Integer idMous) {
		this.idMous = idMous;
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
	 * @return Referencia al módulo que relaciona
	 */
	public Modules getIdModule() {
		return idModule;
	}

	/**
	 * @param idModule
	 *            Referencia al módulo que relaciona a definir
	 */
	public void setIdModule(Modules idModule) {
		this.idModule = idModule;
	}

	/**
	 * @return Referencia al usuario que relaciona
	 */
	public Users getIdUser() {
		return idUser;
	}

	/**
	 * @param idUser
	 *            Referencia al usuario que relaciona a definir
	 */
	public void setIdUser(Users idUser) {
		this.idUser = idUser;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idMous != null ? idMous.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ModulesUsers))
			return false;

		ModulesUsers other = (ModulesUsers) object;
		return !((this.idModule == null && other.idModule != null)
				|| (this.idModule != null && !this.idModule.equals(other.idModule)))
				&& !((this.idUser == null && other.idUser != null)
						|| (this.idUser != null && !this.idUser.equals(other.idUser)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.ModulesUsers[ idMous=" + idMous + " ]";
	}
}
