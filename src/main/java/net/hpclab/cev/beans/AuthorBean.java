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
import net.hpclab.cev.enums.OutcomeEnum;
import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.model.AuthorTypesModel;
import net.hpclab.cev.services.DataBaseService;
import net.hpclab.cev.services.DataWarehouse;

@ManagedBean
@ViewScoped
public class AuthorBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 4430802162921501326L;
	private static final Logger LOGGER = Logger.getLogger(AuthorBean.class.getSimpleName());
	private DataBaseService<Author> authorService;
	private HashMap<Integer, AuthorTypesModel> authorTypes;
	private List<Specimen> authorSpecimens;
	private List<Users> systemUsers;
	private Author author;
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
		systemUsers = new ArrayList<>(DataWarehouse.getInstance().allUsers);
		restartAuthorTypes();
	}

	public void restartAuthorTypes() {
		try {
			users = null;
			authorTypes = new HashMap<>();
			for (Author a : DataWarehouse.getInstance().allAuthors) {
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
			author.setIdAuthor(null);
			AuthorTypesModel model = authorTypes.get(-1);
			if (users != null)
				author.setIdUser(new Users(users));
			else
				author.setIdUser(null);
			author.setAuthorAut(model.getAuthorAut());
			author.setAuthorCol(model.getAuthorCol());
			author.setAuthorDet(model.getAuthorDet());
			author.setStatus(StatusEnum.Activo.get());
			author = authorService.persist(author);
			DataWarehouse.getInstance().allAuthors.add(author);
			restartAuthorTypes();
			outcomeEnum = OutcomeEnum.CREATE_SUCCESS;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error persisting", e);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public void edit() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.UPDATE_ERROR;
		String transactionMessage = author.getAuthorName();
		try {
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
			LOGGER.log(Level.SEVERE, "Error updating", e);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public void delete() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		OutcomeEnum outcomeEnum = OutcomeEnum.DELETE_ERROR;
		String transactionMessage = author.getAuthorName();
		try {
			authorService.delete(author);
			DataWarehouse.getInstance().allAuthors.remove(author);
			restartAuthorTypes();
			outcomeEnum = OutcomeEnum.DELETE_SUCCESS;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error deleting", e);
		}
		showMessage(facesContext, outcomeEnum, transactionMessage);
	}

	public List<Author> getAllAuthors() {
		return DataWarehouse.getInstance().allAuthors;
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
}
