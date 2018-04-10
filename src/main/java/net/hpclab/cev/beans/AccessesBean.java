package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

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
import net.hpclab.cev.services.ParseExceptionService;

@ManagedBean
@ViewScoped
public class AccessesBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = -6526130526851965587L;
	private DataBaseService<Users> usersService;
	private DataBaseService<Roles> rolesService;
	private List<Users> searchUsers;
	private List<Roles> searchRoles;
	private List<RolesModel> userRolesModel;
	private List<ModulesModel> modulesModel;
	private Roles roles;
	private Users users;
	private Users searchUser;
	private Roles searchRole;

	private static final Logger LOGGER = Logger.getLogger(AccessesBean.class.getSimpleName());

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

	public DataBaseService<Users>.Pager getUsersPager() {
		return usersService.getPager();
	}

	public DataBaseService<Roles>.Pager getRolesPager() {
		return rolesService.getPager();
	}

	public void restartDomainLists() {
		try {
			DataWarehouse.getInstance().initLists();
			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_INFO,
					"Listas de datos reiniciadas correctamente");
		} catch (Exception e) {
			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR,
					"Error reiniciando las listas: " + e.getMessage());
		}
	}

	public void limpiarRoleFiltros() {
		roles = new Roles();
		searchRole = new Roles();
		searchRoles = null;
	}

	public void limpiarUserFiltros() {
		users = new Users();
		searchUser = new Users();
		searchUsers = null;
	}

	public void searchUser() {
		try {
			searchUsers = usersService.getList(searchUser);
			if (usersService.getPager().getNumberOfResults() == 0)
				showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR, "No se encontraron datos");
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}
	}

	public void searchRole() {
		try {
			searchRoles = rolesService.getList(searchRole);
			if (rolesService.getPager().getNumberOfResults() == 0)
				showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR, "No se encontraron datos");
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}
	}

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

	private void loadRemainingModules(AccessService accessService) throws Exception {
		ModulesModel mm = null;
		for (Modules m : DataWarehouse.getInstance().allModules) {
			mm = new ModulesModel(m, null, 0, accessService.getAccessFromCode(0));
			if (!modulesModel.contains(mm))
				modulesModel.add(mm);
		}
	}

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

	public List<Users> getSearchUsers() {
		return searchUsers;
	}

	public List<Roles> getSearchRoles() {
		return searchRoles;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public Users getSearchUser() {
		return searchUser;
	}

	public void setSearchUser(Users searchUser) {
		this.searchUser = searchUser;
	}

	public Roles getRoles() {
		return roles;
	}

	public void setRoles(Roles roles) {
		this.roles = roles;
	}

	public Roles getSearchRole() {
		return searchRole;
	}

	public void setSearchRole(Roles searchRole) {
		this.searchRole = searchRole;
	}

	public List<Modules> getAllModules() {
		return DataWarehouse.getInstance().allModules;
	}

	public List<Institution> getAllInstitutions() {
		return DataWarehouse.getInstance().allInstitutions;
	}

	public List<Roles> getAllRoles() {
		return DataWarehouse.getInstance().allRoles;
	}

	public List<ModulesModel> getModulesModel() {
		Collections.sort(modulesModel);
		return modulesModel;
	}

	public List<RolesModel> getUserRolesModel() {
		Collections.sort(userRolesModel);
		return userRolesModel;
	}
}
