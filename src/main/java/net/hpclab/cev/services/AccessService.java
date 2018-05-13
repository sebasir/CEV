/*
 * Colección Entomológica Virtual
 * Universidad Central
 * High Performance Computing Laboratory
 * Grupo COMMONS.
 * 
 * Sebastián Motavita Medellín
 * 
 * 2017 - 2018
 */

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

/**
 * Es un servicio creado para la encapsulación de la creación, edición,
 * eliminación y consulta de Roles, Módulos, Usuarios, y las interacciones entre
 * sí. El servicio ofrece una manera de acceso único a través de un objeto
 * <tt>Singleton</tt> creado en memoria estática, y sincronizada, permitiendo
 * acceder la misma instancia desde varios hilos simultaneamente.
 * 
 * <p>
 * La organización está dada por mapas que permiten obtener la configuración de
 * roles y accesos para un usuario a través de una llave natural
 * <tt>Integer</tt> y soportada por la clase <tt>HashMap</tt>, los cuales
 * permiten mapear el contenido de las entidades <tt>Users</tt>,
 * <tt>Modules</tt>, <tt>Roles</tt>, y las derivadas de su integridad de datos
 * <tt>ModulesUsers</tt>, <tt>RolesUsers</tt>, <tt>RolesModules</tt>, todos
 * obtenidos a través del servicio de base de datos <tt>DataBaseService</tt>.
 * 
 * <p>
 * Todos los orígenes de datos, se obtienen del servicio <tt>DataWareHouse</tt>,
 * el cual provee una manera centralizada de acceder a todas las listas de
 * dominio.
 * 
 * <p>
 * El nivel de acceso está definido por la siguiente tabla:<br>
 * <tt>DUCR </tt><br>
 * <tt>0000 -> 00 NONE </tt><br>
 * <tt>0001 -> 01 ONLY READ </tt><br>
 * <tt>0010 -> 02 ONLY CREATE </tt><br>
 * <tt>0011 -> 03 READ, CREATE </tt><br>
 * <tt>0100 -> 04 ONLY UPDATE </tt><br>
 * <tt>0101 -> 05 READ, UPDATE </tt><br>
 * <tt>0110 -> 06 CREATE, UPDATE </tt><br>
 * <tt>0111 -> 07 READ, CREATE, UPDATE </tt><br>
 * <tt>1000 -> 08 ONLY DELETE </tt><br>
 * <tt>1001 -> 09 READ, DELETE </tt><br>
 * <tt>1010 -> 10 CREATE, DELETE </tt><br>
 * <tt>1011 -> 11 READ, CREATE, DELETE </tt><br>
 * <tt>1100 -> 12 UPDATE, DELETE </tt><br>
 * <tt>1101 -> 13 READ, UPDATE, DELETE </tt><br>
 * <tt>1110 -> 14 CREATE, UPDATE, DELETE </tt><br>
 * <tt>1111 -> 15 READ, CREATE, UPDATE, DELETE<br></tt> Donde D: Delete, U:
 * Update, C: Create, R: Read, 0 es acceso denegado, y 1 concedido. El número
 * decimal que se visualiza es la representación decimal de la conjunción de los
 * números de la izquierda.
 * 
 * @since 1.0
 * @author Sebasir
 * @see ModulesUsers
 * @see RolesUsers
 * @see RolesModules
 * @see Users
 * @see Modules
 * @see Roles
 * @see DataBaseService
 * @see DataWarehouse
 * @see Integer
 * @see HashMap
 */

public class AccessService implements Serializable {

	private static final long serialVersionUID = 497068865656993475L;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(AccessService.class.getSimpleName());

	/**
	 * Objeto estático de esta clase el cual permite acceder siempre a la misma
	 * referencia.
	 */
	private static AccessService accessService;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>ModulesUsers</tt>, lo cual permite extender todas las operaciones del
	 * servicio para esta clase.
	 */
	private DataBaseService<ModulesUsers> mousService;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>RolesUsers</tt>, lo cual permite extender todas las operaciones del
	 * servicio para esta clase.
	 */
	private DataBaseService<RolesUsers> rousService;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>RolesModules</tt>, lo cual permite extender todas las operaciones del
	 * servicio para esta clase.
	 */
	private DataBaseService<RolesModules> romoService;

	/**
	 * Objeto que permite mapear los accesos a módulos de un usuario, así como el
	 * nivel de acceso para estos módulos, siguiendo este esquema:
	 * 
	 * <p>
	 * <tt>Usuario 1 -> Módulo 1: Nivel 1, Módulo 2: Nivel 2, Modulo n: Nivel entre 1 y 15</tt>
	 * <p>
	 * <tt>Usuario 2 -> Módulo 1: Nivel 1, Módulo 2: Nivel 2, Modulo n: Nivel entre 1 y 15</tt>
	 * 
	 * <p>
	 * Este mapa en particular, es usado para obtener los módulos y accesos
	 * específicos de un usuario si un rol asignado al acceso.
	 */
	private HashMap<Users, HashMap<Modules, Integer>> directUserAccess;

	/**
	 * Objeto que permite mapear los accesos a módulos de un usuario, así como el
	 * nivel de acceso para estos módulos, siguiendo este esquema:
	 * 
	 * <p>
	 * <tt>Usuario 1 -> Módulo 1: Nivel 1, Módulo 2: Nivel 2, Modulo n: Nivel entre 1 y 15</tt>
	 * <p>
	 * <tt>Usuario 2 -> Módulo 1: Nivel 1, Módulo 2: Nivel 2, Modulo n: Nivel entre 1 y 15</tt>
	 * 
	 * <p>
	 * Este mapa es usado para obtener los módulos y accesos específicos de un
	 * usuario atados o no atados a un rol.
	 */
	private HashMap<Users, HashMap<Modules, Integer>> userAccess;

	/**
	 * Objeto que permite mapear los roles de un usuario siguiendo este esquema:
	 * 
	 * <p>
	 * <tt>Usuario 1 -> Rol 1, Rol 2, Rol n</tt>
	 * <p>
	 * <tt>Usuario 2 -> Rol 1, Rol 2, Rol n</tt>
	 * 
	 * <p>
	 * Este mapa es usado para obtener los roles de un usuario.
	 */
	private HashMap<Users, HashSet<Roles>> roleUserAccess;

	/**
	 * Objeto que permite mapear los accesos de un rol específico siguiendo este
	 * esquema:
	 * 
	 * <p>
	 * <tt>Rol 1 -> Módulo 1: Nivel 1, Módulo 2: Nivel 2, Modulo n: Nivel entre 1 y 15</tt>
	 * <p>
	 * <tt>Rol 2 -> Módulo 1: Nivel 1, Módulo 2: Nivel 2, Modulo n: Nivel entre 1 y 15</tt>
	 * 
	 * <p>
	 * Este mapa es usado para obtener los roles de un usuario.
	 */
	private HashMap<Roles, HashMap<Modules, Integer>> roleModuleAccess;

	/**
	 * Objeto que permite obtener una referencia a las interelaciones de un usuario
	 * y un módulo, que luego se deriva en el mapa <tt>directUserAccess</tt>
	 */
	private List<ModulesUsers> modulesUsers;

	/**
	 * Objeto que permite obtener una referencia a las interelaciones de un usuario
	 * y un rol, que luego se deriva en el mapa <tt>roleUserAccess</tt>
	 */
	private List<RolesUsers> rolesUsers;

	/**
	 * Objeto que permite obtener una referencia a las interelaciones de un rol y un
	 * módulo, que luego se deriva en el mapa <tt>roleModuleAccess</tt>
	 */
	private List<RolesModules> rolesModules;

	/**
	 * Construye un servicio inicializando los servicios derivados de
	 * <tt>DataBaseService</tt>, para las etidades <tt>ModulesUsers</tt>,
	 * <tt>RolesUsers</tt>, <tt>RolesModules</tt>, además de inicializar los mapas.
	 * 
	 * @throws Exception
	 *             Cuando los servicios de <tt>DataBaseService</tt> presentan un
	 *             error inicializando, que se presenta en un mensaje de LOG
	 */
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

	/**
	 * Consume los servicios de <tt>DataWarehouse</tt> extrayendo las listas de
	 * entidades de <tt>ModulesUsers</tt>, <tt>RolesUsers</tt>,
	 * <tt>RolesModules</tt>, y genera la interrelación para cada uno de los mapas
	 * asociados
	 * 
	 * @throws Exception
	 *             Cuando hay una violación de alguna integridad en el momento de
	 *             construir los mapas
	 */
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

	/**
	 * Permite realizar operaciones de inserción, eliminación, actualización y
	 * cambio de estado de alguna relación de un usuario y los módulos, indicando el
	 * nivel de acceso con el número que se indicado anteriormente, y la operación a
	 * realizar según se define en las enumeraciones <tt>AuditEnum</tt> y
	 * <tt>StatusEnum</tt>.
	 * 
	 * @param idModule
	 *            Objeto que representa el módulo, con la llave única.
	 * @param idUser
	 *            Objeto que representa el usuario, con la llave única.
	 * @param accessLevel
	 *            Número entre 1 y 15 representando los niveles de acceso
	 * @param operation
	 *            Operación para la combinación de usuario y modulo, definidas en el
	 *            <tt>AuditEnum</tt>
	 * @param status
	 *            Estado nuevo que se aplica al registro, según la enumeración
	 *            <tt>StatusEnum</tt>
	 * @throws Exception
	 *             Si realizando alguna operación sobre la base de datos genera
	 *             alguna excepción, o existe una operación no válida.
	 * @throws RecordNotExistsException
	 *             En caso que los registros no se encuentren cuando se realice las
	 *             operaciones de eliminación y actualización.
	 * @throws RecordAlreadyExistsException
	 *             Cuando inserta algo que ya existe.
	 */
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

	/**
	 * Permite realizar operaciones de inserción, eliminación, actualización y
	 * cambio de estado de alguna relación de un usuario y los roles, indicando la
	 * operación a realizar según se define en las enumeraciones <tt>AuditEnum</tt>
	 * y <tt>StatusEnum</tt>.
	 * 
	 * @param idRole
	 *            Objeto que representa el rol, con la llave única.
	 * @param idUser
	 *            Objeto que representa el usuario, con la llave única.
	 * @param operation
	 *            Operación para la combinación de usuario y modulo, definidas en el
	 *            <tt>AuditEnum</tt>
	 * @param status
	 *            Estado nuevo que se aplica al registro, según la enumeración
	 *            <tt>StatusEnum</tt>
	 * @throws Exception
	 *             Si realizando alguna operación sobre la base de datos genera
	 *             alguna excepción, o existe una operación no válida.
	 * @throws RecordNotExistsException
	 *             En caso que los registros no se encuentren cuando se realice las
	 *             operaciones de eliminación y actualización.
	 * @throws RecordAlreadyExistsException
	 *             Cuando inserta algo que ya existe.
	 */
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

	/**
	 * Permite realizar operaciones de inserción, eliminación, actualización y
	 * cambio de estado de alguna relación de un módulos y los roles, indicando el
	 * nivel de acceso con el número que se indicado anteriormente, y las
	 * operaciones a realizar según se define en las enumeraciones
	 * <tt>AuditEnum</tt> y <tt>StatusEnum</tt>.
	 * 
	 * @param idRole
	 *            Objeto que representa el rol, con la llave única.
	 * @param idModule
	 *            Objeto que representa el módulo, con la llave única.
	 * @param accessLevel
	 *            Número entre 1 y 15 representando los niveles de acceso
	 * @param operation
	 *            Operación para la combinación de usuario y modulo, definidas en el
	 *            <tt>AuditEnum</tt>
	 * @param status
	 *            Estado nuevo que se aplica al registro, según la enumeración
	 *            <tt>StatusEnum</tt>
	 * @throws Exception
	 *             Si realizando alguna operación sobre la base de datos genera
	 *             alguna excepción, o existe una operación no válida.
	 * @throws RecordNotExistsException
	 *             En caso que los registros no se encuentren cuando se realice las
	 *             operaciones de eliminación y actualización.
	 * @throws RecordAlreadyExistsException
	 *             Cuando inserta algo que ya existe.
	 */
	public void setRoleModule(Roles idRole, Modules idModule, Integer accessLevel, AuditEnum operation,
			StatusEnum status) throws Exception {
		RolesModules roleModule = new RolesModules();
		roleModule.setIdRole(idRole);
		roleModule.setIdModule(idModule);
		switch (operation) {
		case INSERT:
			if (roleModuleAccess.containsKey(idRole) && roleModuleAccess.get(idRole).containsKey(idModule))
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

	/**
	 * Funcion que utiliza el mapa de accesos de usuario, para obtener una
	 * existencia del nivel de acceso de un modulo para un usuario determiado.
	 * 
	 * @param idModule
	 *            Objeto que representa el módulo, con la llave única.
	 * @param idUser
	 *            Objeto que representa el usuario, con la llave única.
	 * @return Nivel decimal (número entre 1 y 15) de acceso si se encontró en el
	 *         mapa.
	 * @throws Exception
	 *             Cuando se viola una posición del mapa, en su función <tt>get</tt>
	 * @throws UnauthorizedAccessException
	 *             Cuando el usuario no existe en el mapa o bien no tiene el módulo
	 */
	private int accessLevel(Modules idModule, Users idUser) throws Exception {
		if (!userAccess.containsKey(idUser) || !userAccess.get(idUser).containsKey(idModule))
			throw new UnauthorizedAccessException("El usuario no tiene permisos para el módulo.");
		return userAccess.get(idUser).get(idModule);
	}

	/**
	 * Función que permite verficar que un usuario cuenta con el nivel de acceso
	 * sobre un módulo que se verifica sobre la enumeración <tt>ModulesEnum</tt>, y
	 * esta a su vez, se compara contra un módulo en su parte <tt>ModuleCode</tt>,
	 * para un usuario en particular
	 * 
	 * @param idModule
	 *            Módulo del sistema según la enumeración <tt>ModulesEnum</tt>
	 * @param idUser
	 *            Objeto que representa el usuario, con la llave única.
	 * @param accessLevel
	 *            Nivel de acceso deseado sobre un módulo, según la enumeración
	 *            <tt>Constant.AccessLevel</tt>
	 * @return <tt>true</tt> Si la función <tt>existsAccess</tt> verifica la
	 *         existencia <br>
	 *         <tt>false</tt> Si la función <tt>existsAccess</tt> no verifica la
	 *         existencia, o bien el módulo no existe como interceptable
	 * @throws Exception
	 *             Si no es posible acceder a la lista de dominios de los módulos, o
	 *             la función <tt>existsAccess</tt> encuentra un error
	 */
	public boolean checkUserAccess(ModulesEnum idModule, Users idUser, Constant.AccessLevel accessLevel)
			throws Exception {
		for (Modules m : DataWarehouse.getInstance().allModules)
			if (idModule.name().equals(m.getModuleCode()))
				return existsAccess(accessLevel(m, idUser), accessLevel.getLevel());
		return false;
	}

	/**
	 * Función que permite cargar los mapas de accesos de usuario que se usan para
	 * validar la existencia de un acceso a un módulo, incluido su nivel
	 * 
	 * @param idUser
	 *            Objeto que representa el usuario, con la llave única.
	 * @param idModule
	 *            Objeto que representa el módulo, con la llave única.
	 * @param accessLevel
	 *            Número entre 1 y 15 representando los niveles de acceso
	 * @param direct
	 *            Indica si el nivel a añadir al mapa consiste como parte de un
	 *            modulo, o desde un rol
	 */
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

	/**
	 * Método que elimina un módulo de los accesos de un usuario
	 * 
	 * @param idUser
	 *            Objeto que representa el usuario, con la llave única.
	 * @param idModule
	 *            Objeto que representa el módulo, con la llave única.
	 */
	private void removeUserAccess(Users idUser, Modules idModule) {
		userAccess.get(idUser).remove(idModule);
	}

	/**
	 * Función que añade un rol a un usuario, derivando el acceso de los módulos que
	 * tiene un rol al usuario
	 * 
	 * @param idUser
	 *            Objeto que representa el usuario, con la llave única.
	 * @param idRole
	 *            Objeto que representa el rol, con la llave única.
	 */
	private void addRoleUserAccess(Users idUser, Roles idRole) {
		if (!roleUserAccess.containsKey(idUser))
			roleUserAccess.put(idUser, new HashSet<Roles>());

		roleUserAccess.get(idUser).add(idRole);

		for (Modules idModule : roleModuleAccess.get(idRole).keySet())
			addUserAccess(idUser, idModule, roleModuleAccess.get(idRole).get(idModule), false);
	}

	/**
	 * Función que remueve del mapa de accesos, los roles de un usuario, derivandose
	 * en la elimnación de todos los accesos a módulos del rol para un usuario.
	 * 
	 * @param idUser
	 *            Objeto que representa el usuario, con la llave única.
	 * @param idRole
	 *            Objeto que representa el rol, con la llave única.
	 */
	private void removeRoleUserAccess(Users idUser, Roles idRole) {
		roleUserAccess.get(idUser).remove(idRole);

		for (Modules idModule : roleModuleAccess.get(idRole).keySet())
			removeUserAccess(idUser, idModule);
	}

	/**
	 * Funciuón que añade a un rol, el módulo que se especifique, teniendo en cuenta
	 * su nivel de acceso, y además, actualiza todos los accesos para los usuarios
	 * que tengan el rol.
	 * 
	 * @param idRole
	 *            Objeto que representa el rol, con la llave única.
	 * @param idModule
	 *            Objeto que representa el módulo, con la llave única.
	 * @param accessLevel
	 *            Número entre 1 y 15 representando los niveles de acceso
	 */
	private void addRoleModuleAccess(Roles idRole, Modules idModule, Integer accessLevel) {
		if (!roleModuleAccess.containsKey(idRole))
			roleModuleAccess.put(idRole, new HashMap<Modules, Integer>());

		roleModuleAccess.get(idRole).put(idModule, accessLevel);
		for (Users idUser : roleUserAccess.keySet())
			for (Roles idRole_ : roleUserAccess.get(idUser))
				if (idRole_.equals(idRole))
					addUserAccess(idUser, idModule, accessLevel, false);
	}

	/**
	 * Funciuón que remueve de un rol, el módulo que se especifique, y además,
	 * actualiza todos los accesos para los usuarios que tengan el rol.
	 * 
	 * @param idRole
	 *            Objeto que representa el rol, con la llave única.
	 * @param idModule
	 *            Objeto que representa el módulo, con la llave única.
	 */
	private void removeRoleModuleAccess(Roles idRole, Modules idModule) {
		roleModuleAccess.get(idRole).remove(idModule);
		for (Users idUser : roleUserAccess.keySet())
			for (Roles idRole_ : roleUserAccess.get(idUser))
				if (idRole_.equals(idRole))
					removeUserAccess(idUser, idModule);
	}

	/**
	 * Función que genera un listado ordenado de módulos según la pripiedad
	 * <tt>moduleOrder</tt>, a los cuales el usuario tiene acceso.
	 * 
	 * @param idUser
	 *            Objeto que representa el usuario, con la llave única.
	 * @return Una lista de módulos ordenados
	 */
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

	/**
	 * Función que permite a partir de un arreglo de selecciones (<tt>boolean</tt>),
	 * obtener el nivel de acceso en número decimal.
	 * 
	 * @param accesses
	 *            Arreglo de 4 posiciones que indican las selecciones de accesos a
	 *            un módulo. Las posiciones del arreglo indican, 0 = Read, 1 =
	 *            Insert, 2 = Update, 3 = Delete
	 * @return Número entre 1 y 15 desde la transformación de un arreglo a
	 *         <tt>String</tt>, y luego a número según la función
	 *         <tt>getAccessCode</tt>
	 */
	public int getAccessCode(boolean[] accesses) {
		String accessesString = "";
		for (int i = 0; i < 4; i++)
			accessesString += accesses[i] ? "1" : "0";
		return getAccessCode(accessesString);
	}

	/**
	 * Función que a partir de una cadena de texto convertible a base binaria,
	 * devuelve un número en base decimal
	 * 
	 * @param accesses
	 *            Cadena de caracteres compuesta por 0 y 1 sin espacios de 4
	 *            posiciones.
	 * @return Número decimal a partir de la conversión de base 2 a base 10
	 */
	public int getAccessCode(String accesses) {
		return Integer.parseInt(accesses, 2);
	}

	/**
	 * Función que descompone un número decimal en un arreglo de selecciones. Las
	 * posiciones del arreglo indican, 0 = Read, 1 = Insert, 2 = Update, 3 = Delete
	 * 
	 * @param code
	 *            Número en base 10 que representa el nivel de acceso a un módulo
	 * @return Arreglo con 4 posiciones según definen las selecciones.
	 */
	public boolean[] getAccessFromCode(int code) {
		boolean[] access = new boolean[4];
		String binaryCode = String.format("%1$4s", String.valueOf(Integer.toString(code, 2))).replace(' ', '0');
		if (binaryCode.length() == 4)
			for (int i = 0; i < 4; i++)
				access[i] = binaryCode.charAt(i) == '1';
		return access;
	}

	/**
	 * Función que determina si un acceso está implicito en otro. Ambos parámetros
	 * se convierten a arreglos, y estos se comparan, para que almenos una de las
	 * posiciones concuerden.
	 * 
	 * @param existingAccess
	 *            Número decimal que representa un acceso, que es el número actual
	 *            de acceso.
	 * @param desiredAccess
	 *            Número decimal que representa un acceso, que es el número actual
	 *            de acceso.
	 * @return <tt>true</tt> si convergen en almenos una posición.<br>
	 *         <tt>false</tt> si no convergen.
	 */
	public boolean existsAccess(int existingAccess, int desiredAccess) {
		boolean[] access = getAccessFromCode(existingAccess);
		boolean[] desiredAccesses = getAccessFromCode(desiredAccess);
		for (int i = 0; i < 4; i++)
			if (access[i] && desiredAccesses[i])
				return true;
		return false;
	}

	/**
	 * Función que permite obtener el conjunto de módulos, así como el nivel de
	 * acceso, para un usuario en particular.
	 * 
	 * @param idUsers
	 * @return Mapa con el conjunto de módulos y sus accesos en caso que existan.
	 */
	public HashMap<Modules, Integer> getUserModules(Users idUsers) {
		return userAccess.get(idUsers);
	}

	/**
	 * Función que permite obtener el conjunto de módulos directos (no asociados a
	 * un rol) así como el nivel de acceso, para un usuario en particular.
	 * 
	 * @param idUsers
	 *            Objeto que representa el usuario, con la llave única.
	 * @return Mapa con el conjunto de módulos y sus accesos en caso que existan.
	 */
	public HashMap<Modules, Integer> getDirectUserModules(Users idUsers) {
		return directUserAccess.get(idUsers);
	}

	/**
	 * Función que permite obtener el conjunto de módulos, así como el nivel de
	 * acceso para un rol en particular.
	 * 
	 * @param idRoles
	 *            Objeto que representa el rol, con la llave única.
	 * @return Mapa con el conjunto de módulos y sus accesos en caso que existan.
	 */
	public HashMap<Modules, Integer> getRoleModules(Roles idRoles) {
		return roleModuleAccess.get(idRoles);
	}

	/**
	 * Función que permite obtener los rolesde un usuario en particular.
	 * 
	 * @param idUsers
	 *            Objeto que representa el usuario, con la llave única.
	 * @return Conjunto de roles de un usuario.
	 */
	public HashSet<Roles> getUserRoles(Users idUsers) {
		return roleUserAccess.get(idUsers);
	}

	/**
	 * @return la relación de Módulos y Usuarios como está en la base de datos
	 */
	public List<ModulesUsers> getModulesUsers() {
		return modulesUsers;
	}

	/**
	 * 
	 * @param aModulesUsers
	 *            define la lista de relación de Módulos y Usuarios
	 */
	public void setModulesUsers(List<ModulesUsers> aModulesUsers) {
		modulesUsers = aModulesUsers;
	}

	/**
	 * @return la relación de Roles y Usuarios como está en la base de datos
	 */
	public List<RolesUsers> getRolesUsers() {
		return rolesUsers;
	}

	/**
	 * @param aRolesUsers
	 *            define la lista de relación de Roles y Usuarios
	 */
	public void setRolesUsers(List<RolesUsers> aRolesUsers) {
		rolesUsers = aRolesUsers;
	}

	/**
	 * @return la relación de Roles y Módulos como está en la base de datos
	 */
	public List<RolesModules> getRolesModules() {
		return rolesModules;
	}

	/**
	 * @param aRolesModules
	 *            define la lista de relación de Roles y Módulos
	 */
	public void setRolesModules(List<RolesModules> aRolesModules) {
		rolesModules = aRolesModules;
	}

	/**
	 * Método sincronizado que permite obtener la instancia <tt>Singleton</tt>,
	 * inicializandola en caso de no estar inicializada, además de cargar los
	 * accesos que derivan en mapas.
	 * 
	 * @return Objeto de instancia <tt>Singleton</tt> de la clase.
	 * @throws Exception
	 *             Cuando hay un error cargando los accesos.
	 */
	public static synchronized AccessService getInstance() throws Exception {
		if (accessService == null)
			(accessService = new AccessService()).loadAccesses();

		return accessService;
	}
}
