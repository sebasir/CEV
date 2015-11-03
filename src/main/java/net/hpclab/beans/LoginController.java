package net.hpclab.beans;

import java.io.IOException;
import java.util.logging.Level;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.hpclab.entities.Login;
import org.apache.log4j.Logger;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

@ManagedBean
@RequestScoped
public class LoginController {
    public static String USER_SESSION_KEY = "UserKey";
    public static String USER_SESSION_KEY_2 = "DataUser";
    public static String USER_SESSION_DIRECTORY = "RootDirectory";
    public static String USER_SESSION_DIRECTORY_GUIA = "RootGuias";
    private String user;
    private String clave;
    private static final Logger logger = Logger.getLogger(LoginController.class);
    private HttpSession session;
    public Login usuario;
    private MenuModel menu_principal;

    public String autenticar() {
	   if (user != null && !user.equals("")) {
		  return "/pages/admin/admin.xhtml?faces-redirect=true";
	   }
	   return null;
    }

    public void cargar_sesiones() {
        logger.debug("--> Entro al metodo cargar_parametros ");
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put(USER_SESSION_KEY_2, user);
        context.getExternalContext().getSessionMap().put(USER_SESSION_KEY, usuario);
    }
    
    public void cargar_menu() {
        menu_principal = new DefaultMenuModel();
	   menu_principal.addElement(getItem("Espécimen", "/pages/admin/specimen/specimen.xhtml", null));
	   menu_principal.addElement(getItem("Usuarios", "/pages/admin/users/index.xhtml", null));
    }
    
    private DefaultMenuItem getItem(String name, String source, String icon) {
	   DefaultMenuItem item = new DefaultMenuItem(name);
	   item.setOutcome(source);
	   if(icon == null)
		  item.setIcon(icon);
	   return item;
    }

    public void ir_login() {
        HttpServletRequest servletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(servletRequest.getContextPath() + servletRequest.getServletPath() + logout());
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String logout() {
        logger.debug("--> Entro al metodo logout");
        session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        logger.debug("--> Salio del metodo logout");
        return "/pages/login.xhtml?faces-redirect=true";
    }

    public Login getUsuario() {
        session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        usuario = (Login) session.getAttribute("UserKey");
        return usuario;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public MenuModel getMenu_principal() {
        cargar_menu();
        return menu_principal;
    }

    public void setMenu_principal(MenuModel menu_principal) {
        this.menu_principal = menu_principal;
    }
}