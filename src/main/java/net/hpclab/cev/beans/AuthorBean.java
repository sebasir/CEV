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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import net.hpclab.cev.entities.Author;
import net.hpclab.cev.entities.Specimen;
import net.hpclab.cev.entities.Users;
import net.hpclab.cev.enums.ModulesEnum;
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.model.AuthorTypesModel;
import net.hpclab.cev.services.AccessService;
import net.hpclab.cev.services.Constant;
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.DataWarehouse;
import net.hpclab.cev.services.ObjectRetriever;
import net.hpclab.cev.services.ParseExceptionService;

/**
 * Este servicio permite la interacción con el servicio de
 * <tt>DataBaseService</tt> para la gestión de autores que componen a un
 * espécimen. Principalmente expone métodos de creación, edición, consulta y
 * eliminación, validando la posibilidad de estas operaciones contra el servicio
 * de <tt>AccessesService</tt>, el cual valida el usuario que realiza la
 * operación.
 * 
 * @author Sebasir
 * @since 1.0
 * @see DataBaseService
 * @see Author
 * @see AuthorTypesModel
 * @see Specimen
 * @see Users
 */

@ManagedBean
@ViewScoped
public class AuthorBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 4430802162921501326L;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(AuthorBean.class.getSimpleName());

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>Author</tt>, lo cual permite extender todas las operaciones del servicio
	 * para esta clase.
	 */
	private DataBaseService<Author> authorService;

	/**
	 * Mapa que permite definir los roles que tiene un autor
	 */
	private HashMap<Integer, AuthorTypesModel> authorTypes;

	/**
	 * Mapa que permite encontrar a un objeto de autor por su llave primaria
	 */
	private HashMap<Integer, Author> authorMap;

	/**
	 * Lista de especímenes que el autor representa según su calidad
	 */
	private List<Specimen> authorSpecimens;

	/**
	 * Lista de usuarios del sistema
	 */
	private List<Users> systemUsers;

	/**
	 * Lista de autores que tienen la calidad de determinadores
	 */
	private ArrayList<Author> determiners;

	/**
	 * Lista de autores que tienen la calidad de colectores
	 */
	private ArrayList<Author> collectors;

	/**
	 * Lista de autores que tienen la calidad de autores de epíteto específico
	 */
	private ArrayList<Author> specificAuthors;

	/**
	 * Objeto que permite editar la información de un autor
	 */
	private Author author;

	/**
	 * Numero de la clave primaria para un determinador
	 */
	private Integer determiner;

	/**
	 * Numero de la clave primaria para un colector
	 */
	private Integer collector;

	/**
	 * Numero de la clave primaria para un autor de epíteto específico
	 */
	private Integer specificEpiteth;

	/**
	 * Clave primaria de un usuario
	 */
	private Integer users;

	/**
	 * Constructor que permite inicializar los servicios de
	 * <tt>DataBaseService</tt>.
	 */
	public AuthorBean() {
		try {
			authorService = new DataBaseService<>(Author.class);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Permite realizar una búsqueda de todos los usuarios, y luego inicializar el
	 * modelo de autores y sus roles
	 */
	@PostConstruct
	public void init() {
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			if (AccessService.getInstance().checkUserAccess(ModulesEnum.AUTHOR, getUsers(facesContext),
					Constant.AccessLevel.SELECT)) {
				systemUsers = new ArrayList<>(DataWarehouse.getInstance().allUsers);
				restartAuthorTypes();
			} else
				showAccessMessage(facesContext, OutcomeEnum.SELECT_NOT_GRANTED);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error inicializando", e);
		}
	}

	/**
	 * Permite inicializar el mapa que permite definir los roles que tiene un autor
	 */
	public void restartAuthorTypes() {
		try {
			users = null;
			author = null;
			collector = null;
			determiner = null;
			specificEpiteth = null;
			authorTypes = new HashMap<>();
			authorMap = new HashMap<>();
			determiners = new ArrayList<>();
			collectors = new ArrayList<>();
			specificAuthors = new ArrayList<>();

			for (Author a : DataWarehouse.getInstance().allAuthors) {
				if (a.getAuthorDet() == 1)
					determiners.add(a);

				if (a.getAuthorCol() == 1)
					collectors.add(a);

				if (a.getAuthorAut() == 1)
					specificAuthors.add(a);
				authorMap.put(a.getIdAuthor(), a);
				authorTypes.put(a.getIdAuthor(), new AuthorTypesModel(a.getIdAuthor(), a.getAuthorDet() != 0,
						a.getAuthorAut() != 0, a.getAuthorCol() != 0));
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Permite inicializar los componentes que definen a un autor
	 */
	public void prepareCreate() {
		author = new Author(-1);
		authorTypes.put(-1, new AuthorTypesModel(-1, false, false, false));
	}

	/**
	 * Permite guardar un autor que se haya definido en la interfáz validando el
	 * permiso de escritura
	 */
	public void persist() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.CREATE_ERROR;
		String transactionMessage = author.getAuthorName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.AUTHOR, getUsers(facesContext),
					Constant.AccessLevel.INSERT)) {
				showAccessMessage(facesContext, OutcomeEnum.INSERT_NOT_GRANTED);
				return;
			}

			author.setIdAuthor(null);
			AuthorTypesModel model = authorTypes.get(-1);
			if (users != null)
				author.setIdUser(new Users(users));
			else
				author.setIdUser(null);
			author.setAuthorAut(model.getAuthorAut());
			author.setAuthorCol(model.getAuthorCol());
			author.setAuthorDet(model.getAuthorDet());
			author.setStatus(StatusEnum.ACTIVO.get());
			author = authorService.persist(author);
			DataWarehouse.getInstance().allAuthors.add(author);
			restartAuthorTypes();
			outcomeEnum = OutcomeEnum.CREATE_SUCCESS;
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error persisting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite editar un autor que se haya redefinido en la interfáz validando el
	 * permiso de modificación
	 */
	public void edit() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.UPDATE_ERROR;
		String transactionMessage = author.getAuthorName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.AUTHOR, getUsers(facesContext),
					Constant.AccessLevel.UPDATE)) {
				showAccessMessage(facesContext, OutcomeEnum.UPDATE_NOT_GRANTED);
				return;
			}

			if (users != null)
				author.setIdUser(new Users(users));
			else
				author.setIdUser(null);
			AuthorTypesModel model = authorTypes.get(author.getIdAuthor());
			author.setAuthorAut(model.getAuthorAut());
			author.setAuthorCol(model.getAuthorCol());
			author.setAuthorDet(model.getAuthorDet());
			Author tempAuthor = authorService.merge(author);
			DataWarehouse.getInstance().allAuthors.remove(author);
			DataWarehouse.getInstance().allAuthors.add(tempAuthor);
			restartAuthorTypes();
			outcomeEnum = OutcomeEnum.UPDATE_SUCCESS;
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error updating: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite eliminar un autor validando el permiso de eliminación
	 */
	public void delete() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.DELETE_ERROR;
		String transactionMessage = author.getAuthorName();
		try {
			if (!AccessService.getInstance().checkUserAccess(ModulesEnum.AUTHOR, getUsers(facesContext),
					Constant.AccessLevel.DELETE)) {
				showAccessMessage(facesContext, OutcomeEnum.DELETE_NOT_GRANTED);
				return;
			}

			authorService.delete(author);
			DataWarehouse.getInstance().allAuthors.remove(author);
			restartAuthorTypes();
			outcomeEnum = OutcomeEnum.DELETE_SUCCESS;
		} catch (Exception e) {
			transactionMessage = ParseExceptionService.getInstance().parse(e);
			LOGGER.log(Level.SEVERE, "Error deleting: " + transactionMessage);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	/**
	 * Permite cargar la lista de especímenes que un autor tiene
	 * 
	 * @param author
	 *            Objeto del autor para cargar los especímenes
	 */
	public void specimenDetail(Author author) {
		authorSpecimens = new ArrayList<Specimen>();
		if (author != null)
			for (Specimen s : DataWarehouse.getInstance().allSpecimens) {
				if (authorSpecimens.size() > Constant.MAX_SPECIMEN_LIST)
					return;
				if (s.getIdCollector().equals(author) || s.getIdDeterminer().equals(author))
					authorSpecimens.add(s);
			}
	}

	/**
	 * Permite mostrar un mensaje de seleccion cuando se escoge un autor de alguna
	 * lista
	 * 
	 * @param idAuthor
	 * @param origin
	 */
	public void onAuthorAssign(Integer idAuthor, String origin) {
		if (idAuthor != null)
			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_INFO,
					origin + ": " + authorMap.get(idAuthor).getAuthorName());
	}

	/**
	 * @return Permite el acceso a todos los autores del sistema
	 */
	public List<Author> getAllAuthors() {
		return DataWarehouse.getInstance().allAuthors;
	}

	/**
	 * @return Lista de autores que tienen la calidad de determinadores
	 */
	public List<Author> getDeterminers() {
		return determiners;
	}

	/**
	 * @return Lista de autores que tienen la calidad de colectores
	 */
	public List<Author> getCollectors() {
		return collectors;
	}

	/**
	 * @return Lista de autores que tienen la calidad de autores de epíteto
	 *         específico
	 */
	public List<Author> getSpecificAuthors() {
		return specificAuthors;
	}

	/**
	 * @return Mapa que permite definir los roles que tiene un autor
	 */
	public HashMap<Integer, AuthorTypesModel> getAuthorTypes() {
		return authorTypes;
	}

	/**
	 * @param authorTypes
	 *            Mapa que permite definir los roles que tiene un autor a definir
	 */
	public void setAuthorTypes(HashMap<Integer, AuthorTypesModel> authorTypes) {
		this.authorTypes = authorTypes;
	}

	/**
	 * @return Lista de especímenes que el autor representa según su calidad
	 */
	public List<Specimen> getAuthorSpecimens() {
		return authorSpecimens;
	}

	/**
	 * @return Lista de usuarios del sistema
	 */
	public List<Users> getSystemUsers() {
		return systemUsers;
	}

	/**
	 * @return Permite obtener el objeto que permite definir los roles que tiene un
	 *         autor
	 */
	public AuthorTypesModel getSelectedAuthorTypes() {
		return author != null ? authorTypes.get(author.getIdAuthor()) : new AuthorTypesModel(0, false, false, false);
	}

	/**
	 * @return Objeto que permite editar la información de un autor
	 */
	public Author getAuthor() {
		users = author == null || author.getIdUser() == null ? null : author.getIdUser().getIdUser();
		return author;
	}

	/**
	 * @param author
	 *            Objeto que permite editar la información de un autor a definir
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}

	/**
	 * @return Clave primaria de un usuario
	 */
	public Integer getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            Clave primaria de un usuario a definir
	 */
	public void setUsers(Integer users) {
		this.users = users;
	}

	/**
	 * @return Numero de la clave primaria para un determinador
	 */
	public Integer getDeterminer() {
		return determiner;
	}

	/**
	 * @param determiner
	 *            Numero de la clave primaria para un determinador a definir
	 */
	public void setDeterminer(Integer determiner) {
		this.determiner = determiner;
	}

	/**
	 * @return Permite obtener el objeto de un colector desde su clave primaria
	 */
	public Author getAuthorCollector() {
		if (collector != null)
			return ObjectRetriever.getObjectFromId(Author.class, collector);
		return null;
	}

	/**
	 * @return Permite obtener el objeto de un determinador desde su clave primaria
	 */
	public Author getAuthorDeterminer() {
		if (determiner != null)
			return ObjectRetriever.getObjectFromId(Author.class, determiner);
		return null;
	}

	/**
	 * @return Permite obtener el objeto de un autor de epíteto específico desde su
	 *         clave primaria
	 */
	public Author getAuthorSpecificEpithet() {
		if (specificEpiteth != null)
			return ObjectRetriever.getObjectFromId(Author.class, specificEpiteth);
		return null;
	}

	/**
	 * @return Numero de la clave primaria para un colector
	 */
	public Integer getCollector() {
		return collector;
	}

	/**
	 * @param collector
	 *            Numero de la clave primaria para un colector a definir
	 */
	public void setCollector(Integer collector) {
		this.collector = collector;
	}

	/**
	 * @return Numero de la clave primaria para un autor de epíteto específico
	 */
	public Integer getSpecificEpiteth() {
		return specificEpiteth;
	}

	/**
	 * @param specificEpiteth
	 *            Numero de la clave primaria para un autor de epíteto específico a
	 *            definir
	 */
	public void setSpecificEpiteth(Integer specificEpiteth) {
		this.specificEpiteth = specificEpiteth;
	}
}
