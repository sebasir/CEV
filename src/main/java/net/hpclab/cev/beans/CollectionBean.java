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

import net.hpclab.cev.entities.Catalog;
import net.hpclab.cev.entities.Collection;
import net.hpclab.cev.entities.Institution;
import net.hpclab.cev.entities.RegType;
import net.hpclab.cev.entities.SampleType;
import net.hpclab.cev.entities.Specimen;
import net.hpclab.cev.enums.ModulesEnum;
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.model.TreeHierachyModel;
import net.hpclab.cev.services.AccessService;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.DataWarehouse;
import net.hpclab.cev.services.ObjectRetriever;
import net.hpclab.cev.services.ParseExceptionService;

/**
 * Este servicio permite la interacción con el servicio de
 * <tt>DataBaseService</tt> para la gestión de catálogos, colecciones e
 * instituciones que componen el sistema. Principalmente expone métodos de
 * creación, edición, consulta y eliminación, validando la posibilidad de estas
 * operaciones contra el servicio de <tt>AccessesService</tt>, el cual valida el
 * usuario que realiza la operación.
 * 
 * @author Sebasir
 * @since 1.0
 * @see DataBaseService
 * @see Collection
 * @see Catalog
 * @see Specimen
 */

@ManagedBean
@ViewScoped
public class CollectionBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = -7407272469474484227L;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>Collection</tt>, lo cual permite extender todas las operaciones del
	 * servicio para esta clase.
	 */
	private DataBaseService<Collection> collectionService;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>Catalog</tt>, lo cual permite extender todas las operaciones del servicio
	 * para esta clase.
	 */
	private DataBaseService<Catalog> catalogService;

	/**
	 * Permite identificar el valor de cada categoría de manera jerárquica
	 */
	private Constant.CollectionClassType classType;

	/**
	 * Numero que representa la clave primaria de un objeto
	 */
	private Integer objectId;

	/**
	 * Cadena de texto que guarda la clave primaria de una institución
	 */
	private String selectedInstitution;

	/**
	 * Cadena de texto que guarda la clave primaria de una colección
	 */
	private String selectedCollection;

	/**
	 * Cadenta de texto que guarda la clave primaria de un catálogo
	 */
	private String selectedCatalog;

	/**
	 * Cadena de texto que guarda el nombre del tipo del objeto
	 */
	private String objectType;

	/**
	 * Cadena de texto que guarda el nombre del objeto
	 */
	private String objectName;

	/**
	 * Objeto que guarda el nombre del tipo del objeto padre
	 */
	private String objectFatherType;

	/**
	 * Objeto que guarda el nombre del objeto padre
	 */
	private String objectFatherName;

	/**
	 * Objeto que permite editar un catálogo
	 */
	private Catalog catalog;

	/**
	 * Objeto que permite editar una colección
	 */
	private Collection collection;

	/**
	 * Clave primaria de un tipo de ejemplar
	 */
	private Integer sampleTypeId;

	/**
	 * Clave primaria de un tipo de registro
	 */
	private Integer regTypeId;

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
	 * Mapa que permite obtener una lista de especímenes a partir de la clave
	 * primaria
	 */
	private HashMap<Integer, ArrayList<Specimen>> specimenCollection;

	/**
	 * Arbol abstracto que permite referenciar el arbol jerárquico
	 */
	private TreeHierachyModel abstractTree;

	/**
	 * Lista de colecciones disponibles
	 */
	private List<Collection> avalCollections;

	/**
	 * Lista de catálogos disponibles
	 */
	private List<Catalog> avalCatalogs;

	/**
	 * Lista de especímenes de una colección
	 */
	private List<Specimen> collectionSpecimens;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(CollectionBean.class.getSimpleName());

	/**
	 * Constructor que permite inicializar los servicios de <tt>DataBaseService</tt>
	 * 
	 * @throws Exception
	 *             Cuando existe un error en el servicio de <tt>DataBaseService</tt>
	 */
	public CollectionBean() throws Exception {
		collectionService = new DataBaseService<>(Collection.class);
		catalogService = new DataBaseService<>(Catalog.class);
	}

	/**
	 * Permite inicializar los filtros de creación y busqueda
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
		classType = null;
		objectId = null;
		selectedInstitution = null;
		selectedCollection = null;
		selectedCatalog = null;
		objectType = null;
		objectName = null;
		objectFatherType = null;
		objectFatherName = null;
		catalog = null;
		collection = null;
		sampleTypeId = null;
		regTypeId = null;
		root = null;
		selectedNode = null;
		selectedNode = null;
		root = null;
		createTree();
	}

	/**
	 * Permite guardar un objeto de tipo catálogo o colección que se haya definido
	 * en la interfáz validando el permiso de escritura
	 */
	public void persist() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.CREATE_ERROR;
		String transactionMessage = objectName;
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.COLLECTION, getUsers(facesContext),
					Constant.AccessLevel.INSERT)) {
				showAccessMessage(facesContext, OutcomeEnum.INSERT_NOT_GRANTED);
				return;
			}

			switch (classType) {
			case INSTITUTION:
				collection = new Collection();
				collection.setCollectionName(objectName);
				collection.setStatus(StatusEnum.ACTIVO.get());
				collection.setIdInstitution((Institution) tree.get(objectId).getData());
				collection = collectionService.persist(collection);
				DataWarehouse.getInstance().allCollections.add(collection);
				objectId = Constant.COLLECTION_HINT + collection.getIdCollection();
				break;
			case COLLECTION:
				catalog = new Catalog();
				catalog.setCatalogName(objectName);
				catalog.setStatus(StatusEnum.ACTIVO.get());
				catalog.setIdCollection((Collection) tree.get(objectId).getData());
				catalog = catalogService.persist(catalog);
				DataWarehouse.getInstance().allCatalogs.add(catalog);
				objectId = Constant.CATALOG_HINT + catalog.getIdCatalog();
				break;
			case CATALOG:
				transactionMessage = "No es posible crear otros elementos";
				throw new Exception();
			}
			createTree();
			openBranch(tree.get(objectId));
			outcomeEnum = OutcomeEnum.CREATE_SUCCESS;
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error persisting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite editar un objeto de tipo catálogo o colección que se haya redefinido
	 * en la interfáz validando el permiso de modificación
	 */
	public void edit() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.UPDATE_ERROR;
		String transactionMessage = null;
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.COLLECTION, getUsers(facesContext),
					Constant.AccessLevel.UPDATE)) {
				showAccessMessage(facesContext, OutcomeEnum.UPDATE_NOT_GRANTED);
				return;
			}

			switch (classType) {
			case INSTITUTION:
				transactionMessage = "No es posible editar las instituciones";
				throw new Exception();
			case COLLECTION:
				transactionMessage = collection.getCollectionName();
				collection.setCollectionName(objectName);
				Collection tempCollection = collectionService.merge(collection);
				objectId = Constant.COLLECTION_HINT + tempCollection.getIdCollection();
				DataWarehouse.getInstance().allCollections.remove(collection);
				DataWarehouse.getInstance().allCollections.add(tempCollection);
				break;
			case CATALOG:
				transactionMessage = catalog.getCatalogName();
				catalog.setCatalogName(objectName);
				Catalog tempCatalog = catalogService.merge(catalog);
				objectId = Constant.CATALOG_HINT + tempCatalog.getIdCatalog();
				DataWarehouse.getInstance().allCatalogs.remove(catalog);
				DataWarehouse.getInstance().allCatalogs.add(tempCatalog);
				break;
			}
			outcomeEnum = OutcomeEnum.UPDATE_SUCCESS;
			createTree();
			openBranch(tree.get(objectId));
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error editing: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite eliminar un objeto de tipo catálogo o colección validando el permiso
	 * de eliminación
	 */
	public void delete() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.DELETE_ERROR;
		String transactionMessage = null;
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.COLLECTION, getUsers(facesContext),
					Constant.AccessLevel.DELETE)) {
				showAccessMessage(facesContext, OutcomeEnum.DELETE_NOT_GRANTED);
				return;
			}

			switch (classType) {
			case INSTITUTION:
				break;
			case COLLECTION:
				transactionMessage = collection.getCollectionName();
				objectId = Constant.INSTITUTION_HINT + collection.getIdInstitution().getIdInstitution();
				collectionService.delete(collection);
				DataWarehouse.getInstance().allCollections.remove(collection);
				break;
			case CATALOG:
				transactionMessage = catalog.getCatalogName();
				objectId = Constant.COLLECTION_HINT + catalog.getIdCollection().getIdCollection();
				catalogService.delete(catalog);
				DataWarehouse.getInstance().allCatalogs.remove(catalog);
				break;
			}
			createTree();
			openBranch(tree.get(objectId));
			outcomeEnum = OutcomeEnum.DELETE_SUCCESS;
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error deleting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite crear los árboles y mapas de la jerarquía de catálogo, colección e
	 * institución
	 */
	public void createTree() {
		tree = new HashMap<>();
		abstractMap = new HashMap<>();
		TreeHierachyModel fatherNode = new TreeHierachyModel();
		TreeHierachyModel childNode = new TreeHierachyModel();
		root = new DefaultTreeNode(Constant.NO_MENU_TYPE, "Colecciones", null);
		tree.put(0, root);
		abstractTree = new TreeHierachyModel(0, Constant.ROOT_LEVEL);
		abstractMap.put(0, abstractTree);
		for (Institution i : DataWarehouse.getInstance().allInstitutions) {
			childNode = new TreeHierachyModel(Constant.INSTITUTION_HINT + i.getIdInstitution(),
					Constant.INSTITUTION_LEVEL);
			fatherNode = abstractMap.get(0);
			fatherNode.addNode(childNode);
			abstractMap.put(Constant.INSTITUTION_HINT + i.getIdInstitution(), childNode);
			tree.put(Constant.INSTITUTION_HINT + i.getIdInstitution(),
					new DefaultTreeNode(Constant.MENU_TYPE, i, tree.get(0)));
		}

		for (Collection c : DataWarehouse.getInstance().allCollections) {
			childNode = new TreeHierachyModel(Constant.COLLECTION_HINT + c.getIdCollection(),
					Constant.COLLECTION_LEVEL);
			fatherNode = abstractMap.get(Constant.INSTITUTION_HINT + c.getIdInstitution().getIdInstitution());
			fatherNode.addNode(childNode);
			abstractMap.put(Constant.COLLECTION_HINT + c.getIdCollection(), childNode);
			tree.put(Constant.COLLECTION_HINT + c.getIdCollection(), new DefaultTreeNode(Constant.MENU_TYPE, c,
					tree.get(Constant.INSTITUTION_HINT + c.getIdInstitution().getIdInstitution())));
		}

		for (Catalog c : DataWarehouse.getInstance().allCatalogs) {
			childNode = new TreeHierachyModel(Constant.CATALOG_HINT + c.getIdCatalog(), Constant.CATALOG_LEVEL);
			fatherNode = abstractMap.get(Constant.COLLECTION_HINT + c.getIdCollection().getIdCollection());
			fatherNode.addNode(childNode);
			abstractMap.put(Constant.CATALOG_HINT + c.getIdCatalog(), childNode);
			tree.put(Constant.CATALOG_HINT + c.getIdCatalog(), new DefaultTreeNode(Constant.NO_MENU_TYPE, c,
					tree.get(Constant.COLLECTION_HINT + c.getIdCollection().getIdCollection())));
		}

		TreeNode n = null;
		if (catalog != null) {
			n = tree.get(Constant.CATALOG_HINT + catalog.getIdCatalog());
			collection = (Collection) n.getParent().getData();
		}

		openBranch(n);

		specimenCollection = new HashMap<>();
		for (Specimen s : DataWarehouse.getInstance().allSpecimens) {
			if (!specimenCollection.containsKey(Constant.CATALOG_HINT + s.getIdCatalog().getIdCatalog()))
				specimenCollection.put(Constant.CATALOG_HINT + s.getIdCatalog().getIdCatalog(), new ArrayList<>());
			specimenCollection.get(Constant.CATALOG_HINT + s.getIdCatalog().getIdCatalog()).add(s);
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
		if (selectedNode.getData() instanceof Catalog) {
			catalog = (Catalog) selectedNode.getData();
			collection = (Collection) selectedNode.getParent().getData();
		} else {
			catalog = null;
			collection = null;
		}
	}

	/**
	 * Permite obtener y definir la información de nombres y tipos de objeto para el
	 * nodo seleccionado y el padre de este
	 * 
	 * @param command
	 *            Cadena de texto que permite diferenciar el tipo de operación a
	 *            realizar
	 */
	public void setDatafromNode(String command) {
		if (selectedNode != null) {
			try {
				root.setExpanded(true);
				classType = null;
				openBranch(selectedNode);
				if (selectedNode.getData() instanceof Institution) {
					classType = Constant.CollectionClassType.INSTITUTION;
					objectId = Constant.INSTITUTION_HINT + ((Institution) selectedNode.getData()).getIdInstitution();
					objectType = "Institución";
					objectName = ((Institution) selectedNode.getData()).getInstitutionName();
				} else if (selectedNode.getData() instanceof Collection) {
					classType = Constant.CollectionClassType.COLLECTION;
					collection = (Collection) selectedNode.getData();
					objectId = Constant.COLLECTION_HINT + collection.getIdCollection();
					objectType = "Colección";
					objectName = collection.getCollectionName();
				} else if (selectedNode.getData() instanceof Catalog) {
					catalog = (Catalog) selectedNode.getData();
					classType = Constant.CollectionClassType.CATALOG;
					objectId = Constant.CATALOG_HINT + catalog.getIdCatalog();
					objectType = "Catálogo";
					objectName = catalog.getCatalogName();
				} else {
					objectId = 0;
				}

				if (classType == null && !Constant.DETAIL_COMMAND.equals(command))
					return;

				if (Constant.CREATE_COMMAND.equals(command)) {
					objectFatherName = objectName;
					objectName = "";
					switch (classType) {
					case INSTITUTION:
						objectFatherType = "Institución";
						objectType = "Colección";
						break;
					case COLLECTION:
						objectFatherType = "Colección";
						objectType = "Catalogo";
						break;
					case CATALOG:
						objectFatherType = "Catálogo";
						objectType = "";
						break;
					}
				} else if (Constant.DETAIL_COMMAND.equals(command)) {
					collectionSpecimens = new ArrayList<>();
					Stack<TreeHierachyModel> searchList = new Stack<>();
					searchList.add(abstractMap.get(objectId));
					TreeHierachyModel node = null;
					while (!searchList.isEmpty() && collectionSpecimens.size() <= Constant.MAX_SPECIMEN_LIST) {
						node = searchList.pop();
						if (specimenCollection.containsKey(node.getNode()))
							collectionSpecimens.addAll(specimenCollection.get(node.getNode()));

						searchList.addAll(node.getLeaves());
					}
					searchList = null;
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error setting DataFromNode", e);
			}
		}
	}

	/**
	 * @return Permite obtener el nodo principal, y abrirlo si existe algún nodo
	 *         seleccionado
	 */
	public TreeNode getCollectionRoot() {
		if (selectedNode != null)
			openBranch(selectedNode);
		return root;
	}

	/**
	 * @param collectionRoot
	 *            Nodo principal del árbol jerárquico a definir
	 */
	public void setCollectionRoot(TreeNode collectionRoot) {
		this.root = collectionRoot;
	}

	/**
	 * @return Nodo seleccionado desde la interfaz
	 */
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * @return Objeto que permite editar un catálogo
	 */
	public Catalog getCatalog() {
		return catalog;
	}

	/**
	 * @param catalog
	 *            Objeto que permite editar un catálogo a definir
	 */
	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * @return Objeto que permite editar una colección
	 */
	public Collection getCollection() {
		return collection;
	}

	/**
	 * @param collection
	 *            Objeto que permite editar una colección a definir
	 */
	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	/**
	 * @param selectedNode
	 *            Nodo seleccionado desde la interfaz
	 */
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	/**
	 * @return Lista de especímenes de una colección
	 */
	public List<Specimen> getCollectionSpecimens() {
		return collectionSpecimens;
	}

	/**
	 * @param collectionSpecimens
	 *            Lista de especímenes de una colección a definir
	 */
	public void setCollectionSpecimens(List<Specimen> collectionSpecimens) {
		this.collectionSpecimens = collectionSpecimens;
	}

	/**
	 * @return Permite obtener un objeto de tipo de muestra desde la clave primaria
	 */
	public SampleType getSampleType() {
		if (sampleTypeId != null)
			return ObjectRetriever.getObjectFromId(SampleType.class, sampleTypeId);
		return null;
	}

	/**
	 * @return Permite obtener un objeto de tipo de registro desde la clave primaria
	 */
	public RegType getRegType() {
		if (regTypeId != null)
			return ObjectRetriever.getObjectFromId(RegType.class, regTypeId);
		return null;
	}

	/**
	 * @return Clave primaria de un tipo de ejemplar
	 */
	public Integer getSampleTypeId() {
		return sampleTypeId;
	}

	/**
	 * @param sampleTypeId
	 *            Clave primaria de un tipo de ejemplar a definir
	 */
	public void setSampleTypeId(Integer sampleTypeId) {
		this.sampleTypeId = sampleTypeId;
	}

	/**
	 * @return Clave primaria de un tipo de registro
	 */
	public Integer getRegTypeId() {
		return regTypeId;
	}

	/**
	 * @param regTypeId
	 *            Clave primaria de un tipo de registro a definir
	 */
	public void setRegTypeId(Integer regTypeId) {
		this.regTypeId = regTypeId;
	}

	/**
	 * @return Permite tener acceso a todas las instituciones
	 */
	public List<Institution> getAllInstitutions() {
		return DataWarehouse.getInstance().allInstitutions;
	}

	/**
	 * @return Permite tener acceso a todas las colecciones
	 */
	public List<Collection> getAllCollections() {
		return DataWarehouse.getInstance().allCollections;
	}

	/**
	 * @return Permite tener acceso a todas los catalogos
	 */
	public List<Catalog> getAllCatalogs() {
		return DataWarehouse.getInstance().allCatalogs;
	}

	/**
	 * @return Permite tener acceso a todas los tipos de muestra
	 */
	public List<SampleType> getAllSampleTypes() {
		return DataWarehouse.getInstance().allSampleTypes;
	}

	/**
	 * @return Permite tener acceso a todas los tipos de registro
	 */
	public List<RegType> getAllRegTypes() {
		return DataWarehouse.getInstance().allRegTypes;
	}

	/**
	 * @return Cadena de texto que guarda la clave primaria de una institución
	 */
	public String getSelectedInstitution() {
		return selectedInstitution;
	}

	/**
	 * @param selectedInstitution
	 *            Cadena de texto que guarda la clave primaria de una institución a
	 *            definir
	 */
	public void setSelectedInstitution(String selectedInstitution) {
		this.selectedInstitution = selectedInstitution;
	}

	/**
	 * @return Cadena de texto que guarda la clave primaria de una colección
	 */
	public String getSelectedCollection() {
		return selectedCollection;
	}

	/**
	 * @param selectedCollection
	 *            Cadena de texto que guarda la clave primaria de una colección a
	 *            definir
	 */
	public void setSelectedCollection(String selectedCollection) {
		this.selectedCollection = selectedCollection;
	}

	/**
	 * @return Cadenta de texto que guarda la clave primaria de un catálogo
	 */
	public String getSelectedCatalog() {
		return selectedCatalog;
	}

	/**
	 * @param selectedCatalog
	 *            Cadenta de texto que guarda la clave primaria de un catálogo a
	 *            definir
	 */
	public void setSelectedCatalog(String selectedCatalog) {
		this.selectedCatalog = selectedCatalog;
	}

	/**
	 * @return Lista de colecciones disponibles
	 */
	public List<Collection> getAvalCollections() {
		return avalCollections;
	}

	/**
	 * @param avalCollections
	 *            Lista de colecciones disponibles a definir
	 */
	public void setAvalCollections(List<Collection> avalCollections) {
		this.avalCollections = avalCollections;
	}

	/**
	 * @return Lista de catálogos disponibles
	 */
	public List<Catalog> getAvalCatalogs() {
		return avalCatalogs;
	}

	/**
	 * @param avalCatalogs
	 *            Lista de catálogos disponibles a definir
	 */
	public void setAvalCatalogs(List<Catalog> avalCatalogs) {
		this.avalCatalogs = avalCatalogs;
	}

	/**
	 * @return Cadena de texto que guarda el nombre del tipo del objeto
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * @return Cadena de texto que guarda el nombre del objeto
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @return Objeto que guarda el nombre del objeto padre
	 */
	public String getObjectFatherName() {
		return objectFatherName;
	}

	/**
	 * @return Objeto que guarda el nombre del tipo del objeto padre
	 */
	public String getObjectFatherType() {
		return objectFatherType;
	}

	/**
	 * @param objectType
	 *            Cadena de texto que guarda el nombre del tipo del objeto a definir
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	/**
	 * @param objectName
	 *            Cadena de texto que guarda el nombre del objeto a definir
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * @param objectFatherType
	 *            Objeto que guarda el nombre del tipo del objeto padre
	 */
	public void setObjectFatherType(String objectFatherType) {
		this.objectFatherType = objectFatherType;
	}
}