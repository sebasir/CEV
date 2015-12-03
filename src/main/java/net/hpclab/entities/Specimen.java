package net.hpclab.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "specimen")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Specimen.findAll", query = "SELECT s FROM Specimen s"),
    @NamedQuery(name = "Specimen.findByIdSpecimen", query = "SELECT s FROM Specimen s WHERE s.idSpecimen = :idSpecimen"),
    @NamedQuery(name = "Specimen.findBySpecificEpithet", query = "SELECT s FROM Specimen s WHERE s.specificEpithet = :specificEpithet"),
    @NamedQuery(name = "Specimen.findByCommonName", query = "SELECT s FROM Specimen s WHERE s.commonName = :commonName"),
    @NamedQuery(name = "Specimen.findByIdenComment", query = "SELECT s FROM Specimen s WHERE s.idenComment = :idenComment"),
    @NamedQuery(name = "Specimen.findByIdenDate", query = "SELECT s FROM Specimen s WHERE s.idenDate = :idenDate"),
    @NamedQuery(name = "Specimen.findByIdBioreg", query = "SELECT s FROM Specimen s WHERE s.idBioreg = :idBioreg"),
    @NamedQuery(name = "Specimen.findByCollectDate", query = "SELECT s FROM Specimen s WHERE s.collectDate = :collectDate"),
    @NamedQuery(name = "Specimen.findByCollectComment", query = "SELECT s FROM Specimen s WHERE s.collectComment = :collectComment")})
public class Specimen implements entNaming, Serializable {
    @OneToMany(mappedBy = "idSpecimen")
    private List<SpecimenContent> specimenContentList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @JoinColumn(name = "id_taxonomy", referencedColumnName = "id_taxonomy")
    @ManyToOne
    private Taxonomy idTaxonomy;
    @JoinColumn(name = "id_saty", referencedColumnName = "id_saty")
    @ManyToOne
    private SampleType idSaty;
    @JoinColumn(name = "id_rety", referencedColumnName = "id_rety")
    @ManyToOne
    private RegType idRety;
    @JoinColumn(name = "id_location", referencedColumnName = "id_location")
    @ManyToOne
    private Location idLocation;
    @JoinColumn(name = "id_catalog", referencedColumnName = "id_catalog")
    @ManyToOne
    private Catalog idCatalog;
    @JoinColumn(name = "id_collector", referencedColumnName = "id_auro")
    @ManyToOne
    private AuthorRole idCollector;
    @JoinColumn(name = "id_determiner", referencedColumnName = "id_auro")
    @ManyToOne
    private AuthorRole idDeterminer;

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

    public Taxonomy getIdTaxonomy() {
	   return idTaxonomy;
    }

    public void setIdTaxonomy(Taxonomy idTaxonomy) {
	   this.idTaxonomy = idTaxonomy;
    }

    public SampleType getIdSaty() {
	   return idSaty;
    }

    public void setIdSaty(SampleType idSaty) {
	   this.idSaty = idSaty;
    }

    public RegType getIdRety() {
	   return idRety;
    }

    public void setIdRety(RegType idRety) {
	   this.idRety = idRety;
    }

    public Location getIdLocation() {
	   return idLocation;
    }

    public void setIdLocation(Location idLocation) {
	   this.idLocation = idLocation;
    }

    public Catalog getIdCatalog() {
	   return idCatalog;
    }

    public void setIdCatalog(Catalog idCatalog) {
	   this.idCatalog = idCatalog;
    }

    public AuthorRole getIdCollector() {
	   return idCollector;
    }

    public void setIdCollector(AuthorRole idCollector) {
	   this.idCollector = idCollector;
    }

    public AuthorRole getIdDeterminer() {
	   return idDeterminer;
    }

    public void setIdDeterminer(AuthorRole idDeterminer) {
	   this.idDeterminer = idDeterminer;
    }
    
    
    @XmlTransient
    public List<SpecimenContent> getSpecimenContentList() {
	   return specimenContentList;
    }

    public void setSpecimenContentList(List<SpecimenContent> specimenContentList) {
	   this.specimenContentList = specimenContentList;
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
	   return (this.idSpecimen != null || other.idSpecimen == null) && (this.idSpecimen == null || this.idSpecimen.equals(other.idSpecimen));
    }

    @Override
    public String toString() {
	   return "net.hpclab.entities.Specimen[ idSpecimen=" + idSpecimen + " ]";
    }

    @Override
    public String getEntityName() {
	   return "Espécimen";
    }

    @Override
    public String getDescription() {
	   return getSpecificEpithet() + "(" + getCommonName() + ")";
    }
}
