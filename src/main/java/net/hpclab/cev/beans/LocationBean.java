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

@ManagedBean
@SessionScoped
public class LocationBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 6170712285673190627L;
	private DataBaseService<Location> locationService;
	private String selectedLevel;
	private Location location;
	private Location parentLocation;
	private TreeNode root;
	private TreeNode selectedNode;
	private HashMap<Integer, TreeNode> tree;
	private HashMap<Integer, TreeHierachyModel> abstractMap;
	private HashMap<Integer, Specimen> specimenLocation;
	private TreeHierachyModel abstractTree;
	private List<LocationLevel> avalLevels;
	private List<Specimen> locationSpecimens;

	private static final Logger LOGGER = Logger.getLogger(LocationBean.class.getSimpleName());

	public LocationBean() throws Exception {
		locationService = new DataBaseService<>(Location.class, Constant.UNLIMITED_QUERY_RESULTS);
	}

	@PostConstruct
	public void init() {
		try {
			limpiarFiltros();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	public void limpiarFiltros() {
		selectedLevel = null;
		location = null;
		selectedNode = null;
		root = null;
		createTree();
	}

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

	public void deselectAll() {
		for (TreeNode t : tree.values()) {
			t.setSelected(false);
			t.setExpanded(false);
		}
	}

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

	public Location getNodeName() {
		return (Location) selectedNode.getData();
	}

	public void onNodeSelect(NodeSelectEvent event) {
		selectedNode = event.getTreeNode();
		showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_INFO,
				selectedNode.toString() + " seleccionado");
		location = (Location) selectedNode.getData();
	}

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

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<Location> getAllLocations() {
		return DataWarehouse.getInstance().allLocations;
	}

	public String getSelectedLevel() {
		return selectedLevel;
	}

	public void setSelectedLevel(String selectedLevel) {
		this.selectedLevel = selectedLevel;
	}

	public List<LocationLevel> getAllLevels() {
		return DataWarehouse.getInstance().allLocationLevels;
	}

	public TreeNode getLocRoot() {
		return root;
	}

	public void setLocRoot(TreeNode locRoot) {
		this.root = locRoot;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public List<Specimen> getLocationSpecimens() {
		return locationSpecimens;
	}

	public void setLocationSpecimens(List<Specimen> locationSpecimens) {
		this.locationSpecimens = locationSpecimens;
	}

	public List<LocationLevel> getAvalLevels() {
		return avalLevels;
	}

	public void setAvalLevels(List<LocationLevel> avalLevels) {
		this.avalLevels = avalLevels;
	}

	public Location getParentLocation() {
		return parentLocation;
	}

	public void setParentLocation(Location parentLocation) {
		this.parentLocation = parentLocation;
	}
}
