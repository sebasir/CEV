package net.hpclab.cev.entities;

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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "specimen")
@NamedQueries({ @NamedQuery(name = "Specimen.findAll", query = "SELECT s FROM Specimen s") })
public class Specimen implements Serializable {

	private static final long serialVersionUID = -5795215857648772533L;
	@Id
	@SequenceGenerator(name = "SpecimenSeq", sequenceName = "specimen_id_specimen_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SpecimenSeq")
	@Basic(optional = false)
	@Column(name = "id_specimen")
	private Integer idSpecimen;
	@Size(max = 64)
	@Column(name = "specific_epithet")
	private String specificEpithet;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "common_name")
	private String commonName;
	@Size(max = 2048)
	@Column(name = "iden_comment")
	private String idenComment;
	@Basic(optional = false)
	@NotNull
	@Column(name = "iden_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date idenDate;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "id_bioreg")
	private String idBioreg;
	@Column(name = "collect_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date collectDate;
	@Size(max = 2048)
	@Column(name = "collect_comment")
	private String collectComment;

	@Size(max = 32)
	@Column(name = "status")
	private String status;

	@OneToOne(mappedBy = "idSpecimen")
	private SpecimenContent specimenContent;
	@JoinColumn(name = "id_epithet_author", referencedColumnName = "id_author")
	@ManyToOne
	private Author idEpithetAuthor;
	@JoinColumn(name = "id_collector", referencedColumnName = "id_author")
	@ManyToOne
	private Author idCollector;
	@JoinColumn(name = "id_determiner", referencedColumnName = "id_author")
	@ManyToOne
	private Author idDeterminer;
	@JoinColumn(name = "id_catalog", referencedColumnName = "id_catalog")
	@ManyToOne
	private Catalog idCatalog;
	@JoinColumn(name = "id_location", referencedColumnName = "id_location")
	@ManyToOne
	private Location idLocation;
	@JoinColumn(name = "id_rety", referencedColumnName = "id_rety")
	@ManyToOne
	private RegType idRety;
	@JoinColumn(name = "id_saty", referencedColumnName = "id_saty")
	@ManyToOne
	private SampleType idSaty;
	@JoinColumn(name = "id_taxonomy", referencedColumnName = "id_taxonomy")
	@OneToOne
	private Taxonomy idTaxonomy;
	@JoinColumn(name = "id_user", referencedColumnName = "id_user")
	@ManyToOne
	private Users idUser;

	public Specimen() {
	}

	public Specimen(Integer idSpecimen) {
		this.idSpecimen = idSpecimen;
	}

	public Specimen(Integer idSpecimen, String commonName, Date idenDate, String idBioreg) {
		this.idSpecimen = idSpecimen;
		this.commonName = commonName;
		this.idenDate = idenDate;
		this.idBioreg = idBioreg;
	}

	public Integer getIdSpecimen() {
		return idSpecimen;
	}

	public void setIdSpecimen(Integer idSpecimen) {
		this.idSpecimen = idSpecimen;
	}

	public String getSpecificEpithet() {
		return specificEpithet;
	}

	public void setSpecificEpithet(String specificEpithet) {
		this.specificEpithet = specificEpithet;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getIdenComment() {
		return idenComment;
	}

	public void setIdenComment(String idenComment) {
		this.idenComment = idenComment;
	}

	public Date getIdenDate() {
		return idenDate;
	}

	public void setIdenDate(Date idenDate) {
		this.idenDate = idenDate;
	}

	public String getIdBioreg() {
		return idBioreg;
	}

	public void setIdBioreg(String idBioreg) {
		this.idBioreg = idBioreg;
	}

	public Date getCollectDate() {
		return collectDate;
	}

	public void setCollectDate(Date collectDate) {
		this.collectDate = collectDate;
	}

	public String getCollectComment() {
		return collectComment;
	}

	public void setCollectComment(String collectComment) {
		this.collectComment = collectComment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public SpecimenContent getSpecimenContent() {
		return specimenContent;
	}

	public void setSpecimenContent(SpecimenContent specimenContent) {
		this.specimenContent = specimenContent;
	}

	public Author getIdEpithetAuthor() {
		return idEpithetAuthor;
	}

	public void setIdEpithetAuthor(Author idEpithetAuthor) {
		this.idEpithetAuthor = idEpithetAuthor;
	}

	public Author getIdCollector() {
		return idCollector;
	}

	public void setIdCollector(Author idCollector) {
		this.idCollector = idCollector;
	}

	public Author getIdDeterminer() {
		return idDeterminer;
	}

	public void setIdDeterminer(Author idDeterminer) {
		this.idDeterminer = idDeterminer;
	}

	public Catalog getIdCatalog() {
		return idCatalog;
	}

	public void setIdCatalog(Catalog idCatalog) {
		this.idCatalog = idCatalog;
	}

	public Location getIdLocation() {
		return idLocation;
	}

	public void setIdLocation(Location idLocation) {
		this.idLocation = idLocation;
	}

	public RegType getIdRety() {
		return idRety;
	}

	public void setIdRety(RegType idRety) {
		this.idRety = idRety;
	}

	public SampleType getIdSaty() {
		return idSaty;
	}

	public void setIdSaty(SampleType idSaty) {
		this.idSaty = idSaty;
	}

	public Taxonomy getIdTaxonomy() {
		return idTaxonomy;
	}

	public void setIdTaxonomy(Taxonomy idTaxonomy) {
		this.idTaxonomy = idTaxonomy;
	}

	public Users getIdUser() {
		return idUser;
	}

	public void setIdUser(Users idUser) {
		this.idUser = idUser;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idSpecimen != null ? idSpecimen.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Specimen)) {
			return false;
		}
		Specimen other = (Specimen) object;
		return !((this.idSpecimen == null && other.idSpecimen != null)
				|| (this.idSpecimen != null && !this.idSpecimen.equals(other.idSpecimen)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.Specimen[ idSpecimen=" + idSpecimen + " ]";
	}
}
