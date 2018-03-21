package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import net.hpclab.cev.entities.Specimen;
import net.hpclab.cev.entities.Taxonomy;
import net.hpclab.cev.entities.TaxonomyLevel;
import net.hpclab.cev.enums.ModulesEnum;
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.model.TreeHierachyModel;
import net.hpclab.cev.services.AccessService;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.DataWarehouse;
import net.hpclab.cev.services.ParseExceptionService;

@ManagedBean
@ViewScoped
public class TaxonomyBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = -2452341929369884578L;
	private DataBaseService<Taxonomy> taxonomyService;
	private String selectedLevel;
	private Taxonomy taxonomy;
	private Taxonomy parentTaxonomy;
	private TreeNode root;
	private TreeNode selectedNode;
	private HashMap<Integer, TreeNode> tree;
	private HashMap<Integer, TreeHierachyModel> abstractMap;
	private HashMap<Integer, Specimen> specimenTaxonomy;
	private HashMap<Integer, TaxonomyLevel> levelMap;
	private TreeHierachyModel abstractTree;
	private List<TaxonomyLevel> avalLevels;
	private List<Specimen> taxonomySpecimens;

	private static final Logger LOGGER = Logger.getLogger(TaxonomyBean.class.getSimpleName());

	public TaxonomyBean() throws Exception {
		taxonomyService = new DataBaseService<>(Taxonomy.class, Constant.UNLIMITED_QUERY_RESULTS);
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
		taxonomy = null;
		selectedNode = null;
		root = null;
		createTree();
	}

	public void persist() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.CREATE_ERROR;
		String transactionMessage = taxonomy.getTaxonomyName();
		TaxonomyLevel idTaxonomyLevel;
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.TAXONOMY, getUsers(facesContext),
					Constant.AccessLevel.INSERT)) {
				showAccessMessage(facesContext, OutcomeEnum.INSERT_NOT_GRANTED);
				return;
			}

			idTaxonomyLevel = levelMap.get(new Integer(selectedLevel));
			taxonomy.setIdContainer(new Taxonomy(parentTaxonomy.getIdTaxonomy()));
			taxonomy.setIdTaxlevel(idTaxonomyLevel);
			taxonomy.setStatus(StatusEnum.ACTIVO.get());
			taxonomy = taxonomyService.persist(taxonomy);
			DataWarehouse.getInstance().allTaxonomys.add(taxonomy);
			createTree();
			selectedNode = tree.get(taxonomy.getIdTaxonomy());
			openBranch(selectedNode);
			outcomeEnum = OutcomeEnum.CREATE_SUCCESS;
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
		String transactionMessage = taxonomy.getTaxonomyName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.TAXONOMY, getUsers(facesContext),
					Constant.AccessLevel.UPDATE)) {
				showAccessMessage(facesContext, OutcomeEnum.UPDATE_NOT_GRANTED);
				return;
			}

			Taxonomy tempTaxonomy = taxonomyService.merge(taxonomy);
			DataWarehouse.getInstance().allTaxonomys.remove(taxonomy);
			DataWarehouse.getInstance().allTaxonomys.add(tempTaxonomy);
			createTree();
			selectedNode = tree.get(taxonomy.getIdTaxonomy());
			openBranch(selectedNode);
			outcomeEnum = OutcomeEnum.UPDATE_SUCCESS;
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error editing: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public void delete() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.DELETE_ERROR;
		String transactionMessage = taxonomy.getTaxonomyName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.TAXONOMY, getUsers(facesContext),
					Constant.AccessLevel.DELETE)) {
				showAccessMessage(facesContext, OutcomeEnum.DELETE_NOT_GRANTED);
				return;
			}

			taxonomyService.delete(taxonomy);
			DataWarehouse.getInstance().allTaxonomys.remove(taxonomy);
			createTree();
			selectedNode = tree.get(taxonomy.getIdContainer().getIdTaxonomy());
			openBranch(selectedNode);
			outcomeEnum = OutcomeEnum.DELETE_SUCCESS;
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error deleting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	private void updateAvalLevels(Taxonomy tax, String command) {
		selectedLevel = tax.getIdTaxlevel().getIdTaxlevel().toString();
		avalLevels = new ArrayList<>();
		Integer highestLevel = Integer.MAX_VALUE;
		Integer currentLevel = tax.getIdTaxlevel().getTaxlevelRank();
		if (Constant.EDIT_COMMAND.equals(command)) {
			for (TreeHierachyModel node : abstractMap.get(tax.getIdTaxonomy()).getLeaves()) {
				if (highestLevel > node.getLevel())
					highestLevel = node.getLevel();
			}
		}

		for (TaxonomyLevel t : DataWarehouse.getInstance().allTaxonomyLevels) {
			if (Constant.CREATE_COMMAND.equals(command)) {
				if (t.getTaxlevelRank() > currentLevel)
					avalLevels.add(t);
			} else if (t.getTaxlevelRank() >= currentLevel && t.getTaxlevelRank() < highestLevel) {
				avalLevels.add(t);
			}
		}
	}

	public void createTree() {
		tree = new HashMap<>();
		abstractMap = new HashMap<>();
		TreeHierachyModel fatherNode = new TreeHierachyModel();
		TreeHierachyModel childNode = new TreeHierachyModel();
		root = null;
		for (Taxonomy t : DataWarehouse.getInstance().allTaxonomys) {
			if (root == null) {
				abstractTree = new TreeHierachyModel(t.getIdTaxonomy(), t.getIdTaxlevel().getTaxlevelRank());
				tree.put(t.getIdTaxonomy(), (root = new DefaultTreeNode(t, null)));
				abstractMap.put(t.getIdTaxonomy(), abstractTree);
			} else {
				childNode = new TreeHierachyModel(t.getIdTaxonomy(), t.getIdTaxlevel().getTaxlevelRank());
				fatherNode = abstractMap.get(t.getIdContainer().getIdTaxonomy());
				fatherNode.addNode(childNode);
				abstractMap.put(t.getIdTaxonomy(), childNode);
				tree.put(t.getIdTaxonomy(), new DefaultTreeNode(t, tree.get(t.getIdContainer().getIdTaxonomy())));
			}
		}

		TreeNode n = null;
		if (parentTaxonomy != null) {
			n = tree.get(parentTaxonomy.getIdTaxonomy());
		} else if (taxonomy != null) {
			n = tree.get(taxonomy.getIdTaxonomy());
		}
		openBranch(n);

		levelMap = new HashMap<>();
		for (TaxonomyLevel t : DataWarehouse.getInstance().allTaxonomyLevels) {
			levelMap.put(t.getIdTaxlevel(), t);
		}

		specimenTaxonomy = new HashMap<>();
		for (Specimen s : DataWarehouse.getInstance().allSpecimens) {
			specimenTaxonomy.put(s.getIdTaxonomy().getIdTaxonomy(), s);
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

	public void onNodeSelect(NodeSelectEvent event) {
		selectedNode = event.getTreeNode();
		showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_INFO,
				selectedNode.toString() + " seleccionado");
		taxonomy = (Taxonomy) selectedNode.getData();
	}

	public void setDatafromNode(String command) {
		if (selectedNode != null) {
			try {
				root.setExpanded(true);
				taxonomy = (Taxonomy) selectedNode.getData();
				updateAvalLevels(taxonomy, command);
				if (Constant.CREATE_COMMAND.equals(command)) {
					parentTaxonomy = (Taxonomy) selectedNode.getData();
					taxonomy = new Taxonomy();
				} else if (Constant.DETAIL_COMMAND.equals(command)) {
					taxonomySpecimens = new ArrayList<>();
					Stack<TreeHierachyModel> searchList = new Stack<>();
					searchList.add(abstractMap.get(taxonomy.getIdTaxonomy()));
					TreeHierachyModel node = null;
					while (!searchList.isEmpty() && taxonomySpecimens.size() <= Constant.MAX_SPECIMEN_LIST) {
						node = searchList.pop();
						if (specimenTaxonomy.containsKey(node.getNode())) {
							taxonomySpecimens.add(specimenTaxonomy.get(node.getNode()));
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

	public Taxonomy getNodeName() {
		return (Taxonomy) selectedNode.getData();
	}

	public String getLevelName(Object node) {
		return ((Taxonomy) node).getIdTaxlevel().getTaxlevelName();
	}

	public Taxonomy getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(Taxonomy taxonomy) {
		this.taxonomy = taxonomy;
	}

	public List<Taxonomy> getAllTaxonomys() {
		return DataWarehouse.getInstance().allTaxonomys;
	}

	public String getSelectedLevel() {
		return selectedLevel;
	}

	public void setSelectedLevel(String selectedLevel) {
		this.selectedLevel = selectedLevel;
	}

	public List<TaxonomyLevel> getAllLevels() {
		return DataWarehouse.getInstance().allTaxonomyLevels;
	}

	public TreeNode getTaxRoot() {
		if (selectedNode != null)
			openBranch(selectedNode);
		return root;
	}

	public void setTaxRoot(TreeNode taxRoot) {
		this.root = taxRoot;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public List<Specimen> getTaxonomySpecimens() {
		return taxonomySpecimens;
	}

	public void setTaxonomySpecimens(List<Specimen> taxonomySpecimens) {
		this.taxonomySpecimens = taxonomySpecimens;
	}

	public List<TaxonomyLevel> getAvalLevels() {
		return avalLevels;
	}

	public void setAvalLevels(List<TaxonomyLevel> avalLevels) {
		this.avalLevels = avalLevels;
	}

	public Taxonomy getParentTaxonomy() {
		return parentTaxonomy;
	}

	public void setParentTaxonomy(Taxonomy parentTaxonomy) {
		this.parentTaxonomy = parentTaxonomy;
	}
}