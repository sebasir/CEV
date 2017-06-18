package net.hpclab.cev.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import net.hpclab.cev.beans.UtilsBean;
import net.hpclab.cev.entities.Users;
import net.hpclab.cev.enums.AuditEnum;
import net.hpclab.cev.enums.AuthenticateEnum;
import net.hpclab.cev.enums.ModulesEnum;
import net.hpclab.cev.enums.StatusEnum;

public class LoginService extends UtilsBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginService.class.getSimpleName());
    private static LoginService loginService;
    private DataBaseService<Users> usersService;

    private LoginService() {
        try {
            usersService = new DataBaseService<>(Users.class);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "El servicio de log no ha podido iniciar correctamente: {0}.", e.getMessage());
        }
    }

    public AuthenticateEnum login(FacesContext facesContext, String username, String password, String domain) throws Exception {
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
                if (!SessionService.isUserOnline(users)) {
                    UserSession userSession = loadUserSession(facesContext, users);
                    SessionService.addUser(getSessionId(facesContext), userSession);
                    if (users.getStatus().equals(StatusEnum.Activo)) {
                        AuditService.getInstance().log(userSession.getUser(), Util.getModule(ModulesEnum.LOGIN), userSession.getIpAddress(), AuditEnum.LOGIN, "Users " + users.getIdUser() + " autenticado.");
                        return AuthenticateEnum.LOGIN_SUCCESS;
                    } else {
                        return AuthenticateEnum.LOGIN_USER_NOT_ACTIVE_ERROR;
                    }
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
