package net.hpclab.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.hpclab.entities.AuditEnum;
import net.hpclab.entities.Modules;
import net.hpclab.entities.ModulesUsers;
import net.hpclab.entities.RolesModules;
import net.hpclab.entities.RolesUsers;
import net.hpclab.entities.StatusEnum;

public class AccessService implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AccessService.class.getSimpleName());
    private static final Modules MODULE = new Modules(14);
    private static AccessService accessService;
    private DataBaseService<ModulesUsers> mousService;
    private DataBaseService<RolesUsers> rousService;
    private DataBaseService<RolesModules> romoService;
    private HashMap<Integer, HashMap<Integer, Integer>> effectiveAccess;
    private List<ModulesUsers> modulesUsers;
    private List<RolesUsers> rolesUsers;
    private List<RolesModules> rolesModules;

    static {
        try {
            accessService = new AccessService();
            accessService.mousService = new DataBaseService<>(ModulesUsers.class);
            accessService.rousService = new DataBaseService<>(RolesUsers.class);
            accessService.romoService = new DataBaseService<>(RolesModules.class);
            accessService.effectiveAccess = new HashMap<>();
            accessService.loadAccesses();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "El servicio de acceso no ha podido iniciar correctamente: {0}.", e.getMessage());
        }
    }

    public void loadAccesses() throws Exception {
        modulesUsers = mousService.getList("ModulesUsers.listAll");
        rolesUsers = rousService.getList("RolesUsers.listAll");
        rolesModules = romoService.getList("RolesModules.listAll");
        HashMap<Integer, HashSet<Integer>> RoleModule = new HashMap<>();
        for (RolesModules r : rolesModules) {
            if (RoleModule.get(r.getIdRole().getIdRole()) == null) {
                RoleModule.put(r.getIdRole().getIdRole(), new HashSet<Integer>());
            }
            RoleModule.get(r.getIdRole().getIdRole()).add(r.getIdModule().getIdModule());
        }

        for (RolesUsers r : rolesUsers) {
            if (effectiveAccess.get(r.getIdUser().getIdUser()) == null) {
                effectiveAccess.put(r.getIdUser().getIdUser(), new HashMap<Integer, Integer>());
            }
            for (Integer mod : RoleModule.get(r.getIdRole().getIdRole())) {
                effectiveAccess.get(r.getIdUser().getIdUser()).put(mod, r.getAccessLevel());
            }
        }

        for (ModulesUsers r : modulesUsers) {
            if (effectiveAccess.get(r.getIdUser().getIdUser()) == null) {
                effectiveAccess.put(r.getIdUser().getIdUser(), new HashMap<Integer, Integer>());
            }
            effectiveAccess.get(r.getIdUser().getIdUser()).put(r.getIdModule().getIdModule(), r.getAccessLevel());
        }
    }

    public void setUserAccess(Integer idModule, Integer idUser, Integer accessLevel, AuditEnum operation, StatusEnum status) throws Exception {
        switch (operation) {
            case INSERT:
                break;
            default:
                throw new IllegalArgumentException("Operación desconocida");
        }
        AuditService.getInstance().log(UserSession.user, MODULE, UserSession.ipAddress, operation, "idModule = " + idModule + ", idUser = " + idUser + ", accessLevel = " + accessLevel);
    }

    public void setUserRoleAccess(Integer idModule, Integer idUser, Integer accessLevel) throws Exception {

    }

    public void setModuleRole(Integer idModule, Integer idRole) {

    }

    public int accessLevel(Integer idModule, Integer idUser) {
        try {
            return effectiveAccess.get(idUser).get(idModule);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "El usuario {0}, intenta acceder al módulo {1}: {2}", new Object[]{idUser, idModule, e.getMessage()});
            return 0;
        }
    }

    public static AccessService getInstance() {
        return accessService;
    }

    public List<ModulesUsers> getModulesUsers() {
        return modulesUsers;
    }

    public void setModulesUsers(List<ModulesUsers> aModulesUsers) {
        modulesUsers = aModulesUsers;
    }

    public List<RolesUsers> getRolesUsers() {
        return rolesUsers;
    }

    public void setRolesUsers(List<RolesUsers> aRolesUsers) {
        rolesUsers = aRolesUsers;
    }

    public List<RolesModules> getRolesModules() {
        return rolesModules;
    }

    public void setRolesModules(List<RolesModules> aRolesModules) {
        rolesModules = aRolesModules;
    }
}
