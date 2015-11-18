package net.hpclab.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.hpclab.entities.Author;
import net.hpclab.entities.AuthorRole;
import net.hpclab.entities.AuthorType;
import net.hpclab.sessions.AuthorRoleSession;
import net.hpclab.utilities.AuthorPivot;
import net.hpclab.sessions.AuthorSession;
import net.hpclab.sessions.AuthorTypeSession;

@ManagedBean
@SessionScoped
public class AuthorBean extends Utilsbean implements Serializable {

    @Inject
    private AuthorSession authorSession;

    @Inject
    private AuthorTypeSession authorTypeSession;
    
    @Inject
    private AuthorRoleSession authorRoleSession;
    
    private static final long serialVersionUID = 1L;
    private AuthorPivot authorPivot;
    private Author author;
    private AuthorType authorType;
    private Integer idType;
    private List<AuthorPivot> allAuthors;
    private List<AuthorType> allAuthorTypes;
    private List<AuthorRole> allAuthorRoles;
    private HashMap<Integer, String> columns;
    private boolean checked;

    public AuthorBean() {
	   authorSession = new AuthorSession();
	   authorTypeSession = new AuthorTypeSession();
	   authorRoleSession = new AuthorRoleSession();
    }

    @PostConstruct
    public void init() {
    }

    public String persistAuthor() {
	   try {
		  setAuthor(authorSession.persist(getAuthor()));
		  if (getAuthor() != null && getAuthor().getIdAuthor() != null) {
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Actions.createSuccess));
		  } else {
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Actions.createError));
		  }
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Actions.createError));
	   }

	   return findAllAuthors();
    }
    
    public String persistType() {
	   try {
		  setAuthorType(authorTypeSession.persist(authorType));
		  if (authorType != null && authorType.getIdAuty() != null) {
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(authorType, Actions.createSuccess));
		  } else {
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(authorType, Actions.createError));
		  }
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(authorType, Actions.createError));
	   }

	   return findAllAuthors();
    }

    public void delete() {
	   try {
		  authorSession.delete(getAuthor());
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Actions.deleteSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Actions.deleteError));
	   }
    }

    public void prepareCreateAuthor() {
	   author = new Author();
	   authorType = null;
    }

    public void prepareCreateType() {
	   authorType = new AuthorType();
	   author = null;
    }

    public void edit() {
	   try {
		  setAuthor(authorSession.merge(getAuthor()));
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Actions.updateSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Actions.updateError));
	   }
    }

    public String displayList() {
	   findAllAuthors();
	   return "specimen";
    }

    public String findAllAuthors() {
	   allAuthorRoles = (List<AuthorRole>) authorSession.findListByQuery("AuthorRole.findAll", AuthorRole.class);
	   allAuthorTypes = (List<AuthorType>) authorSession.findListByQuery("AuthorType.findAll", AuthorType.class);
	   List<Author> authors = (List<Author>) authorSession.findListByQuery("Author.findAll", Author.class);
	   if (allAuthorTypes != null && !allAuthorTypes.isEmpty()) {
		  columns = new HashMap<Integer, String>();
		  for (AuthorType a : allAuthorTypes) {
			 columns.put(a.getIdAuty(), a.getAutyName());
		  }
	   }
	   if (allAuthorRoles != null && !allAuthorRoles.isEmpty()) {
		  HashMap<Integer, Integer> idTypes;
		  allAuthors = new ArrayList<AuthorPivot>();
		  AuthorPivot pivot;
		  int index;
		  for (AuthorRole a : allAuthorRoles) {
			 idTypes = new HashMap<Integer, Integer>();
			 idTypes.put(a.getIdAuty().getIdAuty(), a.getIdAuro());
			 pivot = new AuthorPivot(a.getIdAuthor().getIdAuthor());
			 if (authors.contains(a.getIdAuthor())) {
				authors.remove(a.getIdAuthor());
			 }
			 index = allAuthors.indexOf(pivot);
			 if (index < 0) {
				pivot.setAuthorName(a.getIdAuthor().getAuthorName());
				allAuthors.add(pivot);
				index = allAuthors.size() - 1;
			 }
			 allAuthors.get(index).getIdTypes().putAll(idTypes);
		  }
		  for (Author a : authors) {
			 allAuthors.add(new AuthorPivot(a.getIdAuthor(), a.getAuthorName()));
		  }
	   }
	   return null;
    }

    public Author getAuthor() {
	   return author;
    }

    public void setAuthor(Author author) {
	   this.author = author;
    }

    public List<AuthorPivot> getAllAuthors() {
	   findAllAuthors();
	   return allAuthors;
    }

    public void setAllAuthors(List<AuthorPivot> allAuthors) {
	   this.allAuthors = allAuthors;
    }

    public Set<Integer> getColumns() {
	   return columns.keySet();
    }

    public String getColumn(Integer index) {
	   return columns.get(index);
    }

    public void setColumns(HashMap<Integer, String> columns) {
	   this.columns = columns;
    }

    public String editarCheck() {
	   return null;
    }

    public boolean getChecked() {
	   return checked;
    }

    public void setChecked(boolean checked) {
	   this.checked = checked;
    }

    public AuthorPivot getAuthorPivot() {
	   return authorPivot;
    }

    public void setAuthorPivot(AuthorPivot authorPivot) {
	   this.authorPivot = authorPivot;
    }

    public AuthorType getAuthorType() {
	   return authorType;
    }

    public void setAuthorType(AuthorType authorType) {
	   this.authorType = authorType;
    }
    
    public void setIdType (Integer idType) {
	   for(AuthorType a : allAuthorTypes) {
		  if(a.getIdAuty() == idType)
			 authorType = a;
	   }
    }

    public Integer getIdType() {
	   return idType;
    }
}
