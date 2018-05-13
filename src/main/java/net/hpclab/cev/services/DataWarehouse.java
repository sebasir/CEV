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
import java.util.List;

import net.hpclab.cev.entities.Author;
import net.hpclab.cev.entities.Catalog;
import net.hpclab.cev.entities.Collection;
import net.hpclab.cev.entities.Institution;
import net.hpclab.cev.entities.Location;
import net.hpclab.cev.entities.LocationLevel;
import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.entities.ModulesUsers;
import net.hpclab.cev.entities.RegType;
import net.hpclab.cev.entities.Roles;
import net.hpclab.cev.entities.RolesModules;
import net.hpclab.cev.entities.RolesUsers;
import net.hpclab.cev.entities.SampleType;
import net.hpclab.cev.entities.Specimen;
import net.hpclab.cev.entities.SpecimenContent;
import net.hpclab.cev.entities.Taxonomy;
import net.hpclab.cev.entities.TaxonomyLevel;
import net.hpclab.cev.entities.Users;

/**
 * Es un servicio creado para la encapsulación del servicio de almacenamiento de
 * las listas de dominio desde la base de datos. El servicio ofrece una manera
 * de acceso único a través de un objeto <tt>Singleton</tt> creado en memoria
 * estática, y sincronizada, permitiendo acceder la misma instancia desde varios
 * hilos simultaneamente.
 * 
 * @since 1.0
 * @author Sebasir
 * 
 */
public class DataWarehouse implements Serializable {
	public static final long serialVersionUID = -5732196966461312575L;

	/**
	 * Objeto estático de esta clase el cual permite acceder siempre a la misma
	 * referencia.
	 */
	private static DataWarehouse dataWarehouse;

	/**
	 * Lista para acceder a todos los autores registrados
	 */
	public List<Author> allAuthors;

	/**
	 * Lista para acceder a todos los catálogos registrados
	 */
	public List<Catalog> allCatalogs;

	/**
	 * Lista para acceder a todos las colecciones registradas
	 */
	public List<Collection> allCollections;

	/**
	 * Lista para acceder a todos las instituciones registradas
	 */
	public List<Institution> allInstitutions;

	/**
	 * Lista para acceder a todos los ubicaciones registradas
	 */
	public List<Location> allLocations;

	/**
	 * Lista para acceder a todos los niveles de ubicación registrados
	 */
	public List<LocationLevel> allLocationLevels;

	/**
	 * Lista para acceder a todos los módulos registrados
	 */
	public List<Modules> allModules;

	/**
	 * Lista para acceder a todos las relaciones de módulos y usuarios registrados
	 */
	public List<ModulesUsers> allModulesUsers;

	/**
	 * Lista para acceder a todos los tipos de registro registrados
	 */
	public List<RegType> allRegTypes;

	/**
	 * Lista para acceder a todos los roles registrados
	 */
	public List<Roles> allRoles;

	/**
	 * Lista para acceder a todos las relaciones de módulos y roles registradas
	 */
	public List<RolesModules> allRolesModules;

	/**
	 * Lista para acceder a todos las relaciones de roles y usuarios registradas
	 */
	public List<RolesUsers> allRolesUsers;

	/**
	 * Lista para acceder a todos los tipos de ejemplares registrados
	 */
	public List<SampleType> allSampleTypes;

	/**
	 * Lista para acceder a todos los especímenes registrados
	 */
	public List<Specimen> allSpecimens;

	/**
	 * Lista para acceder a todos los contenidos gráficos registrados
	 */
	public List<SpecimenContent> allSpecimenContents;

	/**
	 * Lista para acceder a todos las clasificaciones taxonómicas registradas
	 */
	public List<Taxonomy> allTaxonomys;

	/**
	 * Lista para acceder a todos los niveles de clasificaciones registrados
	 */
	public List<TaxonomyLevel> allTaxonomyLevels;

	/**
	 * Lista para acceder a todos los usuarios registrados
	 */
	public List<Users> allUsers;

	/**
	 * Función que permite inicializar todas las listas con los servicios de
	 * <tt>DataBaseService</tt> para cada una de las entidades enlazadas al
	 * servicio, para luego ser consumidas, sin afectar el rendimiento de la base de
	 * datos.
	 * 
	 * @throws Exception
	 */
	public void initLists() throws Exception {
		allAuthors = new DataBaseService<Author>().getList(Author.class);
		allCatalogs = new DataBaseService<Catalog>().getList(Catalog.class);
		allCollections = new DataBaseService<Collection>().getList(Collection.class);
		allInstitutions = new DataBaseService<Institution>().getList(Institution.class);
		allLocations = new DataBaseService<Location>(Location.class, Constant.UNLIMITED_QUERY_RESULTS).getList();
		allLocationLevels = new DataBaseService<LocationLevel>().getList(LocationLevel.class);
		allModules = new DataBaseService<Modules>().getList(Modules.class);
		allModulesUsers = new DataBaseService<ModulesUsers>().getList(ModulesUsers.class);
		allRegTypes = new DataBaseService<RegType>().getList(RegType.class);
		allRoles = new DataBaseService<Roles>().getList(Roles.class);
		allRolesModules = new DataBaseService<RolesModules>().getList(RolesModules.class);
		allRolesUsers = new DataBaseService<RolesUsers>().getList(RolesUsers.class);
		allSampleTypes = new DataBaseService<SampleType>().getList(SampleType.class);
		allSpecimens = new DataBaseService<Specimen>().getList(Specimen.class);
		allSpecimenContents = new DataBaseService<SpecimenContent>().getList(SpecimenContent.class);
		allTaxonomys = new DataBaseService<Taxonomy>().getList(Taxonomy.class);
		allTaxonomyLevels = new DataBaseService<TaxonomyLevel>().getList(TaxonomyLevel.class);
		allUsers = new DataBaseService<Users>().getList(Users.class);

		AccessService.getInstance().loadAccesses();
		AuditService.clearInstance();
	}

	/**
	 * Método sincronizado que permite obtener la instancia <tt>Singleton</tt>,
	 * inicializandola en caso de no estar inicializada.
	 * 
	 * @return Objeto de instancia <tt>Singleton</tt> de la clase.
	 */
	public static synchronized DataWarehouse getInstance() {
		if (dataWarehouse == null)
			dataWarehouse = new DataWarehouse();
		return dataWarehouse;
	}

}
