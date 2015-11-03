package net.hpclab.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "author_role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AuthorRole.findAll", query = "SELECT a FROM AuthorRole a"),
    @NamedQuery(name = "AuthorRole.findByIdAuro", query = "SELECT a FROM AuthorRole a WHERE a.idAuro = :idAuro"),
    @NamedQuery(name = "AuthorRole.findCollectors", query = "SELECT a FROM AuthorRole a WHERE a.idAuty.idAuty = 3"),
    @NamedQuery(name = "AuthorRole.findEpithets", query = "SELECT a FROM AuthorRole a WHERE a.idAuty.idAuty = 2"),
    @NamedQuery(name = "AuthorRole.findDeterminers", query = "SELECT a FROM AuthorRole a WHERE a.idAuty.idAuty = 1")
})
public class AuthorRole implements entNaming, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_auro")
    private Integer idAuro;
    @OneToMany(mappedBy = "idCollector")
    private List<Specimen> specimenList;
    @OneToMany(mappedBy = "idDeterminer")
    private List<Specimen> specimenList1;
    @JoinColumn(name = "id_auty", referencedColumnName = "id_auty")
    @ManyToOne
    private AuthorType idAuty;
    @JoinColumn(name = "id_author", referencedColumnName = "id_author")
    @ManyToOne
    private Author idAuthor;

    public AuthorRole() {
    }

    public AuthorRole(Integer idAuro) {
	this.idAuro = idAuro;
    }

    public Integer getIdAuro() {
	return idAuro;
    }

    public void setIdAuro(Integer idAuro) {
	this.idAuro = idAuro;
    }

    @XmlTransient
    public List<Specimen> getSpecimenList() {
	return specimenList;
    }

    public void setSpecimenList(List<Specimen> specimenList) {
	this.specimenList = specimenList;
    }

    @XmlTransient
    public List<Specimen> getSpecimenList1() {
	return specimenList1;
    }

    public void setSpecimenList1(List<Specimen> specimenList1) {
	this.specimenList1 = specimenList1;
    }

    public AuthorType getIdAuty() {
	return idAuty;
    }

    public void setIdAuty(AuthorType idAuty) {
	this.idAuty = idAuty;
    }

    public Author getIdAuthor() {
	return idAuthor;
    }

    public void setIdAuthor(Author idAuthor) {
	this.idAuthor = idAuthor;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (idAuro != null ? idAuro.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	if (!(object instanceof AuthorRole))
	    return false;
	AuthorRole other = (AuthorRole) object;
	return (this.idAuro != null || other.idAuro == null) && (this.idAuro == null || this.idAuro.equals(other.idAuro));
    }

    @Override
    public String toString() {
	return "net.hpclab.entities.AuthorRole[ idAuro=" + idAuro + " ]";
    }

    @Override
    public String getEntityName() {
	return "Rol de Autor";
    }

    @Override
    public String getDescription() {
	return getIdAuthor().getAuthorName() + " como " + getIdAuty().getAutyName();
    }
}
