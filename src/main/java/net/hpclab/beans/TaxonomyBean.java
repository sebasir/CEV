package net.hpclab.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.hpclab.entities.Specimen;
import net.hpclab.entities.Taxonomy;
import net.hpclab.entities.TaxonomyLevel;
import net.hpclab.sessions.TaxonomySession;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean
@SessionScoped
public class TaxonomyBean extends Utilsbean implements Serializable {

    @Inject
    private TaxonomySession taxonomySession;

    private static final long serialVersionUID = 1L;
    private Taxonomy taxonomy;
    private Taxonomy parentTaxonomy;
    private TreeNode root;
    private TreeNode selectedNode;
    private HashMap<Integer, TreeNode> tree;
    private List<Taxonomy> allTaxonomys;
    private List<TaxonomyLevel> allLevels;
    private List<Specimen> taxonomySpecimens;
    private String selectedLevel;
    private boolean onTreeEdit;

    public TaxonomyBean() {
	   taxonomySession = new TaxonomySession();
    }

    @PostConstruct
    public void init() {
	   createTaxTree();
    }

    public String persist() {
	   try {
		  taxonomy.setIdContainer(new Taxonomy(parentTaxonomy.getIdTaxonomy()));
		  taxonomy.setIdTaxlevel(new TaxonomyLevel(new Integer(selectedLevel)));
		  setTaxonomy(taxonomySession.persist(getTaxonomy()));
		  if (getTaxonomy() != null && getTaxonomy().getIdTaxonomy() != null) {
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(taxonomy, Actions.createSuccess));
			 createTaxTree();
			 onTreeEdit = true;
		  } else {
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(taxonomy, Actions.createError));
		  }
	   } catch (Exception e) {
		  e.printStackTrace();
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(taxonomy, Actions.createError));
	   }
	   selectedLevel = null;
	   return findAllTaxonomys();
    }

    public void edit() {
	   try {
		  setTaxonomy(taxonomySession.merge(getTaxonomy()));
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomy(), Actions.updateSuccess));
		  createTaxTree();
		  onTreeEdit = true;
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomy(), Actions.updateError));
	   }
    }
    
    public void delete() {
	   try {
		  taxonomySession.delete(getTaxonomy());
		  createTaxTree();
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomy(), Actions.deleteSuccess));
		  onTreeEdit = true;
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(getTaxonomy(), Actions.deleteError));
	   }
    }

    public void prepareCreate() {
	   onTreeEdit = false;
	   parentTaxonomy = taxonomy;
	   taxonomy = new Taxonomy();
	   List<TaxonomyLevel> taxLevels = (List<TaxonomyLevel>) taxonomySession.findListByQuery("TaxonomyLevel.findAll", TaxonomyLevel.class);
	   allLevels = new ArrayList<TaxonomyLevel>();
	   for (TaxonomyLevel t : taxLevels) {
		  if (t.getTaxlevelRank() > parentTaxonomy.getIdTaxlevel().getTaxlevelRank()) {
			 allLevels.add(t);
		  }
	   }
    }

    public void prepareUpdate() {
	   onTreeEdit = false;
	   selectedLevel = taxonomy.getIdTaxlevel().getIdTaxlevel().toString();
	   List<TaxonomyLevel> taxLevels = (List<TaxonomyLevel>) taxonomySession.findListByQuery("TaxonomyLevel.findAll", TaxonomyLevel.class);
	   allLevels = new ArrayList<TaxonomyLevel>();
	   for (TaxonomyLevel t : taxLevels) {
		  if (t.getTaxlevelRank() > parentTaxonomy.getIdTaxlevel().getTaxlevelRank()) {
			 allLevels.add(t);
		  }
	   }
    }

    public String findAllTaxonomys() {
	   setAllTaxonomys(taxonomySession.listAll());
	   return null;
    }

    public Taxonomy getTaxonomy() {
	   return taxonomy;
    }

    public void setTaxonomy(Taxonomy taxonomy) {
	   this.taxonomy = taxonomy;
    }

    public List<Taxonomy> getAllTaxonomys() {
	   findAllTaxonomys();
	   return allTaxonomys;
    }

    public void setAllTaxonomys(List<Taxonomy> allTaxonomys) {
	   this.allTaxonomys = allTaxonomys;
    }

    public String getSelectedLevel() {
	   return selectedLevel;
    }

    public void setSelectedLevel(String selectedLevel) {
	   this.selectedLevel = selectedLevel;
    }

    public List<TaxonomyLevel> getAllLevels() {
	   return allLevels;
    }

    public void setAllLevels(List<TaxonomyLevel> allLevels) {
	   this.allLevels = allLevels;
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

    private void createTaxTree() {
	   List<Taxonomy> taxList = taxonomySession.findListByQuery("Taxonomy.findOrderedAsc");
	   tree = new HashMap<Integer, TreeNode>();
	   root = null;
	   if (taxList != null) {
		  for (Taxonomy t : taxList) {
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
	   if(!onTreeEdit)
		  for (TreeNode t : tree.values()) {
			 t.setSelected(false);
			 t.setExpanded(false);
		  }
    }
    
    public void selectNodeFromId(Integer idLocation) {
	   selectedNode = tree.get(idLocation);
	   onTreeEdit = false;
	   openBranch(selectedNode);
    }

    public void setTaxfromNode(String order) {
	   if (selectedNode != null) {
		  try {
			 root.setExpanded(true);
			 taxonomy = (Taxonomy) selectedNode.getData();
			 if (!order.equals("create")) {
				if (selectedNode.getParent() != null) {
				    parentTaxonomy = (Taxonomy) selectedNode.getParent().getData();
				}
			 }
			 if (order.equals("detail")) {
				taxonomySpecimens = taxonomy.getSpecimenList();
			 }
		  } catch (Exception e) {
			 e.printStackTrace();
		  }
		  RequestContext context = RequestContext.getCurrentInstance();

		  if (order.equals("detail")) {
			 context.execute("PF('taxonomyDetail').show()");
		  } else if (order.equals("create")) {
			 prepareCreate();
			 context.execute("PF('taxonomyCreate').show()");
		  } else if (order.equals("edit")) {
			 prepareUpdate();
			 context.execute("PF('taxonomyEdit').show()");
		  } else if (order.equals("delete")) {
			 context.execute("PF('taxonomyDelete').show()");
		  }
	   }
    }
}
