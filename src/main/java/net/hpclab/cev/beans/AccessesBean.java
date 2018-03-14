package net.hpclab.cev.beans;

import java.io.Serializable;
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
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.DataWarehouse;

@ManagedBean
@ViewScoped
public class AccessesBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = -6526130526851965587L;
	private DataBaseService<Users> usersService;
	private List<Users> searchUsers;
	private Users users;
	private Users searchUser;

	private static final Logger LOGGER = Logger.getLogger(AccessesBean.class.getSimpleName());

	public AccessesBean() {
		try {
			usersService = new DataBaseService<>(Users.class);
			limpiarFiltros();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	
	public DataBaseService<Users>.Pager getPager() {
		return usersService.getPager();
	}

	public void limpiarFiltros() {
		users = new Users();
		searchUser = new Users();
		searchUsers = null;
	}
	
	public void search() {
		try {
			searchUsers = usersService.getList(searchUser);
			if (usersService.getPager().getNumberOfResults() == 0)
				showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR, "No se encontraron datos");
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}
	}

	public List<Users> getSearchUsers() {
		return searchUsers;
	}

	public void setSearchUsers(List<Users> searchUsers) {
		this.searchUsers = searchUsers;
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

	public List<Modules> getAllModules() {
		return DataWarehouse.getInstance().allModules;
	}
	
	public List<Institution> getAllInstitutions() {
		return DataWarehouse.getInstance().allInstitutions;
	}
	
	public List<Roles> getAllRoles() {
		return DataWarehouse.getInstance().allRoles;
	}
}
