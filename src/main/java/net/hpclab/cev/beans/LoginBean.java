package net.hpclab.cev.beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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

@ManagedBean
@SessionScoped
public class LoginBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 1046360575084820953L;
	private static final Logger LOGGER = Logger.getLogger(LoginBean.class.getSimpleName());
	private String domain;
	private String user;
	private String revDomain;
	private String revUser;
	private String password;
	private Users users;
	private List<Modules> userModules;

	public void onLoad() {
		try {
			if (SessionService.getInstance().getUserSession(getSessionId(FacesContext.getCurrentInstance())) != null)
				redirect(Constant.MAIN_ADMIN_PAGE);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error getting UserSession", e);
		}
	}

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
				userModules = AccessService.getInstance().getUserMenu(users);
				redirect(Constant.MAIN_ADMIN_PAGE);
				break;
			case LOGIN_USER_NOT_ACTIVE_ERROR:
				users = SessionService.getInstance().getUserSession(getSessionId(facesContext)).getUser();
				message = MessagesService.getInstance().getMessage(authenticateEnum, users.getStatus());
				logMessage = MessagesService.getInstance().getMessage(authenticateEnum.name() + Constant.LOG,
						users.getStatus());
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

	public void logOut() throws IOException {
		invalidateSession(FacesContext.getCurrentInstance());
		redirect(Constant.LOGIN_PAGE + Constant.FACES_REDIRECT);
	}

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

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUser() {
		return user;
	}

	public String getRevDomain() {
		return revDomain;
	}

	public void setRevDomain(String revDomain) {
		this.revDomain = revDomain;
	}

	public String getRevUser() {
		return revUser;
	}

	public void setRevUser(String revUser) {
		this.revUser = revUser;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public List<Institution> getInstitutions() {
		return DataWarehouse.getInstance().allInstitutions;
	}

	public List<Modules> getUserModules() {
		return userModules;
	}

	public void setUserModules(LinkedList<Modules> userModules) {
		this.userModules = userModules;
	}
}
