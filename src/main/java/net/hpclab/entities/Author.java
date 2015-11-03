package net.hpclab.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "author")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Author.findAll", query = "SELECT a FROM Author a"),
    @NamedQuery(name = "Author.findByIdAuthor", query = "SELECT a FROM Author a WHERE a.idAuthor = :idAuthor"),
    @NamedQuery(name = "Author.findByAuthorName", query = "SELECT a FROM Author a WHERE a.authorName = :authorName")
})
public class Author implements entNaming, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_author")
    private Integer idAuthor;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "author_name")
    private String authorName;
    @OneToMany(mappedBy = "idAuthor")
    private List<AuthorRole> authorRoleList;

    public Author() {
    }

    public Author(Integer idAuthor) {
	this.idAuthor = idAuthor;
    }

    public Author(Integer idAuthor, String authorName) {
	this.idAuthor = idAuthor;
	this.authorName = authorName;
    }

    public Integer getIdAuthor() {
	return idAuthor;
    }

    public void setIdAuthor(Integer idAuthor) {
	this.idAuthor = idAuthor;
    }

    public String getAuthorName() {
	return authorName;
    }

    public void setAuthorName(String authorName) {
	this.authorName = authorName;
    }

    @XmlTransient
    public List<AuthorRole> getAuthorRoleList() {
	return authorRoleList;
    }

    public void setAuthorRoleList(List<AuthorRole> authorRoleList) {
	this.authorRoleList = authorRoleList;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (idAuthor != null ? idAuthor.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	if (!(object instanceof Author))
	    return false;
	Author other = (Author) object;
	return (this.idAuthor != null || other.idAuthor == null) && (this.idAuthor == null || this.idAuthor.equals(other.idAuthor));
    }

    @Override
    public String toString() {
	return "net.hpclab.entities.Author[ idAuthor=" + idAuthor + " ]";
    }

    @Override
    public String getEntityName() {
	return "Autor";
    }

    @Override
    public String getDescription() {
	return getAuthorName();
    }
}
