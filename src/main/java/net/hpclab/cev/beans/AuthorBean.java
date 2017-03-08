package net.hpclab.cev.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import net.hpclab.cev.entities.Author;
import net.hpclab.cev.services.AuthorPivot;

@ManagedBean
@SessionScoped
public class AuthorBean extends UtilsBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private AuthorPivot authorPivot;
    private Author author;
    private Integer idType;
    private HashMap<Integer, String> columns;

    public AuthorBean() {
        super(FacesContext.getCurrentInstance());
        allAuths = new ArrayList<Author>();
    }

    @PostConstruct
    public void init() {
    }

    public String persistAuthor() {
        try {
            //author = authorSession.persist(author);
            if (getAuthor() != null && getAuthor().getIdAuthor() != null) {
                FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Operations.CREATE_SUCCESS));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Operations.CREATE_ERROR));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Operations.CREATE_ERROR));
        }

        return findAllAuthors();
    }

    private void persistAuro(Integer idAuthor, Integer idType) {
        AuthorRole authorRole = new AuthorRole();
        try {
            authorRole.setIdAuthor(new Author(idAuthor));
            authorRole.setIdAuty(new AuthorType(idType));
            //authorRole = authorRoleSession.persist(authorRole);
            if (authorRole != null && authorRole.getIdAuro() != null) {
                FacesContext.getCurrentInstance().addMessage(null, showOperationMessage(authorRole, Operations.CREATE_SUCCESS));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, showOperationMessage(authorRole, Operations.CREATE_ERROR));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, showOperationMessage(authorRole, Operations.CREATE_ERROR));
        }
    }

    public String persistType() {
        try {
            //setAuthorType(authorTypeSession.persist(authorType));
            if (authorType != null && authorType.getIdAuty() != null) {
                FacesContext.getCurrentInstance().addMessage(null, showOperationMessage(authorType, Operations.CREATE_SUCCESS));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, showOperationMessage(authorType, Operations.CREATE_ERROR));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showOperationMessage(authorType, Operations.CREATE_ERROR));
        }

        return findAllAuthors();
    }

    public void delete() {
        try {
            //authorSession.delete(getAuthor());
            FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Operations.DELETE_SUCCESS));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Operations.DELETE_ERROR));
        }
    }

    public void deleteType() {
        try {
            //authorTypeSession.delete(authorType);
            FacesContext.getCurrentInstance().addMessage(null, showOperationMessage(authorType, Operations.DELETE_SUCCESS));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showOperationMessage(authorType, Operations.DELETE_ERROR));
        }
    }

    private void deleteAuro(Integer idAuthor, Integer idType) {
        AuthorRole authorRole = new AuthorRole();
        try {
            for (AuthorRole a : allAuthorRoles) {
                if (a.getIdAuthor().getIdAuthor().equals(idAuthor) && a.getIdAuty().getIdAuty().equals(idType)) {
                    authorRole = a;
                    break;
                }
            }
            //authorRoleSession.delete(authorRole);
            FacesContext.getCurrentInstance().addMessage(null, showOperationMessage(authorRole, Operations.DELETE_SUCCESS));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showOperationMessage(authorRole, Operations.DELETE_ERROR));
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
            //author = authorSession.merge(author);
            FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Operations.UPDATE_SUCCESS));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showMessage(author, Operations.UPDATE_ERROR));
        }
    }

    public void editType() {
        try {
            //authorType = authorTypeSession.merge(authorType);
            FacesContext.getCurrentInstance().addMessage(null, showOperationMessage(authorType, Operations.UPDATE_SUCCESS));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, showOperationMessage(authorType, Operations.UPDATE_ERROR));
        }
    }

    public String displayList() {
        findAllAuthors();
        return "specimen";
    }

    public String findAllAuthors() {
        //allAuthorRoles = (List<AuthorRole>) authorSession.findListByQuery("AuthorRole.findAll", AuthorRole.class);
        //allAuthorTypes = (List<AuthorType>) authorSession.findListByQuery("AuthorType.findAll", AuthorType.class);
        //allAuths = (List<Author>) authorSession.listAll();
        List<Author> authors = new ArrayList<Author>(allAuths);
        if (allAuthorTypes != null && !allAuthorTypes.isEmpty()) {
            columns = new HashMap<Integer, String>();
            for (AuthorType a : allAuthorTypes) {
                columns.put(a.getIdAuty(), a.getAutyName());
            }
        }
        if (allAuthorRoles != null && !allAuthorRoles.isEmpty()) {
            HashMap<Integer, Boolean> idTypes;
            allAuthors = new ArrayList<AuthorPivot>();
            AuthorPivot pivot;
            int index;
            for (AuthorRole a : allAuthorRoles) {
                idTypes = new HashMap<Integer, Boolean>();
                idTypes.put(a.getIdAuty().getIdAuty(), true);
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

    public void prepareTransaction(Integer idAuthor) {
        for (Author a : allAuths) {
            if (a.getIdAuthor().equals(idAuthor)) {
                author = a;
                break;
            }
        }
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

    public String editarCheck(Integer idType, Integer idAuthor) {
        for (AuthorPivot a : allAuthors) {
            if (a.getIdAuthor().equals(idAuthor)) {
                if (a.getIdTypes().get(idType) != null) {
                    deleteAuro(idAuthor, idType);
                } else {
                    persistAuro(idAuthor, idType);
                }
                break;
            }
        }
        return null;
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

    public void setIdType(Integer idType) {
        for (AuthorType a : allAuthorTypes) {
            if (a.getIdAuty() == idType) {
                authorType = a;
                break;
            }
        }
    }

    public Integer getIdType() {
        return idType;
    }
}
