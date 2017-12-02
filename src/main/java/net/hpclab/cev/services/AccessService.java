package net.hpclab.cev.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.hpclab.cev.enums.AuditEnum;
import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.entities.ModulesUsers;
import net.hpclab.cev.entities.Roles;
import net.hpclab.cev.entities.RolesModules;
import net.hpclab.cev.entities.RolesUsers;
import net.hpclab.cev.enums.StatusEnum;
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
	private HashMap<Users, HashMap<Modules, Integer>> userAccess;
	private HashMap<Users, HashMap<Roles, Integer>> roleUserAccess;
	private HashMap<Roles, HashSet<Modules>> roleModuleAccess;
	private List<ModulesUsers> modulesUsers;
	private List<RolesUsers> rolesUsers;
	private List<RolesModules> rolesModules;

	private AccessService() throws Exception {
		try {
			mousService = new DataBaseService<>(ModulesUsers.class, -1);
			rousService = new DataBaseService<>(RolesUsers.class, -1);
			romoService = new DataBaseService<>(RolesModules.class, -1);
			userAccess = new HashMap<>();
			roleUserAccess = new HashMap<>();
			roleModuleAccess = new HashMap<>();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "El servicio de acceso no ha podido iniciar correctamente: {0}.", e.getMessage());
			throw new Exception("El servicio de acceso no ha podido iniciar correctamente: " + e.getMessage());
		}
	}

	public void loadAccesses() throws Exception {
		userAccess.clear();
		roleUserAccess.clear();
		roleModuleAccess.clear();
		modulesUsers = DataWarehouse.getInstance().allModulesUsers;
		rolesUsers = DataWarehouse.getInstance().allRolesUsers;
		rolesModules = DataWarehouse.getInstance().allRolesModules;

		for (RolesModules r : rolesModules) {
			addRoleModuleAccess(r.getIdRole(), r.getIdModule());
		}

		for (RolesUsers r : rolesUsers) {
			addRoleUserAccess(r.getIdUser(), r.getIdRole(), r.getAccessLevel());
			for (Modules idModule : roleModuleAccess.get(r.getIdRole())) {
				addUserAccess(r.getIdUser(), idModule, r.getAccessLevel());
			}
		}

		for (ModulesUsers r : modulesUsers) {
			addUserAccess(r.getIdUser(), r.getIdModule(), r.getAccessLevel());
		}
	}

	public void setModuleUserAccess(Modules idModule, Users idUser, Integer accessLevel, AuditEnum operation,
			StatusEnum status) throws Exception {
		ModulesUsers moduleUser;
		HashMap<String, Object> params;
		switch (operation) {
		case INSERT:
			if (userAccess.get(idUser).get(idModule) != null) {
				throw new RecordAlreadyExistsException("Ya existe esta relación");
			}
			moduleUser = new ModulesUsers();
			moduleUser.setIdModule(idModule);
			moduleUser.setIdUser(idUser);
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
	}

	public void setRoleUserAccess(Roles idRole, Users idUser, Integer accessLevel, AuditEnum operation,
			StatusEnum status) throws Exception {
		RolesUsers roleUser;
		HashMap<String, Object> params;
		switch (operation) {
		case INSERT:
			if (roleUserAccess.get(idUser).get(idRole) != null) {
				throw new RecordAlreadyExistsException("Ya existe esta relación");
			}
			roleUser = new RolesUsers();
			roleUser.setIdRole(idRole);
			roleUser.setIdUser(idUser);
			roleUser.setAccessLevel(accessLevel);
			rousService.persist(roleUser);
			addRoleUserAccess(idUser, idRole, accessLevel);
			break;
		case DELETE:
			if (roleUserAccess.get(idUser).get(idRole) == null) {
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
			if (roleUserAccess.get(idUser).get(idRole) == null) {
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
			if (roleUserAccess.get(idUser).get(idRole) == null) {
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
	}

	public void setRoleModule(Roles idRole, Modules idModule, AuditEnum operation, StatusEnum status) throws Exception {
		RolesModules roleModule;
		HashMap<String, Object> params;
		switch (operation) {
		case INSERT:
			if (roleModuleAccess.get(idRole).contains(idModule)) {
				throw new RecordAlreadyExistsException("Ya existe esta relación");
			}
			roleModule = new RolesModules();
			roleModule.setIdRole(idRole);
			roleModule.setIdModule(idModule);
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
	}

	public int accessLevel(Modules idModule, Users idUser) throws Exception {
		if (userAccess.get(idUser) == null || userAccess.get(idUser).get(idModule) == null) {
			throw new RestrictedAccessException("El usuario no tiene permisos para el módulo.");
		}
		return userAccess.get(idUser).get(idModule);
	}

	private void addUserAccess(Users idUser, Modules idModule, Integer accessLevel) {
		if (userAccess.get(idUser) == null) {
			userAccess.put(idUser, new HashMap<Modules, Integer>());
		}
		userAccess.get(idUser).put(idModule, accessLevel);
	}

	private void removeUserAccess(Users idUser, Modules idModule) {
		userAccess.get(idUser).remove(idModule);
	}

	private void addRoleUserAccess(Users idUser, Roles idRole, Integer accessLevel) {
		if (roleUserAccess.get(idUser) == null) {
			roleUserAccess.put(idUser, new HashMap<Roles, Integer>());
		}
		roleUserAccess.get(idUser).put(idRole, accessLevel);
		for (Modules idModule : roleModuleAccess.get(idRole)) {
			addUserAccess(idUser, idModule, accessLevel);
		}
	}

	private void removeRoleUserAccess(Users idUser, Roles idRole) {
		roleUserAccess.get(idUser).remove(idRole);
		for (Modules idModule : roleModuleAccess.get(idRole)) {
			removeUserAccess(idUser, idModule);
		}
	}

	private void addRoleModuleAccess(Roles idRole, Modules idModule) {
		if (roleModuleAccess.get(idRole) == null) {
			roleModuleAccess.put(idRole, new HashSet<Modules>());
		}
		roleModuleAccess.get(idRole).add(idModule);
		for (Users idUser : roleUserAccess.keySet()) {
			for (Roles idRole_ : roleUserAccess.get(idUser).keySet()) {
				if (idRole_.equals(idRole)) {
					addUserAccess(idUser, idModule, roleUserAccess.get(idUser).get(idRole));
				}
			}
		}
	}

	private void removeRoleModuleAccess(Roles idRole, Modules idModule) {
		roleModuleAccess.get(idRole).remove(idModule);
		for (Users idUser : roleUserAccess.keySet()) {
			for (Roles idRole_ : roleUserAccess.get(idUser).keySet()) {
				if (idRole_.equals(idRole)) {
					removeUserAccess(idUser, idModule);
				}
			}
		}
	}

	public List<Modules> getUserMenu(Users idUser) {
		Set<Modules> setModules = getUserModules(idUser).keySet();
		PriorityQueue<Modules> userModules = new PriorityQueue<>(setModules.size(), new Comparator<Modules>() {
			@Override
			public int compare(Modules o1, Modules o2) {
				return new Integer(o1.getModuleOrder()).compareTo(o2.getModuleOrder());
			}
		});
		for (Modules idModule : setModules) {
			userModules.add(idModule);
		}
		ArrayList<Modules> orderedUserModules = new ArrayList<>();
		while (!userModules.isEmpty()) {
			orderedUserModules.add(userModules.poll());
		}
		return orderedUserModules;
	}

	public HashMap<Modules, Integer> getUserModules(Users idUsers) {
		return userAccess.get(idUsers);
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

	public static synchronized AccessService getInstance() throws Exception {
		if (accessService == null) {
			accessService = new AccessService();
			accessService.loadAccesses();
		}
		return accessService;
	}
}
