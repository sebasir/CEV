package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import net.hpclab.cev.entities.Institution;
import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.entities.Users;
import net.hpclab.cev.enums.AuditEnum;
import net.hpclab.cev.enums.DataBaseEnum;
import net.hpclab.cev.services.AuditService;
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.UserSession;
import net.hpclab.cev.services.Util;

@Named
@SessionScoped
public class LoginBean extends UtilsBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginBean.class.getSimpleName());
    private String domain;
    private String user;
    private String pass;
    private Users users;
    private HashMap<String, String> userMenu;
    private DataBaseService<Users> usersService;
    private List<Institution> institutions;

    public LoginBean() {
        super(FacesContext.getCurrentInstance());
    }

    @PostConstruct
    public void init() {
        try {
            usersService = new DataBaseService<>(Users.class);
            institutions = Util.getInstitutions();
        } catch (Exception e) {
            showDataBaseMessage(DataBaseEnum.DB_INIT_ERROR, e.getMessage());
        }
    }

    public void authenticate() {
        if (Util.isEmpty(user) || Util.isEmpty(pass)) {
            showDataBaseMessage(DataBaseEnum.LOGIN_ERROR, "Ni el Usuario ni la Contraseña pueden estar vacías");
            LOGGER.log(Level.WARNING, "Usuario o contraseña vacías");
        } else {
            if (usersService != null && usersService.isConnected()) {
                try {
                    HashMap<String, Object> params = new HashMap<>(2);
                    params.put(":userEmail", user + domain);
                    params.put(":userPassword", Util.encrypt(pass));
                    users = usersService.getSingleRecord("Users.authenticate", params);
                    if (users != null) {
                        if (users.getStatus().equals(StatusEnum.ACTIVO.get())) {
                            loadUserSession(users);
                            showDataBaseMessage(DataBaseEnum.LOGIN_SUCCESS, users.getUserNames() + " " + users.getUserLastnames());
                            LOGGER.log(Level.INFO, "Users {0} autenticado.", users.getIdUser());
                            AuditService.getInstance().log(users, new Modules(2), UserSession.ipAddress, AuditEnum.LOGIN, "Users " + users.getIdUser() + " autenticado.");
                        } else {
                            showDataBaseMessage(DataBaseEnum.LOGIN_ERROR, "Tu estado actual es: " + users.getStatus());
                            LOGGER.log(Level.INFO, "Error autenticando: Users status = {0}", users.getStatus());
                        }
                    } else {
                        showDataBaseMessage(DataBaseEnum.LOGIN_ERROR, "Asegurate de que los valores ingresados sean correctos.");
                        LOGGER.log(Level.INFO, "Error autenticando: Users null");
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.INFO, "Error autenticando: {0}", e.getMessage());
                    showDataBaseMessage(DataBaseEnum.LOGIN_ERROR, "Intenta nuevamente");
                }
            } else {
                showDataBaseMessage(DataBaseEnum.DB_INIT_ERROR, "Error inicializando conexión a base de datos.");
            }
        }
    }

    public void loadMenu() {
        userMenu = new HashMap<>();
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

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public HashMap<String, String> getUserMenu() {
        return userMenu;
    }

    public void setUserMenu(HashMap<String, String> userMenu) {
        this.userMenu = userMenu;
    }

    public List<Institution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }
}
