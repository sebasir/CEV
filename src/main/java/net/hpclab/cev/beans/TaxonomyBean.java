package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import net.hpclab.cev.entities.Specimen;
import net.hpclab.cev.entities.Taxonomy;
import net.hpclab.cev.entities.TaxonomyLevel;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.DataBaseService;

@ManagedBean
@ViewScoped
public class TaxonomyBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private DataBaseService<Taxonomy> taxonomyService;
	private DataBaseService<TaxonomyLevel> taxonomyLevelService;
	private String selectedLevel;
	private Taxonomy taxonomy;
	private Taxonomy parentTaxonomy;
	private TreeNode root;
	private TreeNode selectedNode;
	private HashMap<Integer, TreeNode> tree;
	private List<Taxonomy> allTaxonomys;
	private List<TaxonomyLevel> allTaxonomyLevels;
	private List<TaxonomyLevel> avalLevels;
	private List<Specimen> taxonomySpecimens;

	private static final Logger LOGGER = Logger.getLogger(TaxonomyBean.class.getSimpleName());

	public TaxonomyBean() throws Exception {
		taxonomyService = new DataBaseService<>(Taxonomy.class, Constant.UNLIMITED_QUERY_RESULTS);
		taxonomyLevelService = new DataBaseService<>(TaxonomyLevel.class, Constant.UNLIMITED_QUERY_RESULTS);
	}

	@PostConstruct
	public void init() {
		System.out.println("Inicializando lista 'Taxonomys'");
		try {
			allTaxonomys = taxonomyService.getList();
			allTaxonomyLevels = taxonomyLevelService.getList();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		createTaxTree();
	}

	public void persist() {
		try {
			taxonomy.setIdContainer(new Taxonomy(parentTaxonomy.getIdTaxonomy()));
			taxonomy.setIdTaxlevel(new TaxonomyLevel(new Integer(selectedLevel)));
			setTaxonomy(taxonomyService.persist(getTaxonomy()));
			if (getTaxonomy() != null && getTaxonomy().getIdTaxonomy() != null) {
				allTaxonomys.add(taxonomy);
				System.out.println("mensaje");
				createTaxTree();
			} else {
				System.out.println("mensaje");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("mensaje");
		}
		selectedLevel = null;
	}

	public void edit() {
		try {
			setTaxonomy(taxonomyService.merge(getTaxonomy()));
			System.out.println("mensaje");
			createTaxTree();
		} catch (Exception e) {
			System.out.println("mensaje");
		}
	}

	public void delete() {
		try {
			taxonomyService.delete(taxonomy);
			createTaxTree();
			System.out.println("mensaje");
		} catch (Exception e) {
			System.out.println("mensaje " + e.getMessage());
		}
	}

	public void setTaxonomyTree() {
		taxonomy = (Taxonomy) selectedNode.getData();
	}

	public void prepareCreate() {
		parentTaxonomy = taxonomy;
		taxonomy = new Taxonomy();
		selectedLevel = null;
		avalLevels = new ArrayList<TaxonomyLevel>();

		for (TaxonomyLevel t : allTaxonomyLevels) {
			if (t.getTaxlevelRank() > parentTaxonomy.getIdTaxlevel().getTaxlevelRank()) {
				avalLevels.add(t);
			}
		}
	}

	private void updateAvalLevels(Taxonomy tax, String command) {
		selectedLevel = tax.getIdTaxlevel().getIdTaxlevel().toString();
		avalLevels = new ArrayList<>();
		for (TaxonomyLevel t : allTaxonomyLevels) {
			if (Constant.CREATE_COMMAND.equals(command)) {
				if (t.getTaxlevelRank() > tax.getIdTaxlevel().getTaxlevelRank())
					avalLevels.add(t);
			} else if (t.getTaxlevelRank() >= tax.getIdTaxlevel().getTaxlevelRank())
				avalLevels.add(t);
		}
	}

	private void createTaxTree() {
		tree = new HashMap<Integer, TreeNode>();
		root = null;
		if (allTaxonomys != null) {
			for (Taxonomy t : allTaxonomys) {
				if (root == null) {
					tree.put(t.getIdTaxonomy(), (root = new DefaultTreeNode(t, null)));
				} else {
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
		}
	}

	public Taxonomy getNodeName() {
		return (Taxonomy) selectedNode.getData();
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

	public void selectNodeFromId(Integer idLocation) {
		selectedNode = tree.get(idLocation);
		openBranch(selectedNode);
	}

	public void setTaxfromNode(String command) {
		if (selectedNode != null) {
			try {
				root.setExpanded(true);
				taxonomy = (Taxonomy) selectedNode.getData();
				updateAvalLevels(taxonomy, command);
				if (Constant.CREATE_COMMAND.equals(command)) {
					parentTaxonomy = (Taxonomy) selectedNode.getData();
					taxonomy = new Taxonomy();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Taxonomy getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(Taxonomy taxonomy) {
		this.taxonomy = taxonomy;
	}

	public List<Taxonomy> getAllTaxonomys() {
		return allTaxonomys;
	}

	public String getSelectedLevel() {
		return selectedLevel;
	}

	public void setSelectedLevel(String selectedLevel) {
		this.selectedLevel = selectedLevel;
	}

	public List<TaxonomyLevel> getAllLevels() {
		return allTaxonomyLevels;
	}

	public TreeNode getTaxRoot() {
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
