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

/**
 * Este servicio permite la interacción con el servicio de
 * <tt>DataBaseService</tt> para la gestión de clasificaciones taxonómicas que
 * componen un espécimen. Principalmente expone métodos de creación, edición,
 * consulta y eliminación, validando la posibilidad de estas operaciones contra
 * el servicio de <tt>AccessesService</tt>, el cual valida el usuario que
 * realiza la operación.
 * 
 * @author Sebasir
 * @since 1.0
 * @see DataBaseService
 * @see Taxonomy
 * @see TaxonomyLevel
 * @see TreeHierachyModel
 */

@ManagedBean
@ViewScoped
public class TaxonomyBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = -2452341929369884578L;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>Taxonomy</tt>, lo cual permite extender todas las operaciones del
	 * servicio para esta clase.
	 */
	private DataBaseService<Taxonomy> taxonomyService;

	/**
	 * Cadena de texto que guarda la clave primaria de un nivel de clasificación
	 */
	private String selectedLevel;

	/**
	 * Objeto que permite editar una clasificación
	 */
	private Taxonomy taxonomy;

	/**
	 * Objeto que permite referenciar una clasificación padre
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
	private static final Logger LOGGER = Logger.getLogger(TaxonomyBean.class.getSimpleName());

	/**
	 * Constructor que permite inicializar los servicios de <tt>DataBaseService</tt>
	 */
	public TaxonomyBean() throws Exception {
		taxonomyService = new DataBaseService<>(Taxonomy.class, Constant.UNLIMITED_QUERY_RESULTS);
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
	 * Permite guardar un objeto de tipo clasificación que se haya definido en la
	 * interfáz validando el permiso de escritura
	 */
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

	/**
	 * Permite editar un objeto de tipo clasificación que se haya redefinido en la
	 * interfáz validando el permiso de modificación
	 */
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

	/**
	 * Permite eliminar una clasificación, validando el permiso de eliminación
	 */
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

	/**
	 * Permite obtener una lista de niveles permitidos para la edición o creación
	 * 
	 * @param tax
	 *            Objeto de tipo clasificación
	 * @param command
	 *            Permite diferenciar los distintos niveles dependiendo de la accion
	 */
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

	/**
	 * Permite crear los árboles y mapas de la jerarquía de clasificaciones
	 */
	public void createTree() {
		tree = new HashMap<>();
		abstractMap = new HashMap<>();

		PriorityQueue<Taxonomy> orderedTaxonomy = new PriorityQueue<>(DataWarehouse.getInstance().allTaxonomys.size(),
				new Comparator<Taxonomy>() {
					@Override
					public int compare(Taxonomy o1, Taxonomy o2) {
						if (o1.getIdTaxlevel().getTaxlevelRank() < o2.getIdTaxlevel().getTaxlevelRank())
							return -1;
						else if (o1.getIdTaxlevel().getTaxlevelRank() == o2.getIdTaxlevel().getTaxlevelRank())
							if (o1.getIdTaxonomy() < o2.getIdTaxonomy())
								return -1;
						return 1;
					}

				});

		orderedTaxonomy.addAll(DataWarehouse.getInstance().allTaxonomys);

		TreeHierachyModel fatherNode = new TreeHierachyModel();
		TreeHierachyModel childNode = new TreeHierachyModel();
		root = null;
		Taxonomy t = null;
		while (!orderedTaxonomy.isEmpty()) {
			t = orderedTaxonomy.poll();
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
		for (TaxonomyLevel tl : DataWarehouse.getInstance().allTaxonomyLevels) {
			levelMap.put(tl.getIdTaxlevel(), tl);
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
	 * Permite obtener y definir la información de la clasificación
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
	 * @return Objeto que permite editar una clasificación
	 */
	public Taxonomy getTaxonomy() {
		return taxonomy;
	}

	/**
	 * @param taxonomy
	 *            Objeto que permite editar una clasificación a definir
	 */
	public void setTaxonomy(Taxonomy taxonomy) {
		this.taxonomy = taxonomy;
	}

	/**
	 * @return Permite tener acceso a todas las clasificaciones
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
	 * @return Permite tener acceso a todas los niveles de clasificación
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
	 * @return Objeto que permite referenciar una clasificación padre
	 */
	public Taxonomy getParentTaxonomy() {
		return parentTaxonomy;
	}

	/**
	 * @param parentTaxonomy
	 *            Objeto que permite referenciar una clasificación padre a definir
	 */
	public void setParentTaxonomy(Taxonomy parentTaxonomy) {
		this.parentTaxonomy = parentTaxonomy;
	}
}