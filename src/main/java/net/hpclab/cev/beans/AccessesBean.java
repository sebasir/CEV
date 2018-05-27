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

package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import net.hpclab.cev.entities.Institution;
import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.entities.Roles;
import net.hpclab.cev.entities.Users;
import net.hpclab.cev.enums.AuditEnum;
import net.hpclab.cev.enums.ModulesEnum;
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.model.ModulesModel;
import net.hpclab.cev.model.RolesModel;
import net.hpclab.cev.services.AccessService;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.DataWarehouse;
import net.hpclab.cev.services.EntityResourcer;
import net.hpclab.cev.services.MessagesService;
import net.hpclab.cev.services.ParseExceptionService;

/**
 * Este servicio permite la interacción con el servicio de
 * <tt>DataBaseService</tt> para la gestión de roles y accesos de un usuario
 * para los diferentes módulos que componen el sistema. Principalmente expone
 * métodos de creación, edición, consulta y eliminación, validando la
 * posibilidad de estas operaciones contra el servicio de
 * <tt>AccessesService</tt>, el cual valida el usuario que realiza la operación.
 * 
 * @author Sebasir
 * @since 1.0
 * @see DataBaseService
 * @see Users
 * @see Roles
 * @see RolesModel
 * @see ModulesModel
 */
@ManagedBean
@ViewScoped
public class AccessesBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = -6526130526851965587L;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>Users</tt>, lo cual permite extender todas las operaciones del servicio
	 * para esta clase.
	 */
	private DataBaseService<Users> usersService;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>Roles</tt>, lo cual permite extender todas las operaciones del servicio
	 * para esta clase.
	 */
	private DataBaseService<Roles> rolesService;

	/**
	 * Archivo de propiedades de los mensajes dinámicos
	 */
	private UploadedFile contentFile;

	/**
	 * Lista de usuarios buscados en el página
	 */
	private List<Users> searchUsers;

	/**
	 * Lista de roles buscados en el página
	 */
	private List<Roles> searchRoles;

	/**
	 * Lista de modelos de roles para modificación de accesos
	 */
	private List<RolesModel> userRolesModel;

	/**
	 * Lista de modelos de modulos para modificación de accesos
	 */
	private List<ModulesModel> modulesModel;

	/**
	 * Objeto que permite las operaciones de creación, edición, y eliminación de
	 * roles
	 */
	private Roles roles;

	/**
	 * Objeto que permite las operaciones de creación, edición, y eliminación de
	 * usuarios
	 */
	private Users users;

	/**
	 * Objeto que permite la búsqueda de usuarios
	 */
	private Users searchUser;

	/**
	 * Objeto que permite la búsqueda de roles
	 */
	private Roles searchRole;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(AccessesBean.class.getSimpleName());

	/**
	 * Constructor que permite inicializar los servicios de <tt>DataBaseService</tt>
	 * y búsqueda de filtros
	 */
	public AccessesBean() {
		try {
			usersService = new DataBaseService<>(Users.class);
			rolesService = new DataBaseService<>(Roles.class);
			limpiarUserFiltros();
			limpiarRoleFiltros();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * @return Permite obtener una referencia al paginador de la consulta de
	 *         usuarios
	 */
	public DataBaseService<Users>.Pager getUsersPager() {
		return usersService.getPager();
	}

	/**
	 * @return Permite obtener una referencia al paginador de la consulta de roles
	 */
	public DataBaseService<Roles>.Pager getRolesPager() {
		return rolesService.getPager();
	}

	/**
	 * Servicio que reinicia las listas de dominio que yacen en el servicio
	 * <tt>DataWarehouse</tt>
	 */
	public void restartDomainLists() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			EntityResourcer.getInstance().disconnect();
			DataWarehouse.getInstance().initLists();
			
			super.getExternalBean(facesContext, TaxonomyBean.class).limpiarFiltros();
			super.getExternalBean(facesContext, LocationBean.class).limpiarFiltros();
			super.getExternalBean(facesContext, CollectionBean.class).limpiarFiltros();
			super.getExternalBean(facesContext, AuthorBean.class).restartAuthorTypes();
			super.getExternalBean(facesContext, SpecimenBean.class).limpiarFiltros();
			super.getExternalBean(facesContext, SpecimenContentBean.class).limpiarFiltros();
			super.getExternalBean(facesContext, UsersBean.class).limpiarFiltros();
			
			showMessage(facesContext, OutcomeEnum.GENERIC_INFO,
					"Listas de datos reiniciadas correctamente");
		} catch (Exception e) {
			showMessage(facesContext, OutcomeEnum.GENERIC_ERROR,
					"Error reiniciando las listas: " + e.getMessage());
		}
	}

	/**
	 * Permite reiniciar los objetos de búsqueda y alteración de roles
	 */
	public void limpiarRoleFiltros() {
		roles = new Roles();
		searchRole = new Roles();
		searchRoles = null;
	}

	/**
	 * Permite reiniciar los objetos de búsqueda y alteración de usuarios
	 */
	public void limpiarUserFiltros() {
		users = new Users();
		searchUser = new Users();
		searchUsers = null;
	}

	/**
	 * Permite realizar una consulta filtrada de usuarios
	 */
	public void searchUser() {
		try {
			searchUsers = usersService.getList(searchUser);
			if (usersService.getPager().getNumberOfResults() == 0)
				showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR, "No se encontraron datos");
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}
	}

	/**
	 * Permite realizar una consulta filtrada de roles
	 */
	public void searchRole() {
		try {
			searchRoles = rolesService.getList(searchRole);
			if (rolesService.getPager().getNumberOfResults() == 0)
				showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR, "No se encontraron datos");
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}
	}

	/**
	 * Permite guardar la modificación de accesos para un rol, tal que interpreta
	 * las selecciones de los accesos en una representación decimal
	 */
	public void guardarRoles() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.ACCESSES, getUsers(facesContext),
					Constant.AccessLevel.INSERT)) {
				showAccessMessage(facesContext, OutcomeEnum.INSERT_NOT_GRANTED);
				return;
			}

			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.ACCESSES, getUsers(facesContext),
					Constant.AccessLevel.DELETE)) {
				showAccessMessage(facesContext, OutcomeEnum.DELETE_NOT_GRANTED);
				return;
			}

			String rolesString = "";
			AuditEnum operation;
			AccessService accessService = AccessService.getInstance();
			for (RolesModel r : userRolesModel) {
				if (r.hasChanged()) {
					operation = AuditEnum.INSERT;
					if (!rolesString.isEmpty())
						rolesString += ", ";

					rolesString += r.getRoles().getRoleName();
					if (r.isActive())
						rolesString += " añadido";
					else {
						operation = AuditEnum.DELETE;
						rolesString += " eliminado";
					}
					accessService.setRoleUserAccess(r.getRoles(), users, operation, StatusEnum.ACTIVO);
					r.restartUnchanged();
				}
			}

			if (!rolesString.isEmpty())
				showMessage(facesContext, OutcomeEnum.GENERIC_INFO, "Roles actualizados: " + rolesString);
			else
				showMessage(facesContext, OutcomeEnum.GENERIC_WARNING, "Nada que actualizar");
		} catch (Exception e) {
			showMessage(facesContext, OutcomeEnum.GENERIC_ERROR,
					"No es posible definir los roles para el usuario: " + e.getMessage());
			LOGGER.log(Level.SEVERE, "Error actualizando roles de usuario", e);
		}
	}

	/**
	 * Permite guardar la modificación de accesos para un modulo, tal que interpreta
	 * las selecciones de los accesos en una representación decimal
	 */
	public void guardarModulos() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.ACCESSES, getUsers(facesContext),
					Constant.AccessLevel.INSERT)) {
				showAccessMessage(facesContext, OutcomeEnum.INSERT_NOT_GRANTED);
				return;
			}

			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.ACCESSES, getUsers(facesContext),
					Constant.AccessLevel.UPDATE)) {
				showAccessMessage(facesContext, OutcomeEnum.UPDATE_NOT_GRANTED);
				return;
			}

			AccessService accessService = AccessService.getInstance();
			String accesses = "";
			AuditEnum operation;
			int currentAccessCode = 0;
			for (ModulesModel m : modulesModel) {
				currentAccessCode = accessService.getAccessCode(m.getAccesses());
				if (currentAccessCode != m.getInitialAccessCode()) {
					operation = AuditEnum.INSERT;
					if (m.getInitialAccessCode() != 0)
						operation = AuditEnum.UPDATE;

					if (users != null)
						accessService.setModuleUserAccess(m.getModules(), users, currentAccessCode, operation,
								StatusEnum.ACTIVO);
					else if (roles != null)
						accessService.setRoleModule(roles, m.getModules(), currentAccessCode, operation,
								StatusEnum.ACTIVO);

					if (!accesses.isEmpty())
						accesses += ", ";
					accesses += m.getModules().getModuleName();
					m.setInitialAccessCode(currentAccessCode);
				}
			}

			if (!accesses.isEmpty())
				showMessage(facesContext, OutcomeEnum.GENERIC_INFO, "Permisos actualizados: " + accesses);
			else
				showMessage(facesContext, OutcomeEnum.GENERIC_WARNING, "Nada que actualizar");
		} catch (Exception e) {
			showMessage(facesContext, OutcomeEnum.GENERIC_ERROR,
					"No es posible guardar los módulos: " + e.getMessage());
			LOGGER.log(Level.SEVERE, "No es posible guardar los módulos", e);
		}
	}

	/**
	 * Permite cargar los roles de un usuario de manera que sean modificables
	 * 
	 * @param users
	 *            Objeto de usuario que deben cargarse los roles
	 */
	public void loadUsersRoles(Users users) {
		try {
			this.users = users;
			userRolesModel = new ArrayList<>();
			HashSet<Roles> userRolesSet = AccessService.getInstance().getUserRoles(users);
			if (userRolesSet != null)
				for (Roles r : userRolesSet)
					userRolesModel.add(new RolesModel(r, true));

			RolesModel rr = null;
			for (Roles r : DataWarehouse.getInstance().allRoles) {
				rr = new RolesModel(r, false);
				if (!userRolesModel.contains(rr))
					userRolesModel.add(rr);
			}
		} catch (Exception e) {
			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR,
					"No es posible obtener los roles del usuario, " + e.getMessage());
		}
	}

	/**
	 * Permite cargar los modulos de un usuario de manera que sean modificables
	 * 
	 * @param users
	 *            Objeto de usuario que deben cargarse los modulos
	 */
	public void loadUsersModules(Users users) {
		try {
			AccessService accessService = AccessService.getInstance();
			roles = null;
			this.users = users;
			modulesModel = new ArrayList<>();

			HashSet<Roles> userRoles = accessService.getUserRoles(users);
			HashMap<Modules, Integer> modules;
			if (userRoles != null) {
				ModulesModel mm = null;
				for (Roles r : userRoles) {
					modules = accessService.getRoleModules(r);
					if (modules != null)
						for (Modules m : modules.keySet()) {
							mm = new ModulesModel(m, "Rol: " + r.getRoleName(), modules.get(m),
									accessService.getAccessFromCode(modules.get(m)));
							if (modulesModel.contains(mm)) {
								modulesModel.remove(mm);
								mm.setInherited("Varios origenes");
							}

							modulesModel.add(mm);
						}
				}
			}

			modules = accessService.getDirectUserModules(users);
			ModulesModel mm = null;
			if (modules != null) {
				for (Modules m : modules.keySet()) {
					mm = new ModulesModel(m, null, modules.get(m), accessService.getAccessFromCode(modules.get(m)));
					if (modulesModel.contains(mm)) {
						modulesModel.remove(mm);
						mm.setInherited("Varios origenes");
					}

					modulesModel.add(mm);
				}
			}

			loadRemainingModules(accessService);
		} catch (Exception e) {
			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR,
					"No es posible obtener los accesos del usuario, " + e.getMessage());
		}
	}

	/**
	 * Permite cargar los modulos de un rol de manera que sean modificables
	 * 
	 * @param roles
	 *            Objeto de rol que deben cargarse los modulos
	 */
	public void loadRolesModules(Roles roles) {
		this.roles = roles;
		users = null;
		modulesModel = new ArrayList<>();

		try {
			AccessService accessService = AccessService.getInstance();
			HashMap<Modules, Integer> modules = accessService.getRoleModules(roles);
			ModulesModel mm = null;
			if (modules != null)
				for (Modules m : modules.keySet()) {
					mm = new ModulesModel(m, "Rol: " + roles.getRoleName(), modules.get(m),
							accessService.getAccessFromCode(modules.get(m)));
					if (modulesModel.contains(mm)) {
						modulesModel.remove(mm);
						mm.setInherited("Varios origenes");
					}

					modulesModel.add(mm);
				}
			loadRemainingModules(accessService);
		} catch (Exception e) {
			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR,
					"No es posible obtener los accesos del rol, " + e.getMessage());
		}
	}

	/**
	 * Permite cargar los modulos restantes que no estan presentes para un rol
	 * 
	 * @param accessService
	 *            Servicio de accesos
	 * @throws Exception
	 *             Si el servicio de <tt>DataWarehouse</tt> no pudo iniciar
	 */
	private void loadRemainingModules(AccessService accessService) throws Exception {
		ModulesModel mm = null;
		for (Modules m : DataWarehouse.getInstance().allModules) {
			mm = new ModulesModel(m, null, 0, accessService.getAccessFromCode(0));
			if (!modulesModel.contains(mm))
				modulesModel.add(mm);
		}
	}

	/**
	 * Permite realizar carga y validación del archivo de mensajes
	 * 
	 * @param event
	 *            Evento de carga del cual se recupera el archivo.
	 */
	public void handleFileUpload(FileUploadEvent event) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			Properties messages = new Properties();
			contentFile = event.getFile();
			messages.load(contentFile.getInputstream());
			MessagesService.getInstance().loadMessages(messages);

			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_INFO,
					"Mensajes reiniciados correctamente");
		} catch (Exception e) {
			showMessage(facesContext, OutcomeEnum.GENERIC_ERROR, "Error recargando mensajes");
		}
	}

	/**
	 * Permite guardar un rol que se haya definido en la interfáz validando el
	 * permiso de escritura
	 */
	public void persistRole() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.CREATE_ERROR;
		String transactionMessage = roles.getRoleName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.ACCESSES, getUsers(facesContext),
					Constant.AccessLevel.INSERT)) {
				showAccessMessage(facesContext, OutcomeEnum.INSERT_NOT_GRANTED);
				return;
			}

			roles.setRoleCreated(Calendar.getInstance().getTime());
			roles.setRoleModified(Calendar.getInstance().getTime());
			roles = rolesService.persist(roles);
			DataWarehouse.getInstance().allRoles.add(roles);
			outcomeEnum = OutcomeEnum.CREATE_SUCCESS;

			limpiarRoleFiltros();
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error persisting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite editar un rol que se haya redefinido en la interfáz validando el
	 * permiso de edición
	 */
	public void editRole() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.UPDATE_ERROR;
		String transactionMessage = roles.getRoleName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.ACCESSES, getUsers(facesContext),
					Constant.AccessLevel.UPDATE)) {
				showAccessMessage(facesContext, OutcomeEnum.UPDATE_NOT_GRANTED);
				return;
			}

			roles.setStatus(StatusEnum.ACTIVO.get());
			roles.setRoleModified(Calendar.getInstance().getTime());
			Roles tempRoles = rolesService.merge(roles);
			DataWarehouse.getInstance().allRoles.remove(roles);
			DataWarehouse.getInstance().allRoles.add(tempRoles);
			outcomeEnum = OutcomeEnum.UPDATE_SUCCESS;

			limpiarRoleFiltros();
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error editing: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite eliminar un rol validando el permiso de eliminación
	 */
	public void deleteRole() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.DELETE_ERROR;
		String transactionMessage = roles.getRoleName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.ACCESSES, getUsers(facesContext),
					Constant.AccessLevel.DELETE)) {
				showAccessMessage(facesContext, OutcomeEnum.DELETE_NOT_GRANTED);
				return;
			}

			rolesService.delete(roles);
			DataWarehouse.getInstance().allRoles.remove(roles);
			outcomeEnum = OutcomeEnum.DELETE_SUCCESS;

			limpiarRoleFiltros();
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error deleting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * @return Lista de usuarios buscados en el página
	 */
	public List<Users> getSearchUsers() {
		return searchUsers;
	}

	/**
	 * @return Lista de roles buscados en el página a definir
	 */
	public List<Roles> getSearchRoles() {
		return searchRoles;
	}

	/**
	 * @return Objeto que permite las operaciones de creación, edición, y
	 *         eliminación de usuarios
	 */
	public Users getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            Objeto que permite las operaciones de creación, edición, y
	 *            eliminación de usuarios a definir
	 */
	public void setUsers(Users users) {
		this.users = users;
	}

	/**
	 * @return Objeto que permite la búsqueda de usuarios
	 */
	public Users getSearchUser() {
		return searchUser;
	}

	/**
	 * @param searchUser
	 *            Objeto que permite la búsqueda de usuarios a definir
	 */
	public void setSearchUser(Users searchUser) {
		this.searchUser = searchUser;
	}

	/**
	 * @return Objeto que permite las operaciones de creación, edición, y
	 *         eliminación de roles
	 */
	public Roles getRoles() {
		return roles;
	}

	/**
	 * @param roles
	 *            Objeto que permite las operaciones de creación, edición, y
	 *            eliminación de roles a definir
	 */
	public void setRoles(Roles roles) {
		this.roles = roles;
	}

	/**
	 * @return Objeto que permite la búsqueda de roles
	 */
	public Roles getSearchRole() {
		return searchRole;
	}

	/**
	 * @param searchRole
	 *            Objeto que permite la búsqueda de roles a definir
	 */
	public void setSearchRole(Roles searchRole) {
		this.searchRole = searchRole;
	}

	/**
	 * @return Permite el acceso a todos los modulos del sistema
	 */
	public List<Modules> getAllModules() {
		return DataWarehouse.getInstance().allModules;
	}

	/**
	 * @return Permite el acceso a todas las instituciones del sistema
	 */
	public List<Institution> getAllInstitutions() {
		return DataWarehouse.getInstance().allInstitutions;
	}

	/**
	 * @return Permite el acceso a todos los roles del sistema
	 */
	public List<Roles> getAllRoles() {
		return DataWarehouse.getInstance().allRoles;
	}

	/**
	 * @return Lista de modelos de modulos para modificación de accesos, de manera
	 *         ordenada
	 */
	public List<ModulesModel> getModulesModel() {
		Collections.sort(modulesModel);
		return modulesModel;
	}

	/**
	 * @return Lista de modelos de roles para modificación de accesos, de manera
	 *         ordenada
	 */
	public List<RolesModel> getUserRolesModel() {
		Collections.sort(userRolesModel);
		return userRolesModel;
	}
}
