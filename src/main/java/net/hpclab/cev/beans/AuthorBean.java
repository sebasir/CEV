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

@ManagedBean
@ViewScoped
public class AuthorBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 4430802162921501326L;
	private static final Logger LOGGER = Logger.getLogger(AuthorBean.class.getSimpleName());

	private DataBaseService<Author> authorService;
	private HashMap<Integer, AuthorTypesModel> authorTypes;
	private HashMap<Integer, Author> authorMap;
	private List<Specimen> authorSpecimens;
	private List<Users> systemUsers;
	private ArrayList<Author> determiners;
	private ArrayList<Author> collectors;
	private ArrayList<Author> specificAuthors;
	private Author author;
	private Integer determiner;
	private Integer collector;
	private Integer specificEpiteth;
	private Integer users;

	public AuthorBean() {
		try {
			authorService = new DataBaseService<>(Author.class);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

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

	public void prepareCreate() {
		author = new Author(-1);
		authorTypes.put(-1, new AuthorTypesModel(-1, false, false, false));
	}

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

	public void onAuthorAssign(Integer idAuthor, String origin) {
		if (idAuthor != null)
			showMessage(FacesContext.getCurrentInstance(), OutcomeEnum.GENERIC_INFO,
					origin + ": " + authorMap.get(idAuthor).getAuthorName());
	}

	public List<Author> getAllAuthors() {
		return DataWarehouse.getInstance().allAuthors;
	}

	public List<Author> getDeterminers() {
		return determiners;
	}

	public List<Author> getCollectors() {
		return collectors;
	}

	public List<Author> getSpecificAuthors() {
		return specificAuthors;
	}

	public HashMap<Integer, AuthorTypesModel> getAuthorTypes() {
		return authorTypes;
	}

	public void setAuthorTypes(HashMap<Integer, AuthorTypesModel> authorTypes) {
		this.authorTypes = authorTypes;
	}

	public List<Specimen> getAuthorSpecimens() {
		return authorSpecimens;
	}

	public List<Users> getSystemUsers() {
		return systemUsers;
	}

	public AuthorTypesModel getSelectedAuthorTypes() {
		return author != null ? authorTypes.get(author.getIdAuthor()) : new AuthorTypesModel(0, false, false, false);
	}

	public Author getAuthor() {
		users = author == null || author.getIdUser() == null ? null : author.getIdUser().getIdUser();
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Integer getUsers() {
		return users;
	}

	public void setUsers(Integer users) {
		this.users = users;
	}

	public Integer getDeterminer() {
		return determiner;
	}

	public void setDeterminer(Integer determiner) {
		this.determiner = determiner;
	}

	public Author getAuthorCollector() {
		if (collector != null)
			return ObjectRetriever.getObjectFromId(Author.class, collector);
		return null;
	}

	public Author getAuthorDeterminer() {
		if (determiner != null)
			return ObjectRetriever.getObjectFromId(Author.class, determiner);
		return null;
	}

	public Author getAuthorSpecificEpithet() {
		if (specificEpiteth != null)
			return ObjectRetriever.getObjectFromId(Author.class, specificEpiteth);
		return null;
	}

	public Integer getCollector() {
		return collector;
	}

	public void setCollector(Integer collector) {
		this.collector = collector;
	}

	public Integer getSpecificEpiteth() {
		return specificEpiteth;
	}

	public void setSpecificEpiteth(Integer specificEpiteth) {
		this.specificEpiteth = specificEpiteth;
	}
}
