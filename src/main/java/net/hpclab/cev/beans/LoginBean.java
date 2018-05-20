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

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;

import net.hpclab.cev.entities.Institution;
import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.entities.Users;
import net.hpclab.cev.enums.AuthenticateEnum;
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.services.AccessService;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.DataWarehouse;
import net.hpclab.cev.services.LoginService;
import net.hpclab.cev.services.MessagesService;
import net.hpclab.cev.services.SessionService;
import net.hpclab.cev.services.Util;

/**
 * Este servicio permite la interacción con el servicio de
 * <tt>DataBaseService</tt> para la gestión de ingreso al sistema.
 * Principalmente expone el método de login, logout, y recoverPassword
 * 
 * @author Sebasir
 * @since 1.0
 * @see DataBaseService
 * @see SessionService
 */

@ManagedBean
@ViewScoped
public class LoginBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 1046360575084820953L;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(LoginBean.class.getSimpleName());

	/**
	 * Cadena de texto para el dominio del formulario de ingreso
	 */
	private String domain;

	/**
	 * Cadena de texto para el usuario del formulario de ingreso
	 */
	private String user;

	/**
	 * Cadena de texto para el dominio del formulario de recuperación
	 */
	private String revDomain;

	/**
	 * Cadena de texto para el usuario del formulario de recuperación
	 */
	private String revUser;

	/**
	 * Cadena de texto para la constraseña del formulario de ingreso
	 */
	private String password;

	/**
	 * Objeto que permite consultar la base de datos con los datos del formulario
	 */
	private Users users;

	/**
	 * Permite redireccionar a la página inicial si el usuario ya se encontraba
	 * loggedin
	 */
	public void onLoad() {
		try {
			if (SessionService.getInstance().getUserSession(getSessionId(FacesContext.getCurrentInstance())) != null)
				redirect(Constant.MAIN_ADMIN_PAGE);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error getting UserSession", e);
		}
	}

	/**
	 * Permite realizar la autenticación basándose en el servicio de
	 * <tt>LoginService</tt>
	 */
	public void authenticate() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			String message, logMessage;
			AuthenticateEnum authenticateEnum = LoginService.getInstance().login(facesContext, user, password, domain);
			switch (authenticateEnum) {
			case LOGIN_SUCCESS:
				users = SessionService.getInstance().getUserSession(getSessionId(facesContext)).getUser();
				message = MessagesService.getInstance().getMessage(authenticateEnum, users.getUserNames(),
						users.getUserLastnames());
				logMessage = MessagesService.getInstance().getMessage(authenticateEnum.name() + Constant.LOG,
						users.getIdUser());
				super.setUserModules(facesContext, AccessService.getInstance().getUserMenu(users));
				redirect(Constant.MAIN_ADMIN_PAGE);
				break;
			case LOGIN_USER_NOT_ACTIVE_ERROR:
				users = SessionService.getInstance().getUserSession(getSessionId(facesContext)).getUser();
				message = MessagesService.getInstance().getMessage(authenticateEnum, users.getStatus());
				logMessage = MessagesService.getInstance().getMessage(authenticateEnum.name() + Constant.LOG,
						users.getStatus());
				break;
			case LOGIN_RESTART_PASSWORD:
				users = SessionService.getInstance().getUserSession(getSessionId(facesContext)).getUser();
				message = MessagesService.getInstance().getMessage(authenticateEnum, users.getStatus());
				logMessage = MessagesService.getInstance().getMessage(authenticateEnum.name() + Constant.LOG,
						users.getIdUser());
				break;
			default:
				message = MessagesService.getInstance().getMessage(authenticateEnum);
				logMessage = MessagesService.getInstance().getMessage(authenticateEnum.name() + Constant.LOG);
				break;
			}
			showAuthenticationMessage(facesContext, authenticateEnum, message);
			LOGGER.log(Level.INFO, logMessage);

		} catch (NoResultException e) {
			LOGGER.log(Level.INFO, "Error autenticando: {0}", e.getMessage());
			showAuthenticationMessage(facesContext, AuthenticateEnum.LOGIN_UNKNOWN_ERROR,
					MessagesService.getInstance().getMessage(AuthenticateEnum.LOGIN_UNKNOWN_ERROR));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.log(Level.INFO, "Error autenticando: {0}", e.getMessage());
			showAuthenticationMessage(facesContext, AuthenticateEnum.LOGIN_ERROR, "Intenta nuevamente");
		}
	}

	/**
	 * Permite cerrar la sesión y todo trabajo pendiente
	 * 
	 * @throws IOException
	 *             Si no fue posible invalidar la sesión del usuario
	 */
	public void logOut() throws IOException {
		invalidateSession(FacesContext.getCurrentInstance());
		redirect(Constant.MAIN_PAGE + Constant.FACES_REDIRECT);
	}

	/**
	 * Permite recuperar la contraseña del usuario
	 */
	public void recoverPassword() {
		if (Util.isEmpty(revDomain) || Util.isEmpty(revUser)) {
			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR,
					"Se necesitan todos los datos para continuar!");
		} else if (!Util.checkEmail(revUser + revDomain)) {
			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_ERROR,
					"Se necesitan un nombre de usuario válido!");
		} else {
			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_INFO,
					"Se ha enviado un mensaje al correo con la información de recuperación!");
		}
	}

	/**
	 * @return Cadena de texto para el dominio del formulario de ingreso
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain
	 *            Cadena de texto para el dominio del formulario de ingreso a
	 *            definir
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @return Cadena de texto para el usuario del formulario de ingreso
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return Cadena de texto para el dominio del formulario de recuperación
	 */
	public String getRevDomain() {
		return revDomain;
	}

	/**
	 * @param revDomain
	 *            Cadena de texto para el dominio del formulario de recuperación a
	 *            definir
	 */
	public void setRevDomain(String revDomain) {
		this.revDomain = revDomain;
	}

	/**
	 * @return Cadena de texto para el usuario del formulario de recuperación
	 */
	public String getRevUser() {
		return revUser;
	}

	/**
	 * @param revUser
	 *            Cadena de texto para el usuario del formulario de recuperación a
	 *            definir
	 */
	public void setRevUser(String revUser) {
		this.revUser = revUser;
	}

	/**
	 * @return Cadena de texto para la constraseña del formulario de ingreso
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            Cadena de texto para la constraseña del formulario de ingreso a
	 *            definir
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param user
	 *            Cadena de texto para el usuario del formulario de ingreso a
	 *            definir
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @param users
	 *            Objeto que permite consultar la base de datos con los datos del
	 *            formulario a definir
	 */
	public void setUsers(Users users) {
		this.users = users;
	}

	/**
	 * @return Permite tener acceso a todas las intituciones del sistema
	 */
	public List<Institution> getInstitutions() {
		return DataWarehouse.getInstance().allInstitutions;
	}

	/**
	 * @return Permite obtener el usuario que se ha loggeado
	 */
	public Users getUsers() {
		return super.getUsers(FacesContext.getCurrentInstance());
	}

	/**
	 * @return Permite tener todos los módulos del usuario
	 */
	public List<Modules> getUserModules() {
		return super.getUserModules(FacesContext.getCurrentInstance());
	}
}
