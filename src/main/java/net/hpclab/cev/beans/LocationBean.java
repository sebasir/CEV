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

package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import net.hpclab.cev.entities.Location;
import net.hpclab.cev.entities.LocationLevel;
import net.hpclab.cev.entities.Specimen;
import net.hpclab.cev.enums.ModulesEnum;
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.model.TreeHierachyModel;
import net.hpclab.cev.services.AccessService;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.DataWarehouse;
import net.hpclab.cev.services.ParseExceptionService;

/**
 * Este servicio permite la interacción con el servicio de
 * <tt>DataBaseService</tt> para la gestión de ubicaciones que componen a un
 * espécimen. Principalmente expone métodos de creación, edición, consulta y
 * eliminación, validando la posibilidad de estas operaciones contra el servicio
 * de <tt>AccessesService</tt>, el cual valida el usuario que realiza la
 * operación.
 * 
 * @author Sebasir
 * @since 1.0
 * @see DataBaseService
 * @see Location
 * @see LocationLevel
 * @see Specimen
 * @see TreeHierachyModel
 */

@ManagedBean
@SessionScoped
public class LocationBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 6170712285673190627L;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>Location</tt>, lo cual permite extender todas las operaciones del
	 * servicio para esta clase.
	 */
	private DataBaseService<Location> locationService;

	/**
	 * Cadena de texto que guarda la clave primaria de un nivel de ubicación
	 */
	private String selectedLevel;

	/**
	 * Objeto que permite editar una ubicación
	 */
	private Location location;

	/**
	 * Objeto que permite referenciar una ubicación padre
	 */
	private Location parentLocation;

	/**
	 * Nodo principal del árbol jerárquico
	 */
	private TreeNode root;

	/**
	 * Nodo seleccionado desde la interfaz
	 */
	private TreeNode selectedNode;

	/**
	 * Mapa que permite obtener un nodo a partir de la clave primaria
	 */
	private HashMap<Integer, TreeNode> tree;

	/**
	 * Mapa que permite obtener un nodo abstracto a partir de la clave primaria
	 */
	private HashMap<Integer, TreeHierachyModel> abstractMap;

	/**
	 * Mapa que permite obtener un espécimen a partir de la clave primaria
	 */
	private HashMap<Integer, Specimen> specimenLocation;

	/**
	 * Arbol abstracto que permite referenciar el arbol jerárquico
	 */
	private TreeHierachyModel abstractTree;

	/**
	 * Lista de niveles disponibles
	 */
	private List<LocationLevel> avalLevels;

	/**
	 * Lista de especímenes disponibles para una ubicación
	 */
	private List<Specimen> locationSpecimens;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(LocationBean.class.getSimpleName());

	/**
	 * Constructor que permite inicializar los servicios de <tt>DataBaseService</tt>
	 */
	public LocationBean() throws Exception {
		locationService = new DataBaseService<>(Location.class, Constant.UNLIMITED_QUERY_RESULTS);
	}

	/**
	 * Permite inicializar los filtros de creación y búsqueda
	 */
	@PostConstruct
	public void init() {
		try {
			limpiarFiltros();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Permite limpiar los filtros
	 */
	public void limpiarFiltros() {
		selectedLevel = null;
		location = null;
		selectedNode = null;
		root = null;
		createTree();
	}

	/**
	 * Permite guardar un objeto de tipo ubicación que se haya definido en la
	 * interfáz validando el permiso de escritura
	 */
	public void persist() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.CREATE_ERROR;
		String transactionMessage = location.getLocationName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.LOCATION, getUsers(facesContext),
					Constant.AccessLevel.INSERT)) {
				showAccessMessage(facesContext, OutcomeEnum.INSERT_NOT_GRANTED);
				return;
			}

			location.setIdContainer(new Location(parentLocation.getIdLocation()));
			location.setIdLoclevel(new LocationLevel(new Integer(selectedLevel)));
			location = locationService.persist(location);
			DataWarehouse.getInstance().allLocations.add(location);
			outcomeEnum = OutcomeEnum.CREATE_SUCCESS;
			createTree();
			selectedNode = tree.get(location.getIdLocation());
			openBranch(selectedNode);
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error persisting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
		selectedLevel = null;
	}

	/**
	 * Permite editar un objeto de tipo ubicación que se haya redefinido en la
	 * interfáz validando el permiso de modificación
	 */
	public void edit() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.UPDATE_ERROR;
		String transactionMessage = location.getLocationName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.LOCATION, getUsers(facesContext),
					Constant.AccessLevel.UPDATE)) {
				showAccessMessage(facesContext, OutcomeEnum.UPDATE_NOT_GRANTED);
				return;
			}

			Location tempLocation = locationService.merge(location);
			DataWarehouse.getInstance().allLocations.remove(location);
			DataWarehouse.getInstance().allLocations.add(tempLocation);
			outcomeEnum = OutcomeEnum.UPDATE_SUCCESS;
			createTree();
			selectedNode = tree.get(location.getIdLocation());
			openBranch(selectedNode);
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error editing: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite eliminar una ubicación, validando el permiso de eliminación
	 */
	public void delete() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.DELETE_ERROR;
		String transactionMessage = location.getLocationName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.COLLECTION, getUsers(facesContext),
					Constant.AccessLevel.DELETE)) {
				showAccessMessage(facesContext, OutcomeEnum.DELETE_NOT_GRANTED);
				return;
			}

			locationService.delete(location);
			DataWarehouse.getInstance().allLocations.remove(location);
			createTree();
			selectedNode = tree.get(location.getIdContainer().getIdLocation());
			openBranch(selectedNode);
			outcomeEnum = OutcomeEnum.DELETE_SUCCESS;
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error deleting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite obtener una lista de niveles permitidos para la edición o creación
	 * 
	 * @param loc
	 *            Objeto de tipo ubicación
	 * @param command
	 *            Permite diferenciar los distintos niveles dependiendo de la accion
	 */
	private void updateAvalLevels(Location loc, String command) {
		selectedLevel = loc.getIdLoclevel().getIdLoclevel().toString();
		avalLevels = new ArrayList<>();
		Integer highestLevel = Integer.MAX_VALUE;
		Integer currentLevel = loc.getIdLoclevel().getLoclevelRank();
		if (Constant.EDIT_COMMAND.equals(command)) {
			for (TreeHierachyModel node : abstractMap.get(loc.getIdLocation()).getLeaves()) {
				if (highestLevel > node.getLevel())
					highestLevel = node.getLevel();
			}
		}

		for (LocationLevel l : DataWarehouse.getInstance().allLocationLevels) {
			if (Constant.CREATE_COMMAND.equals(command)) {
				if (l.getLoclevelRank() > currentLevel)
					avalLevels.add(l);
			} else if (l.getLoclevelRank() >= currentLevel && l.getLoclevelRank() < highestLevel) {
				avalLevels.add(l);
			}
		}
	}

	/**
	 * Permite crear los árboles y mapas de la jerarquía de ubicaciones
	 */
	public void createTree() {
		tree = new HashMap<>();
		abstractMap = new HashMap<>();
		PriorityQueue<Location> orderedLocation = new PriorityQueue<>(DataWarehouse.getInstance().allLocations.size(),
				new Comparator<Location>() {
					@Override
					public int compare(Location o1, Location o2) {
						if (o1.getIdLoclevel().getLoclevelRank() < o2.getIdLoclevel().getLoclevelRank())
							return -1;
						else if (o1.getIdLoclevel().getLoclevelRank() == o2.getIdLoclevel().getLoclevelRank())
							if (o1.getIdLocation() < o2.getIdLocation())
								return -1;
						return 1;
					}

				});

		orderedLocation.addAll(DataWarehouse.getInstance().allLocations);
		TreeHierachyModel fatherNode = new TreeHierachyModel();
		TreeHierachyModel childNode = new TreeHierachyModel();
		root = null;
		Location t = null;
		while (!orderedLocation.isEmpty()) {
			t = orderedLocation.poll();
			if (root == null) {
				abstractTree = new TreeHierachyModel(t.getIdLocation(), t.getIdLoclevel().getLoclevelRank());
				tree.put(t.getIdLocation(), (root = new DefaultTreeNode(t, null)));
				abstractMap.put(t.getIdLocation(), abstractTree);
			} else {
				childNode = new TreeHierachyModel(t.getIdLocation(), t.getIdLoclevel().getLoclevelRank());
				fatherNode = abstractMap.get(t.getIdContainer().getIdLocation());
				fatherNode.addNode(childNode);
				abstractMap.put(t.getIdLocation(), childNode);
				tree.put(t.getIdLocation(), new DefaultTreeNode(t, tree.get(t.getIdContainer().getIdLocation())));
			}
		}

		TreeNode n = null;
		if (parentLocation != null) {
			n = tree.get(parentLocation.getIdLocation());
		} else if (location != null) {
			n = tree.get(location.getIdLocation());
		}
		openBranch(n);

		specimenLocation = new HashMap<>();
		for (Specimen s : DataWarehouse.getInstance().allSpecimens) {
			specimenLocation.put(s.getIdLocation().getIdLocation(), s);
		}
	}

	/**
	 * Permite abrir una rama del arbol
	 * 
	 * @param node
	 *            Nodo final de la rama a abrir
	 */
	private void openBranch(TreeNode node) {
		if (node == null) {
			return;
		}
		deselectAll();
		node.setSelected(true);
		while (node != null) {
			node.setExpanded(true);
			node = node.getParent();
		}
	}

	/**
	 * Permite deseleccionar todos los nodos del arbol
	 */
	public void deselectAll() {
		for (TreeNode t : tree.values()) {
			t.setSelected(false);
			t.setExpanded(false);
		}
	}

	/**
	 * Permite obtener y definir la información de la ubicación
	 * 
	 * @param command
	 *            Cadena de texto que permite diferenciar el tipo de operación a
	 *            realizar
	 */
	public void setDatafromNode(String command) {
		if (selectedNode != null) {
			try {
				root.setExpanded(true);
				location = (Location) selectedNode.getData();
				openBranch(selectedNode);
				updateAvalLevels(location, command);
				if (Constant.CREATE_COMMAND.equals(command)) {
					parentLocation = (Location) selectedNode.getData();
					location = new Location();
				} else if (Constant.DETAIL_COMMAND.equals(command)) {
					locationSpecimens = new ArrayList<>();
					Stack<TreeHierachyModel> searchList = new Stack<>();
					searchList.add(abstractMap.get(location.getIdLocation()));
					TreeHierachyModel node = null;
					while (!searchList.isEmpty() && locationSpecimens.size() <= Constant.MAX_SPECIMEN_LIST) {
						node = searchList.pop();
						if (specimenLocation.containsKey(node.getNode())) {
							locationSpecimens.add(specimenLocation.get(node.getNode()));
						}
						searchList.addAll(node.getLeaves());
					}
					searchList = null;
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error setting TaxFromNode", e);
			}
		}
	}

	/**
	 * Permite obtener la ubicación a partir de un nodo
	 * 
	 * @return Objeto de la ubicación
	 */
	public Location getNodeName() {
		return (Location) selectedNode.getData();
	}

	/**
	 * Permite obtener un nodo de una seleccion
	 * 
	 * @param event
	 *            Evento del cual se obtiene el nodo
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		selectedNode = event.getTreeNode();
		showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_INFO,
				selectedNode.toString() + " seleccionado");
		location = (Location) selectedNode.getData();
	}

	/**
	 * @return Permtite obtener la respresentación de un objeto tipo JSON de las
	 *         propiedades de una ubicación para ser geolocalizada en el mapa de la
	 *         interfaz
	 */
	public String setMapCenter() {
		JSONObject json = new JSONObject();
		try {
			location = getNodeName();
			json.put("latitude", location.getLatitude());
			json.put("longitude", location.getLongitude());
			json.put("name", location.getLocationName());
			json.put("zoom", 12);

			List<Specimen> specimens = new ArrayList<>();
			for (Specimen s : DataWarehouse.getInstance().allSpecimens)
				if (s.getIdLocation().equals(location))
					specimens.add(s);

			if (specimens != null && !specimens.isEmpty()) {
				JSONArray jsonSpecimensArray = new JSONArray();
				JSONObject jsonSpecimens;
				for (Specimen specimen : specimens) {
					jsonSpecimens = new JSONObject();
					jsonSpecimens.put("scientificName", specimen.getIdLocation().getLocationName()
							+ (specimen.getSpecificEpithet() == null ? "" : " " + specimen.getSpecificEpithet()));
					jsonSpecimens.put("commonName", specimen.getCommonName());
					jsonSpecimensArray.put(jsonSpecimens);
				}
				json.put("tooltip", jsonSpecimensArray);
			} else {
				json.put("tooltip", "none");
			}
		} catch (Exception e) {
			json.put("latitude", 4.583333);
			json.put("longitude", -74.066667);
			json.put("name", "Colombia");
			json.put("zoom", 9);
			json.put("tooltip", "none");
		}
		return json.toString();
	}

	/**
	 * @return Objeto que permite editar una ubicación
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            Objeto que permite editar una ubicación a definir
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return Permite el accesos a todas las ubicaciones
	 */
	public List<Location> getAllLocations() {
		return DataWarehouse.getInstance().allLocations;
	}

	/**
	 * @return Cadena de texto que guarda la clave primaria de un nivel de ubicación
	 */
	public String getSelectedLevel() {
		return selectedLevel;
	}

	/**
	 * @param selectedLevel
	 *            Cadena de texto que guarda la clave primaria de un nivel de
	 *            ubicación a definir
	 */
	public void setSelectedLevel(String selectedLevel) {
		this.selectedLevel = selectedLevel;
	}

	/**
	 * @return Permite el accesos a todos los niveles de ubicaciones
	 */
	public List<LocationLevel> getAllLevels() {
		return DataWarehouse.getInstance().allLocationLevels;
	}

	/**
	 * @return Nodo principal del árbol jerárquico
	 */
	public TreeNode getLocRoot() {
		return root;
	}

	/**
	 * @param locRoot
	 *            Nodo principal del árbol jerárquico a definir
	 */
	public void setLocRoot(TreeNode locRoot) {
		this.root = locRoot;
	}

	/**
	 * @return Nodo seleccionado desde la interfaz
	 */
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * @param selectedNode
	 *            Nodo seleccionado desde la interfaz a definir
	 */
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	/**
	 * @return Lista de especímenes disponibles para una ubicación
	 */
	public List<Specimen> getLocationSpecimens() {
		return locationSpecimens;
	}

	/**
	 * @param locationSpecimens
	 *            Lista de especímenes disponibles para una ubicación a definir
	 */
	public void setLocationSpecimens(List<Specimen> locationSpecimens) {
		this.locationSpecimens = locationSpecimens;
	}

	/**
	 * @return Lista de niveles disponibles
	 */
	public List<LocationLevel> getAvalLevels() {
		return avalLevels;
	}

	/**
	 * @param avalLevels
	 *            Lista de niveles disponibles a definir
	 */
	public void setAvalLevels(List<LocationLevel> avalLevels) {
		this.avalLevels = avalLevels;
	}

	/**
	 * @return Objeto que permite referenciar una ubicación padre
	 */
	public Location getParentLocation() {
		return parentLocation;
	}

	/**
	 * @param parentLocation
	 *            Objeto que permite referenciar una ubicación padre a definir
	 */
	public void setParentLocation(Location parentLocation) {
		this.parentLocation = parentLocation;
	}
}
