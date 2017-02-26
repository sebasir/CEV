package net.hpclab.beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.hpclab.entities.Users;
import net.hpclab.services.DataBaseService;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

@Named
@RequestScoped
public class LoginBean extends UtilsBean implements Serializable {
    private static final long serialVersionUID = 1L;
    public static String USER_SESSION_KEY = "UserKey";
    public static String USER_SESSION_KEY_2 = "DataUser";
    public static String USER_SESSION_DIRECTORY = "RootDirectory";
    public static String USER_SESSION_DIRECTORY_GUIA = "RootGuias";

    private HttpSession session;

    private String user;
    private String pass;
    private Users users;
    private HashMap<String, String> userMenu;
    private DataBaseService<Users> dataBaseService;

    public LoginBean(){
        super(FacesContext.getCurrentInstance());
        try {
            dataBaseService = new DataBaseService<>(Users.class);
        } catch (Exception e) {
            showMessage(Users.class, Actions.createError);
        }
    }

    public String autenticar() {
        if (user != null && !user.equals("")) {
            return "/pages/admin/admin.xhtml?faces-redirect=true";
        }
        return null;
    }

    public void cargar_sesiones() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put(USER_SESSION_KEY_2, user);
        context.getExternalContext().getSessionMap().put(USER_SESSION_KEY, user);
    }

    public void loadMenu() {
        userMenu = new HashMap<>();

        menu_principal = new DefaultMenuModel();
        menu_principal.addElement(getItem("Espécimen", "specimen.xhtml", null));
        menu_principal.addElement(getItem("Contenido de Colección", "specimenManager.xhtml", null));
        menu_principal.addElement(getItem("Ubicaciones", "location.xhtml", null));
        menu_principal.addElement(getItem("Clasificaciones", "taxonomy.xhtml", null));
        menu_principal.addElement(getItem("Autores", "author.xhtml", null));
        menu_principal.addElement(getItem("Variables de Entorno", "envConfig.xhtml", null));
        menu_principal.addElement(getItem("Volver a la Colección", "/index.html", null));
    }

    private DefaultMenuItem getItem(String name, String source, String icon) {
        DefaultMenuItem item = new DefaultMenuItem(name);
        item.setOutcome(source);
        if (icon == null) {
            item.setIcon(icon);
        }
        return item;
    }

    public void ir_login() {
        HttpServletRequest servletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(servletRequest.getContextPath() + servletRequest.getServletPath() + logout());
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String logout() {
        session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "/pages/login.xhtml?faces-redirect=true";
    }

    public Users getUsuario() {
        session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        users = (Users) session.getAttribute("UserKey");
        return users;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getClave() {
        return pass;
    }

    public void setClave(String clave) {
        this.pass = clave;
    }

    public MenuModel getMenu_principal() {
        return menu_principal;
    }

    public void setMenu_principal(MenuModel menu_principal) {
        this.menu_principal = menu_principal;
    }
}
