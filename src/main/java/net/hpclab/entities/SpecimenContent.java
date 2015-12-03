package net.hpclab.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "specimen_content")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SpecimenContent.findAll", query = "SELECT s FROM SpecimenContent s"),
    @NamedQuery(name = "SpecimenContent.findByIdSpeccont", query = "SELECT s FROM SpecimenContent s WHERE s.idSpeccont = :idSpeccont"),
    @NamedQuery(name = "SpecimenContent.findByFileName", query = "SELECT s FROM SpecimenContent s WHERE s.fileName = :fileName"),
    @NamedQuery(name = "SpecimenContent.findByShortDescription", query = "SELECT s FROM SpecimenContent s WHERE s.shortDescription = :shortDescription"),
    @NamedQuery(name = "SpecimenContent.findByPublish", query = "SELECT s FROM SpecimenContent s WHERE s.publish = :publish"),
    @NamedQuery(name = "SpecimenContent.findByFileUploadDate", query = "SELECT s FROM SpecimenContent s WHERE s.fileUploadDate = :fileUploadDate"),
    @NamedQuery(name = "SpecimenContent.findByPublishDate", query = "SELECT s FROM SpecimenContent s WHERE s.publishDate = :publishDate")})
public class SpecimenContent implements entNaming, Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_speccont")
    private Integer idSpeccont;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "file_name")
    private String fileName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 512)
    @Column(name = "short_description")
    private String shortDescription;
    @Basic(optional = false)
    @NotNull
    @Column(name = "publish")
    private char publish;
    @Basic(optional = false)
    @NotNull
    @Column(name = "file_upload_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fileUploadDate;
    @Column(name = "publish_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishDate;
    @JoinColumn(name = "id_specimen", referencedColumnName = "id_specimen")
    @ManyToOne
    private Specimen idSpecimen;

    public SpecimenContent() {
    }

    public SpecimenContent(Integer idSpeccont) {
	   this.idSpeccont = idSpeccont;
    }

    public SpecimenContent(Integer idSpeccont, String fileName, String shortDescription, char publish, Date fileUploadDate) {
	   this.idSpeccont = idSpeccont;
	   this.fileName = fileName;
	   this.shortDescription = shortDescription;
	   this.publish = publish;
	   this.fileUploadDate = fileUploadDate;
    }

    public Integer getIdSpeccont() {
	   return idSpeccont;
    }

    public void setIdSpeccont(Integer idSpeccont) {
	   this.idSpeccont = idSpeccont;
    }

    public String getFileName() {
	   return fileName;
    }

    public void setFileName(String fileName) {
	   this.fileName = fileName;
    }

    public String getShortDescription() {
	   return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
	   this.shortDescription = shortDescription;
    }

    public char getPublish() {
	   return publish;
    }

    public void setPublish(char publish) {
	   this.publish = publish;
    }

    public Date getFileUploadDate() {
	   return fileUploadDate;
    }

    public void setFileUploadDate(Date fileUploadDate) {
	   this.fileUploadDate = fileUploadDate;
    }

    public Date getPublishDate() {
	   return publishDate;
    }

    public void setPublishDate(Date publishDate) {
	   this.publishDate = publishDate;
    }

    public Specimen getIdSpecimen() {
	   return idSpecimen;
    }

    public void setIdSpecimen(Specimen idSpecimen) {
	   this.idSpecimen = idSpecimen;
    }

    @Override
    public int hashCode() {
	   int hash = 0;
	   hash += (idSpeccont != null ? idSpeccont.hashCode() : 0);
	   return hash;
    }

    @Override
    public boolean equals(Object object) {
	   if (!(object instanceof SpecimenContent)) {
		  return false;
	   }
	   SpecimenContent other = (SpecimenContent) object;
	   return (this.idSpeccont != null || other.idSpeccont == null) && (this.idSpeccont == null || this.idSpeccont.equals(other.idSpeccont));
    }

    @Override
    public String toString() {
	   return "net.hpclab.entities.SpecimenContent[ idSpeccont=" + idSpeccont + " ]";
    }

    @Override
    public String getEntityName() {
	   return "Contenido visual";
    }

    @Override
    public String getDescription() {
	   return "del archivo " + fileName + " para " + idSpecimen.getIdTaxonomy().getTaxonomyName() + " " + idSpecimen.getSpecificEpithet();
    }
}
