package net.hpclab.cev.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.hpclab.cev.entities.AuditEnum;
import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.entities.ModulesUsers;
import net.hpclab.cev.entities.Roles;
import net.hpclab.cev.entities.RolesModules;
import net.hpclab.cev.entities.RolesUsers;
import net.hpclab.cev.entities.StatusEnum;
import net.hpclab.cev.entities.Users;
import net.hpclab.cev.exceptions.RecordAlreadyExistsException;
import net.hpclab.cev.exceptions.RecordNotExistsException;
import net.hpclab.cev.exceptions.RestrictedAccessException;
import net.hpclab.cev.exceptions.UnexpectedOperationException;

public class AccessService implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AccessService.class.getSimpleName());
    private static AccessService accessService;
    private DataBaseService<ModulesUsers> mousService;
    private DataBaseService<RolesUsers> rousService;
    private DataBaseService<RolesModules> romoService;
    private HashMap<Integer, HashMap<Integer, Integer>> userAccess;
    private HashMap<Integer, HashMap<Integer, Integer>> roleUserAccess;
    private HashMap<Integer, HashSet<Integer>> roleModuleAccess;
    private List<ModulesUsers> modulesUsers;
    private List<RolesUsers> rolesUsers;
    private List<RolesModules> rolesModules;

    static {
        try {
            accessService = new AccessService();
            accessService.mousService = new DataBaseService<>(ModulesUsers.class);
            accessService.rousService = new DataBaseService<>(RolesUsers.class);
            accessService.romoService = new DataBaseService<>(RolesModules.class);
            accessService.userAccess = new HashMap<>();
            accessService.roleUserAccess = new HashMap<>();
            accessService.roleModuleAccess = new HashMap<>();
            accessService.loadAccesses();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "El servicio de acceso no ha podido iniciar correctamente: {0}.", e.getMessage());
        }
    }

    public void loadAccesses() throws Exception {
        userAccess.clear();
        roleUserAccess.clear();
        roleModuleAccess.clear();
        modulesUsers = mousService.getList("ModulesUsers.listAll");
        rolesUsers = rousService.getList("RolesUsers.listAll");
        rolesModules = romoService.getList("RolesModules.listAll");
        for (RolesModules r : rolesModules) {
            addRoleModuleAccess(r.getIdRole().getIdRole(), r.getIdModule().getIdModule());
        }

        for (RolesUsers r : rolesUsers) {
            addRoleUserAccess(r.getIdUser().getIdUser(), r.getIdRole().getIdRole(), r.getAccessLevel());
            for (Integer idModule : roleModuleAccess.get(r.getIdRole().getIdRole())) {
                addUserAccess(r.getIdUser().getIdUser(), idModule, r.getAccessLevel());
            }
        }

        for (ModulesUsers r : modulesUsers) {
            addUserAccess(r.getIdUser().getIdUser(), r.getIdModule().getIdModule(), r.getAccessLevel());
        }
    }

    public void setModuleUserAccess(Integer idModule, Integer idUser, Integer accessLevel, AuditEnum operation, StatusEnum status) throws Exception {
        ModulesUsers moduleUser;
        HashMap<String, Object> params;
        switch (operation) {
            case INSERT:
                if (userAccess.get(idUser).get(idModule) != null) {
                    throw new RecordAlreadyExistsException("Ya existe esta relación");
                }
                moduleUser = new ModulesUsers();
                moduleUser.setIdModule(new Modules(idModule));
                moduleUser.setIdUser(new Users(idUser));
                moduleUser.setAccessLevel(accessLevel);
                mousService.persist(moduleUser);
                addUserAccess(idUser, idModule, accessLevel);
                break;
            case DELETE:
                if (userAccess.get(idUser).get(idModule) == null) {
                    throw new RecordNotExistsException("No existe esta relación");
                }
                params = new HashMap<>(2);
                params.put(":idModule", idModule);
                params.put(":idUser", idUser);
                moduleUser = mousService.getSingleRecord("ModuleUser.findByKey", params);
                mousService.delete(moduleUser);
                removeUserAccess(idUser, idModule);
                break;
            case UPDATE:
                if (userAccess.get(idUser).get(idModule) == null) {
                    throw new RecordNotExistsException("No existe esta relación");
                }
                params = new HashMap<>(2);
                params.put(":idModule", idModule);
                params.put(":idUser", idUser);
                moduleUser = mousService.getSingleRecord("ModuleUser.findByKey", params);
                moduleUser.setAccessLevel(accessLevel);
                mousService.merge(moduleUser);
                removeUserAccess(idUser, idModule);
                addUserAccess(idUser, idModule, accessLevel);
                break;
            case STATUS_CHANGE:
                if (userAccess.get(idUser).get(idModule) == null) {
                    throw new RecordNotExistsException("No existe esta relación");
                }
                params = new HashMap<>(2);
                params.put(":idModule", idModule);
                params.put(":idUser", idUser);
                moduleUser = mousService.getSingleRecord("ModuleUser.findByKey", params);
                moduleUser.setStatus(status.get());
                mousService.merge(moduleUser);
                if (userAccess.get(idUser) == null || userAccess.get(idUser).get(idModule) == null) {
                    addUserAccess(idUser, idModule, accessLevel);
                } else {
                    removeUserAccess(idUser, idModule);
                }
                break;
            default:
                throw new IllegalArgumentException("Operación desconocida");
        }
        AuditService.getInstance().log(UserSession.user, UserSession.module, UserSession.ipAddress, operation, "idModule = " + idModule + ", idUser = " + idUser + ", accessLevel = " + accessLevel);
    }

    public void setRoleUserAccess(Integer idRole, Integer idUser, Integer accessLevel, AuditEnum operation, StatusEnum status) throws Exception {
        RolesUsers roleUser;
        HashMap<String, Object> params;
        switch (operation) {
            case INSERT:
                if (roleUserAccess.get(idUser).get(idRole) != null) {
                    throw new RecordAlreadyExistsException("Ya existe esta relación");
                }
                roleUser = new RolesUsers();
                roleUser.setIdRole(new Roles(idRole));
                roleUser.setIdUser(new Users(idUser));
                roleUser.setAccessLevel(accessLevel);
                rousService.persist(roleUser);
                addRoleUserAccess(idUser, idRole, accessLevel);
                break;
            case DELETE:
                if (userAccess.get(idUser).get(idRole) == null) {
                    throw new RecordNotExistsException("No existe esta relación");
                }
                params = new HashMap<>(2);
                params.put(":idRole", idRole);
                params.put(":idUser", idUser);
                roleUser = rousService.getSingleRecord("RoleUser.findByKey", params);
                rousService.delete(roleUser);
                removeRoleUserAccess(idUser, idRole);
                break;
            case UPDATE:
                if (userAccess.get(idUser).get(idRole) == null) {
                    throw new RecordNotExistsException("No existe esta relación");
                }
                params = new HashMap<>(2);
                params.put(":idRole", idRole);
                params.put(":idUser", idUser);
                roleUser = rousService.getSingleRecord("RoleUser.findByKey", params);
                roleUser.setAccessLevel(accessLevel);
                rousService.merge(roleUser);
                removeRoleUserAccess(idUser, idRole);
                addRoleUserAccess(idUser, idRole, accessLevel);
                break;
            case STATUS_CHANGE:
                if (userAccess.get(idUser).get(idRole) == null) {
                    throw new RecordNotExistsException("No existe esta relación");
                }
                params = new HashMap<>(2);
                params.put(":idRole", idRole);
                params.put(":idUser", idUser);
                roleUser = rousService.getSingleRecord("RoleUser.findByKey", params);
                roleUser.setStatus(status.get());
                rousService.merge(roleUser);

                if (roleUserAccess.get(idUser) == null || roleUserAccess.get(idUser).get(idRole) == null) {
                    addRoleUserAccess(idUser, idRole, accessLevel);
                } else {
                    removeRoleUserAccess(idUser, idRole);
                }
                break;
            default:
                throw new IllegalArgumentException("Operación desconocida");
        }
        AuditService.getInstance().log(UserSession.user, UserSession.module, UserSession.ipAddress, operation, "idRole = " + idRole + ", idUser = " + idUser + ", accessLevel = " + accessLevel);
    }

    public void setRoleModule(Integer idRole, Integer idModule, AuditEnum operation, StatusEnum status) throws Exception {
        RolesModules roleModule;
        HashMap<String, Object> params;
        switch (operation) {
            case INSERT:
                if (roleModuleAccess.get(idRole).contains(idModule)) {
                    throw new RecordAlreadyExistsException("Ya existe esta relación");
                }
                roleModule = new RolesModules();
                roleModule.setIdRole(new Roles(idRole));
                roleModule.setIdModule(new Modules(idModule));
                romoService.persist(roleModule);
                addRoleModuleAccess(idRole, idModule);
                break;
            case DELETE:
                if (!roleModuleAccess.get(idRole).contains(idModule)) {
                    throw new RecordNotExistsException("No existe esta relación");
                }
                params = new HashMap<>(2);
                params.put(":idRole", idRole);
                params.put(":idModule", idModule);
                roleModule = romoService.getSingleRecord("RoleModule.findByKey", params);
                romoService.delete(roleModule);
                removeRoleModuleAccess(idRole, idModule);
                break;
            case UPDATE:
                throw new UnexpectedOperationException("Nada que actualizar.");
            case STATUS_CHANGE:
                if (roleModuleAccess.get(idRole).contains(idModule)) {
                    throw new RecordNotExistsException("No existe esta relación");
                }
                params = new HashMap<>(2);
                params.put(":idRole", idRole);
                params.put(":idModule", idModule);
                roleModule = romoService.getSingleRecord("RoleUser.findByKey", params);
                roleModule.setStatus(status.get());
                romoService.merge(roleModule);

                if (roleModuleAccess.get(idRole) == null || !roleModuleAccess.get(idRole).contains(idModule)) {
                    addRoleModuleAccess(idRole, idModule);
                } else {
                    removeRoleModuleAccess(idRole, idModule);
                }
                break;
            default:
                throw new IllegalArgumentException("Operación desconocida");
        }
        AuditService.getInstance().log(UserSession.user, UserSession.module, UserSession.ipAddress, operation, "idRole = " + idRole + ", idModule = " + idModule);
    }

    public int accessLevel(Integer idModule, Integer idUser) throws Exception {
        if (userAccess.get(idUser) == null || userAccess.get(idUser).get(idModule) == null) {
            throw new RestrictedAccessException("El usuario no tiene permisos para el módulo.");
        }
        return userAccess.get(idUser).get(idModule);
    }

    private void addUserAccess(Integer idUser, Integer idModule, Integer accessLevel) {
        if (userAccess.get(idUser) == null) {
            userAccess.put(idUser, new HashMap<Integer, Integer>());
        }
        userAccess.get(idUser).put(idModule, accessLevel);
    }

    private void removeUserAccess(Integer idUser, Integer idModule) {
        userAccess.get(idUser).remove(idModule);
    }

    private void addRoleUserAccess(Integer idUser, Integer idRole, Integer accessLevel) {
        if (roleUserAccess.get(idUser) == null) {
            roleUserAccess.put(idUser, new HashMap<Integer, Integer>());
        }
        roleUserAccess.get(idUser).put(idRole, accessLevel);
        for (Integer idModule : roleModuleAccess.get(idRole)) {
            addUserAccess(idUser, idModule, accessLevel);
        }
    }

    private void removeRoleUserAccess(Integer idUser, Integer idRole) {
        userAccess.get(idUser).remove(idRole);
        for (Integer idModule : roleModuleAccess.get(idRole)) {
            removeUserAccess(idUser, idModule);
        }
    }

    private void addRoleModuleAccess(Integer idRole, Integer idModule) {
        if (roleModuleAccess.get(idRole) == null) {
            roleModuleAccess.put(idRole, new HashSet<Integer>());
        }
        roleModuleAccess.get(idRole).add(idModule);
        for (Integer idUser : roleUserAccess.keySet()) {
            for (Integer idRole_ : roleUserAccess.get(idUser).keySet()) {
                if (idRole_.equals(idRole)) {
                    addUserAccess(idUser, idModule, roleUserAccess.get(idUser).get(idRole));
                }
            }
        }
    }

    private void removeRoleModuleAccess(Integer idRole, Integer idModule) {
        roleModuleAccess.get(idRole).remove(idModule);
        for (Integer idUser : roleUserAccess.keySet()) {
            for (Integer idRole_ : roleUserAccess.get(idUser).keySet()) {
                if (idRole_.equals(idRole)) {
                    removeUserAccess(idUser, idModule);
                }
            }
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
