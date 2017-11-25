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
import net.hpclab.cev.model.AuthorTypesModel;
import net.hpclab.cev.services.DataBaseService;

@ManagedBean
@ViewScoped
public class AuthorBean extends UtilsBean implements Serializable {

	private static final long serialVersionUID = 4430802162921501326L;
	private static final Logger LOGGER = Logger.getLogger(AuthorBean.class.getSimpleName());
	private DataBaseService<Author> authorService;
	private List<Author> allAuthors;
	private HashMap<Integer, AuthorTypesModel> authorTypes;

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
			allAuthors = authorService.getList();
			authorTypes = new HashMap<>();
			for (Author a : allAuthors) {
				authorTypes.put(a.getIdAuthor(), new AuthorTypesModel(a.getIdAuthor(), a.getAuthorDet() != 0,
						a.getAuthorAut() != 0, a.getAuthorCol() != 0));
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	public List<Author> getAllAuthors() {
		return allAuthors;
	}

	public void setAllAuthors(List<Author> allAuthors) {
		this.allAuthors = allAuthors;
	}

	public HashMap<Integer, AuthorTypesModel> getAuthorTypes() {
		return authorTypes;
	}

	public void setAuthorTypes(HashMap<Integer, AuthorTypesModel> authorTypes) {
		this.authorTypes = authorTypes;
	}
}
