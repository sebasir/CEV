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
	public static List<Author> allAuthors;
	public static List<Catalog> allCatalogs;
	public static List<Collection> allCollections;
	public static List<Institution> allInstitutions;
	public static List<Location> allLocations;
	public static List<LocationLevel> allLocationLevels;
	public static List<Modules> allModules;
	public static List<ModulesUsers> allModulesUsers;
	public static List<RegType> allRegTypes;
	public static List<Roles> allRoles;
	public static List<RolesModules> allRolesModules;
	public static List<RolesUsers> allRolesUsers;
	public static List<SampleType> allSampleTypes;
	public static List<Specimen> allSpecimens;
	public static List<SpecimenContent> allSpecimenContents;
	public static List<Taxonomy> allTaxonomys;
	public static List<TaxonomyLevel> allTaxonomyLevels;
	public static List<Users> allUsers;

	public static void initLists() throws Exception {
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
	}
}
