package net.hpclab.cev.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.hpclab.cev.converters.BooleanToCharConverter;

@Entity
@Table(name = "specimen_content")
@NamedQueries({ @NamedQuery(name = "SpecimenContent.findAll", query = "SELECT s FROM SpecimenContent s") })
public class SpecimenContent implements Serializable {

	private static final long serialVersionUID = 3628121389378365021L;
	@Id
	@SequenceGenerator(name = "SpecimenContentSeq", sequenceName = "specimen_content_id_speccont_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SpecimenContentSeq")
	@Basic(optional = false)
	@Column(name = "id_speccont")
	private Integer idSpeccont;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 128)
	@Column(name = "file_name")
	private String fileName;
	@NotNull
	@Basic(optional = false, fetch = FetchType.LAZY)
	@Column(name = "file_content")
	private byte[] fileContent;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 2048)
	@Column(name = "short_description")
	private String shortDescription;

	@Basic(optional = false)
	@NotNull
	@Column(name = "publish")
	@Convert(converter = BooleanToCharConverter.class)
	private boolean publish;

	@Basic(optional = false)
	@NotNull
	@Column(name = "file_upload_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fileUploadDate;
	@Column(name = "publish_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date publishDate;

	@Size(max = 32)
	@Column(name = "status")
	private String status;

	@JoinColumn(name = "id_specimen", referencedColumnName = "id_specimen")
	@OneToOne
	private Specimen idSpecimen;

	public SpecimenContent() {
	}

	public SpecimenContent(Integer idSpeccont) {
		this.idSpeccont = idSpeccont;
	}

	public SpecimenContent(Integer idSpeccont, String fileName, byte[] fileContent, String shortDescription,
			boolean publish, Date fileUploadDate) {
		this.idSpeccont = idSpeccont;
		this.fileName = fileName;
		this.fileContent = fileContent;
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

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public boolean getPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
		return !((this.idSpeccont == null && other.idSpeccont != null)
				|| (this.idSpeccont != null && !this.idSpeccont.equals(other.idSpeccont)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.SpecimenContent[ idSpeccont=" + idSpeccont + " ]";
	}
}
