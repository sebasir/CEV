/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hpclab.cev.entities;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Sebasir
 */
@Entity
@Table(name = "author")
@NamedQueries({
    @NamedQuery(name = "Author.findAll", query = "SELECT a FROM Author a")})
public class Author implements Serializable {

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
    @Basic(optional = false)
    @NotNull
    @Column(name = "author_det")
    private int authorDet;
    @Basic(optional = false)
    @NotNull
    @Column(name = "author_aut")
    private int authorAut;
    @Basic(optional = false)
    @NotNull
    @Column(name = "author_col")
    private int authorCol;
    @Size(max = 2147483647)
    @Column(name = "status")
    private String status;
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    @ManyToOne
    private Users idUser;
    @OneToMany(mappedBy = "idCollector")
    private List<Specimen> specimenList;
    @OneToMany(mappedBy = "idDeterminer")
    private List<Specimen> specimenList1;

    public Author() {
    }

    public Author(Integer idAuthor) {
        this.idAuthor = idAuthor;
    }

    public Author(Integer idAuthor, String authorName, int authorDet, int authorAut, int authorCol) {
        this.idAuthor = idAuthor;
        this.authorName = authorName;
        this.authorDet = authorDet;
        this.authorAut = authorAut;
        this.authorCol = authorCol;
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

    public int getAuthorDet() {
        return authorDet;
    }

    public void setAuthorDet(int authorDet) {
        this.authorDet = authorDet;
    }

    public int getAuthorAut() {
        return authorAut;
    }

    public void setAuthorAut(int authorAut) {
        this.authorAut = authorAut;
    }

    public int getAuthorCol() {
        return authorCol;
    }

    public void setAuthorCol(int authorCol) {
        this.authorCol = authorCol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users getIdUser() {
        return idUser;
    }

    public void setIdUser(Users idUser) {
        this.idUser = idUser;
    }

    public List<Specimen> getSpecimenList() {
        return specimenList;
    }

    public void setSpecimenList(List<Specimen> specimenList) {
        this.specimenList = specimenList;
    }

    public List<Specimen> getSpecimenList1() {
        return specimenList1;
    }

    public void setSpecimenList1(List<Specimen> specimenList1) {
        this.specimenList1 = specimenList1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAuthor != null ? idAuthor.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Author)) {
            return false;
        }
        Author other = (Author) object;
        if ((this.idAuthor == null && other.idAuthor != null) || (this.idAuthor != null && !this.idAuthor.equals(other.idAuthor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.hpclab.entities.Author[ idAuthor=" + idAuthor + " ]";
    }
    
}
