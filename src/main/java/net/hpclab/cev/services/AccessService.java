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

import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.entities.ModulesUsers;
import net.hpclab.cev.entities.Roles;
import net.hpclab.cev.entities.RolesModules;
import net.hpclab.cev.entities.RolesUsers;
import net.hpclab.cev.entities.Users;
import net.hpclab.cev.enums.AuditEnum;
import net.hpclab.cev.enums.ModulesEnum;
import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.exceptions.RecordAlreadyExistsException;
import net.hpclab.cev.exceptions.RecordNotExistsException;
import net.hpclab.cev.exceptions.UnauthorizedAccessException;

public class AccessService implements Serializable {

	private static final long serialVersionUID = 497068865656993475L;
	private static final Logger LOGGER = Logger.getLogger(AccessService.class.getSimpleName());
	private static AccessService accessService;
	private DataBaseService<ModulesUsers> mousService;
	private DataBaseService<RolesUsers> rousService;
	private DataBaseService<RolesModules> romoService;
	private HashMap<Users, HashMap<Modules, Integer>> directUserAccess;
	private HashMap<Users, HashMap<Modules, Integer>> userAccess;
	private HashMap<Users, HashSet<Roles>> roleUserAccess;
	private HashMap<Roles, HashMap<Modules, Integer>> roleModuleAccess;
	private List<ModulesUsers> modulesUsers;
	private List<RolesUsers> rolesUsers;
	private List<RolesModules> rolesModules;

	private AccessService() throws Exception {
		try {
			mousService = new DataBaseService<>(ModulesUsers.class, -1);
			rousService = new DataBaseService<>(RolesUsers.class, -1);
			romoService = new DataBaseService<>(RolesModules.class, -1);
			directUserAccess = new HashMap<>();
			userAccess = new HashMap<>();
			roleUserAccess = new HashMap<>();
			roleModuleAccess = new HashMap<>();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "El servicio de acceso no ha podido iniciar correctamente: {0}.", e.getMessage());
			throw new Exception("El servicio de acceso no ha podido iniciar correctamente: " + e.getMessage());
		}
	}

	public void loadAccesses() throws Exception {
		userAccess.clear();
		roleUserAccess.clear();
		roleModuleAccess.clear();
		directUserAccess.clear();
		modulesUsers = DataWarehouse.getInstance().allModulesUsers;
		rolesUsers = DataWarehouse.getInstance().allRolesUsers;
		rolesModules = DataWarehouse.getInstance().allRolesModules;

		for (RolesModules r : rolesModules)
			addRoleModuleAccess(r.getIdRole(), r.getIdModule(), r.getAccessLevel());

		for (RolesUsers r : rolesUsers)
			addRoleUserAccess(r.getIdUser(), r.getIdRole());

		for (ModulesUsers r : modulesUsers)
			addUserAccess(r.getIdUser(), r.getIdModule(), r.getAccessLevel(), true);
	}

	public void setModuleUserAccess(Modules idModule, Users idUser, Integer accessLevel, AuditEnum operation,
			StatusEnum status) throws Exception {
		ModulesUsers moduleUser = new ModulesUsers();
		moduleUser.setIdModule(idModule);
		moduleUser.setIdUser(idUser);
		switch (operation) {
		case INSERT:
			if (userAccess.get(idUser).get(idModule) != null)
				throw new RecordAlreadyExistsException("Ya existe esta relación");

			moduleUser.setAccessLevel(accessLevel);
			moduleUser.setStatus(status.get());
			moduleUser = mousService.persist(moduleUser);
			DataWarehouse.getInstance().allModulesUsers.add(moduleUser);
			addUserAccess(idUser, idModule, accessLevel, true);
			break;
		case DELETE:
			if (userAccess.get(idUser).get(idModule) == null)
				throw new RecordNotExistsException("No existe esta relación");

			moduleUser = mousService.getSingleRecord(moduleUser);
			mousService.delete(moduleUser);
			DataWarehouse.getInstance().allModulesUsers.remove(moduleUser);
			removeUserAccess(idUser, idModule);
			break;
		case UPDATE:
			if (userAccess.get(idUser).get(idModule) == null)
				throw new RecordNotExistsException("No existe esta relación");

			moduleUser = mousService.getSingleRecord(moduleUser);
			moduleUser.setAccessLevel(accessLevel);
			moduleUser.setStatus(status.get());
			DataWarehouse.getInstance().allModulesUsers.remove(moduleUser);
			moduleUser = mousService.merge(moduleUser);
			DataWarehouse.getInstance().allModulesUsers.add(moduleUser);
			removeUserAccess(idUser, idModule);
			addUserAccess(idUser, idModule, accessLevel, true);
			break;
		case STATUS_CHANGE:
			if (userAccess.get(idUser).get(idModule) == null)
				throw new RecordNotExistsException("No existe esta relación");

			moduleUser = mousService.getSingleRecord(moduleUser);
			moduleUser.setStatus(status.get());
			DataWarehouse.getInstance().allModulesUsers.remove(moduleUser);
			moduleUser = mousService.merge(moduleUser);
			DataWarehouse.getInstance().allModulesUsers.add(moduleUser);
			if (userAccess.get(idUser) == null || userAccess.get(idUser).get(idModule) == null)
				addUserAccess(idUser, idModule, accessLevel, true);
			else
				removeUserAccess(idUser, idModule);

			break;
		default:
			throw new IllegalArgumentException("Operación desconocida");
		}
	}

	public void setRoleUserAccess(Roles idRole, Users idUser, AuditEnum operation, StatusEnum status) throws Exception {
		RolesUsers roleUser = new RolesUsers();
		roleUser.setIdRole(idRole);
		roleUser.setIdUser(idUser);
		switch (operation) {
		case INSERT:
			if (roleUserAccess.get(idUser).contains(idRole))
				throw new RecordAlreadyExistsException("Ya existe esta relación");

			roleUser.setStatus(status.get());
			roleUser = rousService.persist(roleUser);
			DataWarehouse.getInstance().allRolesUsers.add(roleUser);
			addRoleUserAccess(idUser, idRole);
			break;
		case DELETE:
			if (!roleUserAccess.get(idUser).contains(idRole))
				throw new RecordNotExistsException("No existe esta relación");

			roleUser = rousService.getSingleRecord(roleUser);
			rousService.delete(roleUser);
			DataWarehouse.getInstance().allRolesUsers.remove(roleUser);
			removeRoleUserAccess(idUser, idRole);
			break;
		case UPDATE:
			throw new UnsupportedOperationException("No existe esta operación");
		case STATUS_CHANGE:
			if (!roleUserAccess.get(idUser).contains(idRole))
				throw new RecordNotExistsException("No existe esta relación");

			roleUser = rousService.getSingleRecord(roleUser);
			roleUser.setStatus(status.get());
			rousService.merge(roleUser);

			if (roleUserAccess.get(idUser) == null || !roleUserAccess.get(idUser).contains(idRole))
				addRoleUserAccess(idUser, idRole);
			else
				removeRoleUserAccess(idUser, idRole);

			break;
		default:
			throw new IllegalArgumentException("Operación desconocida");
		}
	}

	public void setRoleModule(Roles idRole, Modules idModule, Integer accessLevel, AuditEnum operation,
			StatusEnum status) throws Exception {
		RolesModules roleModule = new RolesModules();
		roleModule.setIdRole(idRole);
		roleModule.setIdModule(idModule);
		switch (operation) {
		case INSERT:
			if (roleModuleAccess.get(idRole).containsKey(idModule))
				throw new RecordAlreadyExistsException("Ya existe esta relación");

			roleModule.setAccessLevel(accessLevel);
			roleModule = romoService.persist(roleModule);
			DataWarehouse.getInstance().allRolesModules.add(roleModule);
			addRoleModuleAccess(idRole, idModule, accessLevel);
			break;
		case DELETE:
			if (!roleModuleAccess.get(idRole).containsKey(idModule))
				throw new RecordNotExistsException("No existe esta relación");

			roleModule = romoService.getSingleRecord(roleModule);
			romoService.delete(roleModule);
			DataWarehouse.getInstance().allRolesModules.remove(roleModule);
			removeRoleModuleAccess(idRole, idModule);
			break;
		case UPDATE:
			if (!roleModuleAccess.get(idRole).containsKey(idModule))
				throw new RecordAlreadyExistsException("Ya existe esta relación");

			roleModule = romoService.getSingleRecord(roleModule);
			roleModule.setAccessLevel(accessLevel);
			DataWarehouse.getInstance().allRolesModules.remove(roleModule);
			roleModule = romoService.merge(roleModule);
			DataWarehouse.getInstance().allRolesModules.add(roleModule);
			removeRoleModuleAccess(idRole, idModule);
			addRoleModuleAccess(idRole, idModule, accessLevel);
		case STATUS_CHANGE:
			if (!roleModuleAccess.get(idRole).containsKey(idModule))
				throw new RecordNotExistsException("No existe esta relación");

			roleModule = romoService.getSingleRecord(roleModule);
			roleModule.setStatus(status.get());
			DataWarehouse.getInstance().allRolesModules.remove(roleModule);
			roleModule = romoService.merge(roleModule);
			DataWarehouse.getInstance().allRolesModules.add(roleModule);

			if (roleModuleAccess.get(idRole) == null || !roleModuleAccess.get(idRole).containsKey(idModule))
				addRoleModuleAccess(idRole, idModule, accessLevel);
			else
				removeRoleModuleAccess(idRole, idModule);

			break;
		default:
			throw new IllegalArgumentException("Operación desconocida");
		}
	}

	private int accessLevel(Modules idModule, Users idUser) throws Exception {
		if (!userAccess.containsKey(idUser) || !userAccess.get(idUser).containsKey(idModule))
			throw new UnauthorizedAccessException("El usuario no tiene permisos para el módulo.");
		return userAccess.get(idUser).get(idModule);
	}

	public boolean checkUserAccess(ModulesEnum idModule, Users idUser, Constant.AccessLevel accessLevel)
			throws Exception {
		for (Modules m : DataWarehouse.getInstance().allModules)
			if (idModule.name().equals(m.getModuleCode()))
				return existsAccess(accessLevel(m, idUser), accessLevel.getLevel());
		return false;
	}

	private void addUserAccess(Users idUser, Modules idModule, Integer accessLevel, boolean direct) {
		if ((!direct && !userAccess.containsKey(idUser)) || (direct && !directUserAccess.containsKey(idUser)))
			if (!direct)
				userAccess.put(idUser, new HashMap<Modules, Integer>());
			else
				directUserAccess.put(idUser, new HashMap<Modules, Integer>());

		if (!direct)
			userAccess.get(idUser).put(idModule, accessLevel);
		else
			directUserAccess.get(idUser).put(idModule, accessLevel);
	}

	private void removeUserAccess(Users idUser, Modules idModule) {
		userAccess.get(idUser).remove(idModule);
	}

	private void addRoleUserAccess(Users idUser, Roles idRole) {
		if (!roleUserAccess.containsKey(idUser))
			roleUserAccess.put(idUser, new HashSet<Roles>());

		roleUserAccess.get(idUser).add(idRole);

		for (Modules idModule : roleModuleAccess.get(idRole).keySet())
			addUserAccess(idUser, idModule, roleModuleAccess.get(idRole).get(idModule), false);
	}

	private void removeRoleUserAccess(Users idUser, Roles idRole) {
		roleUserAccess.get(idUser).remove(idRole);

		for (Modules idModule : roleModuleAccess.get(idRole).keySet())
			removeUserAccess(idUser, idModule);
	}

	private void addRoleModuleAccess(Roles idRole, Modules idModule, Integer accessLevel) {
		if (!roleModuleAccess.containsKey(idRole))
			roleModuleAccess.put(idRole, new HashMap<Modules, Integer>());

		roleModuleAccess.get(idRole).put(idModule, accessLevel);
		for (Users idUser : roleUserAccess.keySet())
			for (Roles idRole_ : roleUserAccess.get(idUser))
				if (idRole_.equals(idRole))
					addUserAccess(idUser, idModule, accessLevel, false);
	}

	private void removeRoleModuleAccess(Roles idRole, Modules idModule) {
		roleModuleAccess.get(idRole).remove(idModule);
		for (Users idUser : roleUserAccess.keySet())
			for (Roles idRole_ : roleUserAccess.get(idUser))
				if (idRole_.equals(idRole))
					removeUserAccess(idUser, idModule);
	}

	public List<Modules> getUserMenu(Users idUser) {
		Set<Modules> setModules = getUserModules(idUser).keySet();
		PriorityQueue<Modules> userModules = new PriorityQueue<>(setModules.size(), new Comparator<Modules>() {
			@Override
			public int compare(Modules o1, Modules o2) {
				return new Integer(o1.getModuleOrder()).compareTo(o2.getModuleOrder());
			}
		});

		for (Modules idModule : setModules)
			userModules.add(idModule);

		ArrayList<Modules> orderedUserModules = new ArrayList<>();
		while (!userModules.isEmpty())
			orderedUserModules.add(userModules.poll());

		return orderedUserModules;
	}

	public int getAccessCode(boolean[] accesses) {
		String accessesString = "";
		for (int i = 0; i < 4; i++)
			accessesString += accesses[i] ? "1" : "0";
		return getAccessCode(accessesString);
	}

	public int getAccessCode(String accesses) {
		return Integer.parseInt(accesses, 2);
	}

	public boolean[] getAccessFromCode(int code) {
		boolean[] access = new boolean[4];
		String binaryCode = String.format("%1$4s", String.valueOf(Integer.toString(code, 2))).replace(' ', '0');
		if (binaryCode.length() == 4)
			for (int i = 0; i < 4; i++)
				access[i] = binaryCode.charAt(i) == '1';
		return access;
	}

	public boolean existsAccess(int existingAccess, int desiredAccess) {
		boolean[] access = getAccessFromCode(existingAccess);
		boolean[] desiredAccesses = getAccessFromCode(desiredAccess);
		for (int i = 0; i < 4; i++)
			if (access[i] && desiredAccesses[i])
				return true;
		return false;
	}

	public HashMap<Modules, Integer> getUserModules(Users idUsers) {
		return userAccess.get(idUsers);
	}

	public HashMap<Modules, Integer> getDirectUserModules(Users idUsers) {
		return directUserAccess.get(idUsers);
	}

	public HashMap<Modules, Integer> getRoleModules(Roles idRoles) {
		return roleModuleAccess.get(idRoles);
	}

	public HashSet<Roles> getUserRoles(Users idUsers) {
		return roleUserAccess.get(idUsers);
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
		if (accessService == null)
			(accessService = new AccessService()).loadAccesses();

		return accessService;
	}
}
