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
import java.util.List;
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
import javax.persistence.OneToMany;
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
 * anteriormente para la tabla <tt>MODULES</tt> de la base de datos conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "modules")
@NamedQueries({ @NamedQuery(name = "Modules.findAll", query = "SELECT m FROM Modules m"),
		@NamedQuery(name = "Modules.findByIdModule", query = "SELECT m FROM Modules m WHERE m.idModule = :idModule"),
		@NamedQuery(name = "Modules.findByModuleName", query = "SELECT m FROM Modules m WHERE m.moduleName = :moduleName"),
		@NamedQuery(name = "Modules.findByModuleDescr", query = "SELECT m FROM Modules m WHERE m.moduleDescr = :moduleDescr"),
		@NamedQuery(name = "Modules.findByModuleOrder", query = "SELECT m FROM Modules m WHERE m.moduleOrder = :moduleOrder"),
		@NamedQuery(name = "Modules.findByModulePage", query = "SELECT m FROM Modules m WHERE m.modulePage = :modulePage"),
		@NamedQuery(name = "Modules.findByModuleIcon", query = "SELECT m FROM Modules m WHERE m.moduleIcon = :moduleIcon"),
		@NamedQuery(name = "Modules.findByStatus", query = "SELECT m FROM Modules m WHERE m.status = :status") })
public class Modules implements Serializable {

	private static final long serialVersionUID = -5162468038876533953L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id_module")
	private Integer idModule;

	/**
	 * Nombre del modulo
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "module_name")
	private String moduleName;

	/**
	 * Descripción del módulo
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 256)
	@Column(name = "module_descr")
	private String moduleDescr;

	/**
	 * Orden del módulo en el menu
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "module_order")
	private int moduleOrder;

	/**
	 * Página del módulo
	 */
	@Size(max = 32)
	@Column(name = "module_page")
	private String modulePage;

	/**
	 * Nombre del ícono del módulo
	 */
	@Size(max = 16)
	@Column(name = "module_icon")
	private String moduleIcon;

	/**
	 * Çódigo identificador del módulo
	 */
	@Size(max = 16)
	@Column(name = "module_code")
	private String moduleCode;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia los módulos que este contiene
	 */
	@OneToMany(mappedBy = "idContainer")
	private List<Modules> modulesList;

	/**
	 * Referencia el módulo que contiene a este
	 */
	@JoinColumn(name = "id_container", referencedColumnName = "id_module")
	@ManyToOne
	private Modules idContainer;

	/**
	 * Constructor original
	 */
	public Modules() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idModule
	 *            Llave primaria que identifica al registro
	 */
	public Modules(Integer idModule) {
		this.idModule = idModule;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idModule
	 *            Llave primaria que identifica al registro
	 * @param moduleName
	 *            Nombre del modulo
	 * @param moduleDescr
	 *            Descripción del módulo
	 * @param moduleOrder
	 *            Orden del módulo en el menu
	 */
	public Modules(Integer idModule, String moduleName, String moduleDescr, int moduleOrder) {
		this.idModule = idModule;
		this.moduleName = moduleName;
		this.moduleDescr = moduleDescr;
		this.moduleOrder = moduleOrder;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdModule() {
		return idModule;
	}

	/**
	 * @param idModule
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdModule(Integer idModule) {
		this.idModule = idModule;
	}

	/**
	 * @return Nombre del modulo
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * @param moduleName
	 *            Nombre del modulo a definir
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * @return Descripción del módulo
	 */
	public String getModuleDescr() {
		return moduleDescr;
	}

	/**
	 * @param moduleDescr
	 *            Descripción del módulo a definir
	 */
	public void setModuleDescr(String moduleDescr) {
		this.moduleDescr = moduleDescr;
	}

	/**
	 * @return Orden del módulo en el menu
	 */
	public int getModuleOrder() {
		return moduleOrder;
	}

	/**
	 * @param moduleOrder
	 *            Orden del módulo en el menu a definir
	 */
	public void setModuleOrder(int moduleOrder) {
		this.moduleOrder = moduleOrder;
	}

	/**
	 * @return Página del módulo
	 */
	public String getModulePage() {
		return modulePage;
	}

	/**
	 * @param modulePage
	 *            Página del módulo a definir
	 */
	public void setModulePage(String modulePage) {
		this.modulePage = modulePage;
	}

	/**
	 * @return Nombre del ícono del módulo
	 */
	public String getModuleIcon() {
		return moduleIcon;
	}

	/**
	 * @param moduleIcon
	 *            Nombre del ícono del módulo
	 */
	public void setModuleIcon(String moduleIcon) {
		this.moduleIcon = moduleIcon;
	}

	/**
	 * @return Çódigo identificador del módulo
	 */
	public String getModuleCode() {
		return moduleCode;
	}

	/**
	 * @param moduleCode
	 *            Çódigo identificador del módulo a definir
	 */
	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
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
	 * @return Referencia los módulos que este contiene
	 */
	public List<Modules> getModulesList() {
		return modulesList;
	}

	/**
	 * @param modulesList
	 *            Referencia los módulos que este contiene a definir
	 */
	public void setModulesList(List<Modules> modulesList) {
		this.modulesList = modulesList;
	}

	/**
	 * @return Referencia el módulo que contiene a este
	 */
	public Modules getIdContainer() {
		return idContainer;
	}

	/**
	 * @param idContainer
	 *            Referencia el módulo que contiene a este a definir
	 */
	public void setIdContainer(Modules idContainer) {
		this.idContainer = idContainer;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idModule != null ? idModule.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Modules)) {
			return false;
		}
		Modules other = (Modules) object;
		return !((this.idModule == null && other.idModule != null)
				|| (this.idModule != null && !this.idModule.equals(other.idModule)));
	}

	@Override
	public String toString() {
		return "net.hpclab.cev.entities.Modules[ idModule=" + idModule + " ]";
	}

}
