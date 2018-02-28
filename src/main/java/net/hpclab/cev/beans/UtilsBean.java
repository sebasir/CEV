package net.hpclab.cev.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;

import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.entities.Users;
import net.hpclab.cev.enums.AuthenticateEnum;
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.SessionService;
import net.hpclab.cev.services.UserSession;
import net.hpclab.cev.services.Util;

public class UtilsBean implements Serializable {

	private static final long serialVersionUID = 8618879958425181360L;

	public UserSession loadUserSession(FacesContext facesContext, Users user) {
		UserSession userSession = new UserSession(user, getRemoteAddress(facesContext));
		facesContext.getExternalContext().getSessionMap().put(Constant.USER_DATA, userSession);
		return userSession;
	}

	public UserSession getUserSession(FacesContext facesContext) {
		return (UserSession) facesContext.getExternalContext().getSessionMap().get(Constant.USER_DATA);
	}

	public String getSessionId(FacesContext facesContext) {
		return facesContext.getExternalContext().getSessionId(false);
	}

	public void redirect(String destination) throws IOException {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		externalContext.redirect(externalContext.getRequestContextPath() + destination);
	}

	public void invalidateSession(FacesContext facesContext) {
		SessionService.getInstance().removeUser(getSessionId(facesContext));
		facesContext.getExternalContext().getSessionMap().clear();
		facesContext.getExternalContext().invalidateSession();
	}

	private String getRemoteAddress(FacesContext facesContext) {
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		String forwardedFor = request.getHeader("X-FORWARDED-FOR");

		if (!Util.isEmpty(forwardedFor)) {
			return forwardedFor.split("\\s*,\\s*", 2)[0];
		}
		return request.getRemoteAddr();
	}

	public Users getUsers(FacesContext facesContext) {
		return getUserSession(facesContext).getUser();
	}

	@SuppressWarnings("unchecked")
	public List<Modules> getUserModules(FacesContext facesContext) {
		List<Modules> modules = null;
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
		if (null != session)
			modules = (List<Modules>) facesContext.getExternalContext().getSessionMap().get(Constant.USER_MODULES);
		return modules;
	}

	public <T> T getExternalBean(FacesContext facesContext, Class<T> beanClass) {
		ELContext elContext = facesContext.getELContext();
		String beanName = beanClass.getSimpleName();
		beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
		@SuppressWarnings("unchecked")
		T resultBean = (T) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null,
				beanName);
		return resultBean;
	}

	public void setUserModules(FacesContext facesContext, List<Modules> userMenu) {
		facesContext.getExternalContext().getSessionMap().put(Constant.USER_MODULES, userMenu);
	}

	public String formatDate(Date date) {
		return formatDate(date, "dd/MM/yyyy");
	}

	public String formatDate(Date date, String format) {
		String retorno;
		try {
			if (date == null) {
				retorno = "--";
			} else {
				retorno = new SimpleDateFormat(format).format(date);
			}
		} catch (Exception e) {
			retorno = date != null ? date.toString() : null;
		}
		return retorno;
	}

	public void showMessage(FacesContext facesContext, OutcomeEnum action, String message) {
		FacesMessage fMess;
		switch (action) {
		case CREATE_SUCCESS:
			fMess = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro Creado.",
					"Se ha creado el registro satisfactoriamente!");
			break;
		case DELETE_SUCCESS:
			fMess = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro eliminado.",
					"Se ha eliminado el registro satisfactoriamente!");
			break;
		case UPDATE_SUCCESS:
			fMess = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro actualizado.",
					"Se ha actualizado el registro satisfactoriamente!");
			break;
		case CREATE_ERROR:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al crear.",
					"Ha fallado la creación del registro, (" + message + ")");
			break;
		case DELETE_ERROR:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar.",
					"Ha fallado la eliminación del registro, (" + message + ")");
			break;
		case UPDATE_ERROR:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al actualizar.",
					"Ha fallado la actualización del registro, (" + message + ")");
			break;
		case GENERIC_INFO:
			fMess = new FacesMessage(FacesMessage.SEVERITY_INFO, "Información", message);
			break;
		case GENERIC_ERROR:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error general.", message);
			break;
		default:
			fMess = new FacesMessage(FacesMessage.SEVERITY_WARN, "ET llama a casa",
					"Hola. Probando un mensaje no contemplado!!");
			break;
		}
		facesContext.addMessage(null, fMess);
	}

	public void showAccessMessage(FacesContext facesContext, OutcomeEnum action) {
		FacesMessage fMess;
		switch (action) {
		case DELETE_NOT_GRANTED:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sin permisos",
					"No tienes el permiso de eliminación.");
			break;
		case INSERT_NOT_GRANTED:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sin permisos", "No tienes el permiso de creación.");
			break;
		case UPDATE_NOT_GRANTED:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sin permisos",
					"No tienes el permiso de actualización.");
			break;
		case SELECT_NOT_GRANTED:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sin permisos", "No tienes el permiso de consulta.");
			break;
		default:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sin permisos", "No tienes el permiso de nada!!");
			break;
		}
		facesContext.addMessage(null, fMess);
	}

	public void showFileMessage(FacesContext facesContext, OutcomeEnum action, String fileName, String message) {
		FacesMessage fMess;
		switch (action) {
		case FILE_UPLOAD_SUCCESS:
			fMess = new FacesMessage(FacesMessage.SEVERITY_INFO, "Archivo adjuntado",
					"Se ha adjuntado el archivo " + fileName + " satisfactoriamente!");
			break;
		case FILE_UPLOAD_ERROR:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Archivo no adjuntado",
					"Se ha producido un error adjuntando el archivo " + fileName + ", (" + message + ")");
			break;
		case FILE_DELETE_SUCCESS:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Archivo eliminado",
					"Se ha eliminado el archivo " + fileName + "!");
			break;
		case FILE_DELETE_ERROR:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Archivo no eliminado",
					"Se ha producido un error eliminando el archivo " + fileName + ", (" + message + ")");
			break;
		case FILE_REQUIRED:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Archivo no adjunto",
					"El archivo " + fileName + " es obligatorio y debe adjuntarse.");
			break;
		default:
			fMess = new FacesMessage(FacesMessage.SEVERITY_WARN, "ET llama a casa",
					"Hola. Probando un mensaje no contemplado!!");
			break;
		}
		facesContext.addMessage(null, fMess);
	}

	public void showAuthenticationMessage(FacesContext facesContext, AuthenticateEnum action, String message) {
		FacesMessage fMess;
		switch (action) {
		case DB_INIT_ERROR:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error conectando a base de datos",
					"(" + message + ")");
			break;
		case LOGIN_ERROR:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error autenticando",
					"Se ha producido un error autenticando el usuario, (" + message + ")");
			break;
		case LOGIN_SUCCESS:
			fMess = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido, " + message,
					"Has ingresado correctamente al sistema!");
			break;
		case LOGIN_INVALID_FORMAT_ERROR:
		case LOGIN_USER_LOGGED_IN_ERROR:
		case LOGIN_UNKNOWN_ERROR:
			fMess = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error autenticando", message);
			break;
		default:
			fMess = new FacesMessage(FacesMessage.SEVERITY_WARN, "ET llama a casa",
					"Hola. Probando un mensaje no contemplado!! [" + action + "]");
			break;
		}
		facesContext.addMessage(null, fMess);
	}

	public byte[] getByteArray(InputStream in) throws IOException {
		return IOUtils.toByteArray(in);
	}

	public InputStream getInputStream(byte[] byteContent) throws IOException {
		return new ByteArrayInputStream(byteContent);
	}
}
