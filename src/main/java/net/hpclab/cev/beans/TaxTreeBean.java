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
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.model.TreeHierachyModel;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.DataWarehouse;
import net.hpclab.cev.services.ObjectRetriever;

/**
 * Este servicio permite la interacción con todas las clasificaciones
 * taxonómicas que están registradas en el sistema.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Taxonomy
 * @see TaxonomyLevel
 * @see Specimen
 */

@ManagedBean
@ViewScoped
public class TaxTreeBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = -2452341929369884578L;

	/**
	 * Cadena de texto que guarda la clave primaria de un nivel de clasificación
	 */
	private String selectedLevel;

	/**
	 * Parámetro que viene a partir de una naveaación
	 */
	private String taxonomyTreeId;

	/**
	 * Objeto que permite conocer las propiedades de una clasificación
	 */
	private Taxonomy taxonomy;

	/**
	 * Objeto que permite conocer las propiedades del padre de una clasificación
	 */
	private Taxonomy parentTaxonomy;

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
	private HashMap<Integer, Specimen> specimenTaxonomy;

	/**
	 * Mapa que permite obtener un nivel de clasificación a partir de la clave
	 * primaria
	 */
	private HashMap<Integer, TaxonomyLevel> levelMap;

	/**
	 * Arbol abstracto que permite referenciar el arbol jerárquico
	 */
	private TreeHierachyModel abstractTree;

	/**
	 * Lista de niveles disponibles
	 */
	private List<TaxonomyLevel> avalLevels;

	/**
	 * Lista de especímenes disponibles para una clasificación
	 */
	private List<Specimen> taxonomySpecimens;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(TaxTreeBean.class.getSimpleName());

	/**
	 * Permite inicializar el servicio y limpiar los objetos
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
	 * Permite limpiar los objetos
	 */
	public void limpiarFiltros() {
		selectedLevel = null;
		taxonomy = null;
		selectedNode = null;
		root = null;
		createTree();
	}

	/**
	 * Permite crear los árboles y mapas de la jerarquía de clasificaciones
	 */
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
	 * Permite obtener un nodo de una seleccion
	 * 
	 * @param event
	 *            Evento del cual se obtiene el nodo
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		selectedNode = event.getTreeNode();
		showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_INFO,
				selectedNode.toString() + " seleccionado");
		taxonomy = (Taxonomy) selectedNode.getData();
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
				taxonomy = (Taxonomy) selectedNode.getData();
				if (Constant.DETAIL_COMMAND.equals(command)) {
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

	/**
	 * Permite obtener la clasificación a partir de un nodo
	 * 
	 * @return Objeto de la clasificación
	 */
	public Taxonomy getNodeName() {
		return (Taxonomy) selectedNode.getData();
	}

	/**
	 * Permite obtener el nombre del nivel de la clasificación
	 * 
	 * @param node
	 *            Node del cual se extre el nombre
	 * @return Nombre del nivel
	 */
	public String getLevelName(Object node) {
		return ((Taxonomy) node).getIdTaxlevel().getTaxlevelName();
	}

	/**
	 * Permite obtener el objeto de clasificación taxonómica a partir de un
	 * parámetro que llega de URL
	 */
	public void getTaxFromParam() {
		if (taxonomyTreeId == null || taxonomyTreeId.isEmpty())
			return;

		try {
			taxonomy = ObjectRetriever.getObjectFromId(Taxonomy.class, Integer.parseInt(taxonomyTreeId));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error obteniendo elemento", e);
		}
		createTree();
	}

	/**
	 * @return Objeto que permite conocer las propiedades de una clasificación
	 */
	public Taxonomy getTaxonomy() {
		return taxonomy;
	}

	/**
	 * @param taxonomy
	 *            Objeto que permite conocer las propiedades de una clasificación a
	 *            definir
	 */
	public void setTaxonomy(Taxonomy taxonomy) {
		this.taxonomy = taxonomy;
	}

	/**
	 * @return Permite tener acceso a todas las clasificaciones taxonómicas
	 */
	public List<Taxonomy> getAllTaxonomys() {
		return DataWarehouse.getInstance().allTaxonomys;
	}

	/**
	 * @return Cadena de texto que guarda la clave primaria de un nivel de
	 *         clasificación
	 */
	public String getSelectedLevel() {
		return selectedLevel;
	}

	/**
	 * @param selectedLevel
	 *            Cadena de texto que guarda la clave primaria de un nivel de
	 *            clasificación a definir
	 */
	public void setSelectedLevel(String selectedLevel) {
		this.selectedLevel = selectedLevel;
	}

	/**
	 * @return Permite obtener todos los niveles de clasificación
	 */
	public List<TaxonomyLevel> getAllLevels() {
		return DataWarehouse.getInstance().allTaxonomyLevels;
	}

	/**
	 * @return Nodo principal del árbol jerárquico abierto
	 */
	public TreeNode getTaxRoot() {
		if (selectedNode != null)
			openBranch(selectedNode);
		return root;
	}

	/**
	 * @return Parámetro que viene a partir de una naveaación
	 */
	public String getTaxonomyTreeId() {
		return taxonomyTreeId;
	}

	/**
	 * @param taxonomyTreeId
	 *            Parámetro que viene a partir de una naveaación a definir
	 */
	public void setTaxonomyTreeId(String taxonomyTreeId) {
		this.taxonomyTreeId = taxonomyTreeId;
	}

	/**
	 * @param taxRoot
	 *            Nodo principal del árbol jerárquico a definir
	 */
	public void setTaxRoot(TreeNode taxRoot) {
		this.root = taxRoot;
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
	 * @return Lista de especímenes disponibles para una clasificación
	 */
	public List<Specimen> getTaxonomySpecimens() {
		return taxonomySpecimens;
	}

	/**
	 * @param taxonomySpecimens
	 *            Lista de especímenes disponibles para una clasificación a definir
	 */
	public void setTaxonomySpecimens(List<Specimen> taxonomySpecimens) {
		this.taxonomySpecimens = taxonomySpecimens;
	}

	/**
	 * @return Lista de niveles disponibles
	 */
	public List<TaxonomyLevel> getAvalLevels() {
		return avalLevels;
	}

	/**
	 * @param avalLevels
	 *            Lista de niveles disponibles a definir
	 */
	public void setAvalLevels(List<TaxonomyLevel> avalLevels) {
		this.avalLevels = avalLevels;
	}

	/**
	 * @return Objeto que permite conocer las propiedades del padre de una
	 *         clasificación
	 */
	public Taxonomy getParentTaxonomy() {
		return parentTaxonomy;
	}

	/**
	 * @param parentTaxonomy
	 *            Objeto que permite conocer las propiedades del padre de una
	 *            clasificación a definir
	 */
	public void setParentTaxonomy(Taxonomy parentTaxonomy) {
		this.parentTaxonomy = parentTaxonomy;
	}
}