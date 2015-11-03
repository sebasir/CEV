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

/**
 *
 * @author Sebasir
 */
@Entity
@Table(name = "author_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AuthorType.findAll", query = "SELECT a FROM AuthorType a"),
    @NamedQuery(name = "AuthorType.findByIdAuty", query = "SELECT a FROM AuthorType a WHERE a.idAuty = :idAuty"),
    @NamedQuery(name = "AuthorType.findByAutyName", query = "SELECT a FROM AuthorType a WHERE a.autyName = :autyName")})
public class AuthorType implements entNaming, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_auty")
    private Integer idAuty;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "auty_name")
    private String autyName;
    @OneToMany(mappedBy = "idAuty")
    private List<AuthorRole> authorRoleList;

    public AuthorType() {
    }

    public AuthorType(Integer idAuty) {
	   this.idAuty = idAuty;
    }

    public AuthorType(Integer idAuty, String autyName) {
	   this.idAuty = idAuty;
	   this.autyName = autyName;
    }

    public Integer getIdAuty() {
	   return idAuty;
    }

    public void setIdAuty(Integer idAuty) {
	   this.idAuty = idAuty;
    }

    public String getAutyName() {
	   return autyName;
    }

    public void setAutyName(String autyName) {
	   this.autyName = autyName;
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
	   hash += (idAuty != null ? idAuty.hashCode() : 0);
	   return hash;
    }

    @Override
    public boolean equals(Object object) {
	   if (!(object instanceof AuthorType)) {
		  return false;
	   }
	   AuthorType other = (AuthorType) object;
	   return (this.idAuty != null || other.idAuty == null) && (this.idAuty == null || this.idAuty.equals(other.idAuty));
    }

    @Override
    public String toString() {
	   return "net.hpclab.entities.AuthorType[ idAuty=" + idAuty + " ]";
    }

    @Override
    public String getEntityName() {
	   return "Tipo de Autor";
    }

    @Override
    public String getDescription() {
	   return getAutyName();
    }
}
