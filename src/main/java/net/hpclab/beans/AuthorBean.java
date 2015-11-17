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
import net.hpclab.utilities.AuthorPivot;
import net.hpclab.sessions.AuthorRoleSession;
import net.hpclab.sessions.AuthorSession;
import net.hpclab.sessions.AuthorTypeSession;

@ManagedBean
@SessionScoped
public class AuthorBean extends Utilsbean implements Serializable {

    @Inject
    private AuthorSession authorSession;

    @Inject
    private AuthorRoleSession authorRoleSession;

    @Inject
    private AuthorTypeSession authorTypeSession;

    private static final long serialVersionUID = 1L;
    private Author author;
    private List<AuthorPivot> allAuthors;
    private HashMap<Integer, String> columns;
    private boolean checked;
    public AuthorBean() {
	   authorSession = new AuthorSession();
	   authorRoleSession = new AuthorRoleSession();
	   authorTypeSession = new AuthorTypeSession();
    }

    @PostConstruct
    public void init() {
    }

    public String persist() {
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

    public void delete() {
	   try {
		  authorSession.delete(getAuthor());
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Actions.deleteSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Actions.deleteError));
	   }
    }

    public void prepareCreate() {
	   setAuthor(new Author());
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
	   List<AuthorRole> authorRoles = authorRoleSession.listAll();
	   List<AuthorType> authorTypes = authorTypeSession.listAll();
	   if (authorTypes != null && !authorTypes.isEmpty()) {
		  columns = new HashMap<Integer, String>();
		  for (AuthorType a : authorTypes) {
			 columns.put(a.getIdAuty(), a.getAutyName());
		  }
	   }
	   if (authorRoles != null && !authorRoles.isEmpty()) {
		  HashMap<Integer, Integer> idTypes;
		  allAuthors = new ArrayList<AuthorPivot>();
		  AuthorPivot pivot;
		  int index;
		  for (AuthorRole a : authorRoles) {
			 idTypes = new HashMap<Integer, Integer>();
			 idTypes.put(a.getIdAuty().getIdAuty(), a.getIdAuro());
			 pivot = new AuthorPivot(a.getIdAuthor().getIdAuthor());
			 index = allAuthors.indexOf(pivot);
			 if (index < 0) {
				pivot.setAuthorName(a.getIdAuthor().getAuthorName());
				allAuthors.add(pivot);
				index = allAuthors.size() - 1;
			 }
			 allAuthors.get(index).getIdTypes().putAll(idTypes);
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
    
    public String getColumn(Integer index){
	   return columns.get(index);
    }

    public void setColumns(HashMap<Integer, String> columns) {
	   this.columns = columns;
    }
    
    public String editarCheck() {
	   System.out.println(":::::> ");
	   return null;
    }
    
    public boolean getChecked(){
	   return checked;
    }
    
    public void setChecked(boolean checked){
	   this.checked = checked;
    }
}
