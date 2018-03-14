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

public class LoginService extends UtilsBean implements Serializable {

	private static final long serialVersionUID = -8531755011203694863L;
	private static LoginService loginService;
	private DataBaseService<Users> usersService;

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
					UserSession userSession = loadUserSession(facesContext, users);
					SessionService.getInstance().addUser(getSessionId(facesContext), userSession);
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

	public static synchronized LoginService getInstance() {
		return loginService == null ? (loginService = new LoginService()) : loginService;
	}
}
