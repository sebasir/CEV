package net.hpclab.beans;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.hpclab.entities.AuthorType;
import net.hpclab.sessions.AuthorTypeSession;

@ManagedBean
@SessionScoped
public class AuthorTypeBean extends Utilsbean implements Serializable {

    @Inject
    private AuthorTypeSession authorTypeSession;

    private static final long serialVersionUID = 1L;
    private AuthorType authorType;
    private List<AuthorType> allAuthorTypes;

    public AuthorTypeBean() {
	   authorTypeSession = new AuthorTypeSession();
    }

    @PostConstruct
    public void init() {
    }

    public String persist() {
	   try {
		  setAuthorType(authorTypeSession.persist(getAuthorType()));
		  if (getAuthorType() != null && getAuthorType().getIdAuty() != null)
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(authorType, Actions.createSuccess));
		  else
			 FacesContext.getCurrentInstance().addMessage(null, showMessage(authorType, Actions.createError));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(authorType, Actions.createError));
	   }

	   return findAllAuthorTypes();
    }

    public void delete() {
	   try {
		  authorTypeSession.delete(getAuthorType());
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(authorType, Actions.deleteSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(authorType, Actions.deleteError));
	   }
    }

    public void prepareCreate() {
	   setAuthorType(new AuthorType());
    }

    public void edit() {
	   try {
		  setAuthorType(authorTypeSession.merge(getAuthorType()));
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(authorType, Actions.updateSuccess));
	   } catch (Exception e) {
		  FacesContext.getCurrentInstance().addMessage(null, showMessage(authorType, Actions.updateError));
	   }
    }

    public String displayList() {
	   findAllAuthorTypes();
	   return "specimen";
    }

    public String findAllAuthorTypes() {
	   setAllAuthorTypes(authorTypeSession.listAll());
	   return null;
    }

    public AuthorType getAuthorType() {
	   return authorType;
    }

    public void setAuthorType(AuthorType authorType) {
	   this.authorType = authorType;
    }

    public List<AuthorType> getAllAuthorTypes() {
	   return allAuthorTypes;
    }

    public void setAllAuthorTypes(List<AuthorType> allAuthorTypes) {
	   this.allAuthorTypes = allAuthorTypes;
    }
}
