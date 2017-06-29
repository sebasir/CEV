package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import net.hpclab.cev.entities.Location;
import net.hpclab.cev.entities.LocationLevel;
import net.hpclab.cev.entities.Specimen;
import net.hpclab.cev.services.DataBaseService;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean
@SessionScoped
public class LocationBean extends UtilsBean implements Serializable {

    private DataBaseService<Location> locationService;
    private DataBaseService<LocationLevel> locationLevelService;
    private static final long serialVersionUID = 1L;
    private Location location;
    private List<Location> allLocations;
    private Location parentLocation;
    private HashMap<Integer, TreeNode> tree;
    private TreeNode root;
    private TreeNode selectedNode;
    private List<Specimen> locationSpecimens;
    private List<LocationLevel> allLevels;
    private String selectedCont;
    private String selectedLevel;

    public LocationBean() {
        try {
            locationService = new DataBaseService<>(Location.class, -1);
            locationLevelService = new DataBaseService<>(LocationLevel.class);
        } catch (Exception ex) {
            Logger.getLogger(LocationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void init() {
        createLocTree();
    }

    public void persist() {
        try {
            location.setIdContainer(new Location(parentLocation.getIdLocation()));
            location.setIdLoclevel(new LocationLevel(new Integer(selectedLevel)));
            setLocation(locationService.persist(getLocation()));
            if (getLocation() != null && getLocation().getIdLocation() != null) {
                System.out.println("error -> pailas");
                createLocTree();
            } else {
                System.out.println("error -> pailas");
            }
        } catch (Exception e) {
            System.out.println("error -> pailas" + e.getMessage());
        }
    }

    public void delete() {
        try {
            locationService.delete(getLocation());
            createLocTree();
            System.out.println("error -> pailas");
        } catch (Exception e) {
            System.out.println("error -> pailas" + e.getMessage());
        }
    }

    public void prepareCreate() {
        try {
            parentLocation = location;
            location = new Location();
            List<LocationLevel> locLevels = locationLevelService.getList();
            allLevels = new ArrayList<>();
            for (LocationLevel l : locLevels) {
                if (l.getLoclevelRank() > parentLocation.getIdLoclevel().getLoclevelRank()) {
                    allLevels.add(l);
                }
            }
        } catch (Exception e) {

        }
    }

    public void edit() {
        try {
            setLocation(locationService.merge(getLocation()));
            System.out.println("error -> pailas");
        } catch (Exception e) {
            System.out.println("error -> pailas" + e.getMessage());
        }
    }

    public void prepareUpdate() {
        try {
            selectedLevel = location.getIdLoclevel().getIdLoclevel().toString();
            List<LocationLevel> taxLevels = locationLevelService.getList();
            allLevels = new ArrayList<>();
            for (LocationLevel t : taxLevels) {
                if (t.getLoclevelRank() > parentLocation.getIdLoclevel().getLoclevelRank()) {
                    allLevels.add(t);
                }
            }
        } catch (Exception e) {

        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Location> getAllLocations() {
        return allLocations;
    }

    public String getSelectedCont() {
        return selectedCont;
    }

    public void setSelectedCont(String selectedCont) {
        this.selectedCont = selectedCont;
    }

    public List<LocationLevel> getAllLevels() {
        return allLevels;
    }

    public void setAllLevels(List<LocationLevel> allLevels) {
        this.allLevels = allLevels;
    }

    public String getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(String selectedLevel) {
        this.selectedLevel = selectedLevel;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
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

    private void createLocTree() {
        try {
            List<Location> locList = locationService.getList("Location.findOrderedAsc");
            tree = new HashMap<>();
            root = null;
            if (locList != null) {
                for (Location l : locList) {
                    if (root == null) {
                        tree.put(l.getIdLocation(), (root = new DefaultTreeNode(l, null)));
                    } else {
                        tree.put(l.getIdLocation(), new DefaultTreeNode(l, tree.get(l.getIdContainer().getIdLocation())));
                    }
                }

                TreeNode n = null;
                if (parentLocation != null) {
                    n = tree.get(parentLocation.getIdLocation());
                } else if (location != null) {
                    n = tree.get(location.getIdLocation());
                }
                openBranch(n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Location getNodeName() {
        return (Location) selectedNode.getData();
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
                location = (Location) selectedNode.getData();
                if (!order.equals("create")) {
                    if (selectedNode.getParent() != null) {
                        parentLocation = (Location) selectedNode.getParent().getData();
                    }
                }
                if (order.equals("detail")) {
                    locationSpecimens = location.getSpecimenList();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            RequestContext context = RequestContext.getCurrentInstance();

            if (order.equals("detail")) {
                context.execute("PF('locationDetail').show()");
            } else if (order.equals("create")) {
                prepareCreate();
                context.execute("PF('locationCreate').show()");
            } else if (order.equals("edit")) {
                prepareUpdate();
                context.execute("PF('locationEdit').show()");
            } else if (order.equals("delete")) {
                context.execute("PF('locationDelete').show()");
            }
        }
    }
}
