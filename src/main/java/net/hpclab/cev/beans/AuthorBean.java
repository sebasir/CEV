package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import net.hpclab.cev.entities.Author;
import net.hpclab.cev.entities.Specimen;
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
	private Author author;

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
			authorTypes = new HashMap<>();
			for (Author a : DataWarehouse.getInstance().allAuthors) {
				authorTypes.put(a.getIdAuthor(), new AuthorTypesModel(a.getIdAuthor(), a.getAuthorDet() != 0,
						a.getAuthorAut() != 0, a.getAuthorCol() != 0));
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	public void persist() {
		try {
			authorService.persist(new Author());
		} catch (Exception e) {

		}
	}

	public void edit() {

	}

	public void delete() {

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

	public void setAuthorSpecimens(List<Specimen> authorSpecimens) {
		this.authorSpecimens = authorSpecimens;
	}

	public AuthorTypesModel getSelectedAuthorTypes() {
		return author != null ? authorTypes.get(author.getIdAuthor()) : new AuthorTypesModel(0, false, false, false);
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}
}
