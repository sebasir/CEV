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

/**
 * Este servicio permite la interacción con el servicio de
 * <tt>DataBaseService</tt> para la gestión de usuarios del sistema.
 * Principalmente expone métodos de creación, edición, consulta y eliminación,
 * validando la posibilidad de estas operaciones contra el servicio de
 * <tt>AccessesService</tt>, el cual valida el usuario que realiza la operación.
 * 
 * @author Sebasir
 * @since 1.0
 * @see DataBaseService
 * @see Users
 */

@ManagedBean
@ViewScoped
public class UsersBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 501004550803103355L;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>Users</tt>, lo cual permite extender todas las operaciones del servicio
	 * para esta clase.
	 */
	private DataBaseService<Users> usersService;

	/**
	 * Lista de usuarios obtenidos por una consulta
	 */
	private List<Users> searchUsers;

	/**
	 * Objeto que permite la modificación de un usuario
	 */
	private Users users;

	/**
	 * Objeto que permite la consulta de usuarios
	 */
	private Users searchUser;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(UsersBean.class.getSimpleName());

	/**
	 * Constructor que permite inicializar los servicios de <tt>DataBaseService</tt>
	 * y búsqueda de filtros
	 */
	public UsersBean() {
		try {
			usersService = new DataBaseService<>(Users.class);
			limpiarFiltros();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Permite limpiar los filtros de busqueda
	 */
	public void limpiarFiltros() {
		users = new Users();
		searchUser = new Users();
		searchUsers = null;
	}

	/**
	 * @return El paginador de la consulta de usuarios
	 */
	public DataBaseService<Users>.Pager getPager() {
		return usersService.getPager();
	}

	/**
	 * Permite guardar un objeto de tipo usuario que se haya definido en la interfáz
	 * validando el permiso de escritura
	 */
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

	/**
	 * Permite editar un objeto de tipo usuario que se haya redefinido en la
	 * interfáz validando el permiso de modificación
	 */
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
			Users tempUsers = usersService.merge(users);
			DataWarehouse.getInstance().allUsers.remove(users);
			DataWarehouse.getInstance().allUsers.add(tempUsers);
			outcomeEnum = OutcomeEnum.UPDATE_SUCCESS;

			limpiarFiltros();
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error editing: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite eliminar un objeto de tipo usuario validando el permiso de escritura
	 */
	public void delete() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.DELETE_ERROR;
		String transactionMessage = users.getUserNames() + users.getUserLastnames();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.USR_INS, getUsers(facesContext),
					Constant.AccessLevel.DELETE)) {
				showAccessMessage(facesContext, OutcomeEnum.DELETE_NOT_GRANTED);
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

	/**
	 * Permite reiniciar la contraseña de un usuario
	 */
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

	/**
	 * Permite deshabilitar un usuario
	 */
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

	/**
	 * Permite realizar la búsqueda de usuarios dados unos parámetros de búsqueda
	 */
	public void search() {
		try {
			searchUsers = usersService.getList(searchUser);
			if (usersService.getPager().getNumberOfResults() == 0)
				showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR, "No se encontraron datos");
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}
	}

	/**
	 * @return Lista de usuarios obtenidos por una consulta
	 */
	public List<Users> getSearchUsers() {
		return searchUsers;
	}

	/**
	 * @param searchUsers
	 *            Lista de usuarios obtenidos por una consulta a definir
	 */
	public void setSearchUsers(List<Users> searchUsers) {
		this.searchUsers = searchUsers;
	}

	/**
	 * @return Objeto que permite la modificación de un usuario
	 */
	public Users getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            Objeto que permite la modificación de un usuario a definir
	 */
	public void setUsers(Users users) {
		this.users = users;
	}

	/**
	 * @return Objeto que permite la consulta de usuarios
	 */
	public Users getSearchUser() {
		return searchUser;
	}

	/**
	 * @param searchUser
	 *            Objeto que permite la consulta de usuarios a definir
	 */
	public void setSearchUser(Users searchUser) {
		this.searchUser = searchUser;
	}

	/**
	 * @return Permite el acceso a todas las intituciones del sistema
	 */
	public List<Institution> getAllInstitutions() {
		return DataWarehouse.getInstance().allInstitutions;
	}
}
