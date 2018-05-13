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

package net.hpclab.cev.services;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

import javax.faces.context.FacesContext;

import net.hpclab.cev.beans.UtilsBean;
import net.hpclab.cev.entities.Users;
import net.hpclab.cev.enums.AuditEnum;
import net.hpclab.cev.enums.AuthenticateEnum;
import net.hpclab.cev.enums.ModulesEnum;
import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.model.UserSession;

/**
 * Es un servicio creado para la encapsulación del servicio de inicio de sesión,
 * contrastando contra la base de datos. El servicio ofrece una manera de acceso
 * único a través de un objeto <tt>Singleton</tt> creado en memoria estática, y
 * sincronizada, permitiendo acceder la misma instancia desde varios hilos
 * simultaneamente.
 * 
 * @since 1.0
 * @author Sebasir
 * @see DataBaseService
 * @see AuthenticateEnum
 * 
 */
public class LoginService extends UtilsBean implements Serializable {

	private static final long serialVersionUID = -8531755011203694863L;

	/**
	 * Objeto estático de esta clase el cual permite acceder siempre a la misma
	 * referencia.
	 */
	private static LoginService loginService;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>Users</tt>, lo cual permite extender todas las operaciones del servicio
	 * para esta clase.
	 */
	private DataBaseService<Users> usersService;

	/**
	 * Función que permite realizar las operaciones de validación de un usuario
	 * registrado contra la base de datos, obteniendo desde una enumeración
	 * <tt>AuthenticateEnum</tt>.
	 * 
	 * @param facesContext
	 *            Contexto de preparación del ciclo de vida JSF
	 * @param username
	 *            Nombre del usuario recuperado desde el formulario de inicio de
	 *            sesión
	 * @param password
	 *            Contraseña del usuario recuperada desde el formulario de inicio de
	 *            sesió
	 * @param domain
	 *            Dominio del correo del usuario
	 * @return Valor de la enumeración indicando el resultado de la operación de
	 *         login
	 * @throws Exception
	 *             En caso de obtener un error en los servicios de
	 *             <tt>DataBaseService</tt>, <tt>SessionService</tt>,
	 *             <tt>AuditService</tt>
	 */
	public AuthenticateEnum login(FacesContext facesContext, String username, String password, String domain)
			throws Exception {
		usersService = new DataBaseService<>(Users.class);
		if (Util.isEmpty(username) || Util.isEmpty(password)) {
			return AuthenticateEnum.LOGIN_ERROR;
		} else if (!Util.checkEmail(username + domain)) {
			return AuthenticateEnum.LOGIN_INVALID_FORMAT_ERROR;
		} else if (usersService != null && usersService.isConnected()) {
			HashMap<String, Object> params = new HashMap<>(2);
			params.put("userEmail", username + domain);
			params.put("userPassword", Util.encrypt(password));
			Users users = usersService.getSingleRecord("Users.authenticate", params);
			if (users != null) {
				if (!SessionService.getInstance().isUserOnline(users)) {
					UserSession userSession = null;
					if (facesContext != null) {
						userSession = loadUserSession(facesContext, users);
						SessionService.getInstance().addUser(getSessionId(facesContext), userSession);
					}
					if (users.getStatus().equals(StatusEnum.ACTIVO.get())) {
						users.setUserLastLogin(Calendar.getInstance().getTime());
						users = usersService.merge(users);
						userSession.setUser(users);
						AuditService.getInstance().log(userSession.getUser(), Util.getModule(ModulesEnum.LOGIN),
								userSession.getIpAddress(), AuditEnum.LOGIN,
								"Users " + users.getIdUser() + " autenticado.");
						return AuthenticateEnum.LOGIN_SUCCESS;
					} else if (Constant.RESTART_PASSWORD.equals(users.getUserPassword()))
						return AuthenticateEnum.LOGIN_RESTART_PASSWORD;
					else
						return AuthenticateEnum.LOGIN_USER_NOT_ACTIVE_ERROR;
				} else {
					return AuthenticateEnum.LOGIN_USER_LOGGED_IN_ERROR;
				}
			} else {
				return AuthenticateEnum.LOGIN_UNKNOWN_ERROR;
			}
		} else {
			return AuthenticateEnum.DB_INIT_ERROR;
		}
	}

	/**
	 * Método sincronizado que permite obtener la instancia <tt>Singleton</tt>,
	 * inicializandola en caso de no estar inicializada.
	 * 
	 * @return Objeto de instancia <tt>Singleton</tt> de la clase.
	 */
	public static synchronized LoginService getInstance() {
		return loginService == null ? (loginService = new LoginService()) : loginService;
	}
}
