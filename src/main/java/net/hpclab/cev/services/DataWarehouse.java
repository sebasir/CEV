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

public class DataWarehouse implements Serializable {
	public static final long serialVersionUID = -5732196966461312575L;

	private static DataWarehouse dataWarehouse;
	public List<Author> allAuthors;
	public List<Catalog> allCatalogs;
	public List<Collection> allCollections;
	public List<Institution> allInstitutions;
	public List<Location> allLocations;
	public List<LocationLevel> allLocationLevels;
	public List<Modules> allModules;
	public List<ModulesUsers> allModulesUsers;
	public List<RegType> allRegTypes;
	public List<Roles> allRoles;
	public List<RolesModules> allRolesModules;
	public List<RolesUsers> allRolesUsers;
	public List<SampleType> allSampleTypes;
	public List<Specimen> allSpecimens;
	public List<SpecimenContent> allSpecimenContents;
	public List<Taxonomy> allTaxonomys;
	public List<TaxonomyLevel> allTaxonomyLevels;
	public List<Users> allUsers;

	public void initLists() throws Exception {
		allAuthors = new DataBaseService<Author>().getList(Author.class);
		allCatalogs = new DataBaseService<Catalog>().getList(Catalog.class);
		allCollections = new DataBaseService<Collection>().getList(Collection.class);
		allInstitutions = new DataBaseService<Institution>().getList(Institution.class);
		allLocations = new DataBaseService<Location>().getList(Location.class);
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

	public static synchronized DataWarehouse getInstance() {
		if (dataWarehouse == null)
			dataWarehouse = new DataWarehouse();
		return dataWarehouse;
	}

}
