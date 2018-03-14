package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import net.hpclab.cev.entities.Institution;
import net.hpclab.cev.entities.Users;
import net.hpclab.cev.enums.ModulesEnum;
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.services.AccessService;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.DataWarehouse;
import net.hpclab.cev.services.ParseExceptionService;
import net.hpclab.cev.services.Util;

@ManagedBean
@ViewScoped
public class UsersBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 501004550803103355L;
	private DataBaseService<Users> usersService;
	private List<Users> searchUsers;
	private Users users;
	private Users searchUser;

	private static final Logger LOGGER = Logger.getLogger(UsersBean.class.getSimpleName());

	public UsersBean() {
		try {
			usersService = new DataBaseService<>(Users.class);
			limpiarFiltros();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	public void limpiarFiltros() {
		users = new Users();
		searchUser = new Users();
		searchUsers = null;
	}

	public DataBaseService<Users>.Pager getPager() {
		return usersService.getPager();
	}

	public void persist() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.CREATE_ERROR;
		String transactionMessage = users.getUserNames() + users.getUserLastnames();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.USR_INS, getUsers(facesContext),
					Constant.AccessLevel.INSERT)) {
				showAccessMessage(facesContext, OutcomeEnum.INSERT_NOT_GRANTED);
				return;
			}

			users.setStatus(StatusEnum.BLOQUEADO.get());
			users.setUserPassword(Util.encrypt(Constant.RESTART_PASSWORD));
			users.setUserCreated(Calendar.getInstance().getTime());
			users.setUserModified(Calendar.getInstance().getTime());
			users = usersService.persist(users);
			DataWarehouse.getInstance().allUsers.add(users);
			outcomeEnum = OutcomeEnum.CREATE_SUCCESS;

			limpiarFiltros();
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error persisting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public void edit() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.UPDATE_ERROR;
		String transactionMessage = users.getUserNames() + users.getUserLastnames();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.USR_INS, getUsers(facesContext),
					Constant.AccessLevel.UPDATE)) {
				showAccessMessage(facesContext, OutcomeEnum.UPDATE_NOT_GRANTED);
				return;
			}

			users.setStatus(StatusEnum.ACTIVO.get());
			users.setUserModified(Calendar.getInstance().getTime());
			Users tempSpecimen = usersService.merge(users);
			DataWarehouse.getInstance().allUsers.remove(users);
			DataWarehouse.getInstance().allUsers.add(tempSpecimen);
			outcomeEnum = OutcomeEnum.UPDATE_SUCCESS;

			limpiarFiltros();
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error editing: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public void delete() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.UPDATE_ERROR;
		String transactionMessage = users.getUserNames() + users.getUserLastnames();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.USR_INS, getUsers(facesContext),
					Constant.AccessLevel.UPDATE)) {
				showAccessMessage(facesContext, OutcomeEnum.UPDATE_NOT_GRANTED);
				return;
			}

			usersService.delete(users);
			DataWarehouse.getInstance().allUsers.remove(users);
			outcomeEnum = OutcomeEnum.DELETE_SUCCESS;

			limpiarFiltros();
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error deleting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public void restartPassword() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.UPDATE_ERROR;
		String transactionMessage = users.getUserNames() + users.getUserLastnames();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.USR_INS, getUsers(facesContext),
					Constant.AccessLevel.UPDATE)) {
				showAccessMessage(facesContext, OutcomeEnum.UPDATE_NOT_GRANTED);
				return;
			}

			users.setStatus(StatusEnum.BLOQUEADO.get());
			users.setUserPassword(Util.encrypt(Constant.RESTART_PASSWORD));
			users.setUserModified(Calendar.getInstance().getTime());
			Users tempSpecimen = usersService.merge(users);
			DataWarehouse.getInstance().allUsers.remove(users);
			DataWarehouse.getInstance().allUsers.add(tempSpecimen);
			outcomeEnum = OutcomeEnum.UPDATE_SUCCESS;

			limpiarFiltros();
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error deleting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}
	
	public void disableUser() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.UPDATE_ERROR;
		String transactionMessage = users.getUserNames() + users.getUserLastnames();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.USR_INS, getUsers(facesContext),
					Constant.AccessLevel.UPDATE)) {
				showAccessMessage(facesContext, OutcomeEnum.UPDATE_NOT_GRANTED);
				return;
			}

			users.setStatus(StatusEnum.DESHABILITADO.get());
			users.setUserModified(Calendar.getInstance().getTime());
			Users tempSpecimen = usersService.merge(users);
			DataWarehouse.getInstance().allUsers.remove(users);
			DataWarehouse.getInstance().allUsers.add(tempSpecimen);
			outcomeEnum = OutcomeEnum.UPDATE_SUCCESS;

			limpiarFiltros();
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error deleting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
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

	public List<Institution> getAllInstitutions() {
		return DataWarehouse.getInstance().allInstitutions;
	}
}
