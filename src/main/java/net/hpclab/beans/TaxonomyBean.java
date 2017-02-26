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
import net.hpclab.entities.entNaming;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean
@SessionScoped
public class TaxonomyBean extends UtilsBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private Taxonomy taxonomy;
    private Taxonomy parentTaxonomy;
    private TreeNode root;
    private TreeNode selectedNode;
    private HashMap<Integer, TreeNode> tree;
    private List<TaxonomyLevel> avalLevels;
    private List<Specimen> taxonomySpecimens;
    private String selectedLevel;
    private TaxonomyLevelBean taxonomyLevelBean;

    public TaxonomyBean() {
    }

    @PostConstruct
    public void init() {
        System.out.println("Inicializando lista 'Taxonomys'");
        if (allTaxonomys == null) {
            allTaxonomys = new ArrayList<Taxonomy>();
        }
        prepareBeans();
        createTaxTree();
    }

    public void persist() {
        try {
            taxonomy.setIdContainer(new Taxonomy(parentTaxonomy.getIdTaxonomy()));
            taxonomy.setIdTaxlevel(new TaxonomyLevel(new Integer(selectedLevel)));
            //setTaxonomy(taxonomySession.persist(getTaxonomy()));
            if (getTaxonomy() != null && getTaxonomy().getIdTaxonomy() != null) {
                allTaxonomys.add(taxonomy);
                launchMessage(taxonomy, Actions.createSuccess);
                createTaxTree();
            } else {
                launchMessage(taxonomy, Actions.createError);
            }
        } catch (Exception e) {
            e.printStackTrace();
            launchMessage(taxonomy, Actions.createError);
        }
        selectedLevel = null;
    }

    public void edit() {
        try {
            //setTaxonomy(taxonomySession.merge(getTaxonomy()));
            launchMessage(taxonomy, Actions.updateSuccess);
            createTaxTree();
        } catch (Exception e) {
            launchMessage(taxonomy, Actions.updateError);
        }
    }

    public void delete() {
        /*if (taxonomySession.delete(taxonomy)) {
            createTaxTree();
            launchMessage(getTaxonomy(), Actions.deleteSuccess);
        } else {
            launchMessage(getTaxonomy(), Actions.deleteError);
        }*/
    }

    private void prepareBeans() {
        FacesContext fCon = FacesContext.getCurrentInstance();
        taxonomyLevelBean = (TaxonomyLevelBean) fCon.getApplication().getELResolver().getValue(fCon.getELContext(), null, "taxonomyLevelBean");
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

    public void prepareUpdate() {
        selectedLevel = taxonomy.getIdTaxlevel().getIdTaxlevel().toString();
        avalLevels = new ArrayList<TaxonomyLevel>();
        for (TaxonomyLevel t : allTaxonomyLevels) {
            if (t.getTaxlevelRank() > parentTaxonomy.getIdTaxlevel().getTaxlevelRank()) {
                avalLevels.add(t);
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

    private void launchMessage(entNaming ent, Actions act) {
        FacesContext.getCurrentInstance().addMessage(null, showMessage(ent, act));
    }

    public List<TaxonomyLevel> getAvalLevels() {
        return avalLevels;
    }

    public void setAvalLevels(List<TaxonomyLevel> avalLevels) {
        this.avalLevels = avalLevels;
    }
}
