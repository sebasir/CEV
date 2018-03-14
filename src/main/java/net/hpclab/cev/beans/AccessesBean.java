package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.ArrayList;
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
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.model.ModulesModel;
import net.hpclab.cev.services.AccessService;
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.DataWarehouse;

@ManagedBean
@ViewScoped
public class AccessesBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = -6526130526851965587L;
	private DataBaseService<Users> usersService;
	private DataBaseService<Roles> rolesService;
	private List<Users> searchUsers;
	private List<Roles> searchRoles;
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
			limpiarFiltros();
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

	public void limpiarFiltros() {
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

	public void guardar() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			for (ModulesModel m : modulesModel) {
				LOGGER.log(Level.INFO, m.getModules().getModuleName() + " -> "
						+ AccessService.getInstance().getAccessCode(m.getAccesses()));
			}
			showMessage(facesContext, OutcomeEnum.CREATE_SUCCESS, "Permisos actualizados");
		} catch (Exception e) {
			showMessage(facesContext, OutcomeEnum.GENERIC_ERROR,
					"No es posible obtener los roles del usuario");
		}
	}

	public void loadUsersModules(Users users) {
		try {
			roles = null;
			this.users = users;
			modulesModel = new ArrayList<>();

			HashSet<Roles> userRoles = AccessService.getInstance().getUserRoles(users);
			HashMap<Modules, Integer> modules;
			if (userRoles != null) {
				for (Roles r : userRoles) {
					modules = AccessService.getInstance().getRoleModules(r);
					if (modules != null)
						for (Modules m : modules.keySet())
							modulesModel.add(new ModulesModel(m, "Rol: " + r.getRoleName(),
									AccessService.getInstance().getAccessFromCode(modules.get(m))));
				}
			}

			modules = AccessService.getInstance().getDirectUserModules(users);
			ModulesModel mm = null;
			if (modules != null) {
				for (Modules m : modules.keySet()) {
					mm = new ModulesModel(m, null, AccessService.getInstance().getAccessFromCode(modules.get(m)));
					if (modulesModel.contains(mm))
						modulesModel.remove(mm);
					modulesModel.add(mm);
				}
			}

			for (Modules m : DataWarehouse.getInstance().allModules) {
				mm = new ModulesModel(m, null, AccessService.getInstance().getAccessFromCode(0));
				if (!modulesModel.contains(mm))
					modulesModel.add(mm);
			}
		} catch (Exception e) {
			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR,
					"No es posible obtener los roles del usuario");
		}
	}

	public void loadRolesModules(Roles roles) {
		users = null;
		this.roles = roles;
	}

	public List<Users> getSearchUsers() {
		return searchUsers;
	}

	public void setSearchUsers(List<Users> searchUsers) {
		this.searchUsers = searchUsers;
	}

	public List<Roles> getSearchRoles() {
		return searchRoles;
	}

	public void setSearchRoles(List<Roles> searchRoles) {
		this.searchRoles = searchRoles;
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
		return modulesModel;
	}
}
