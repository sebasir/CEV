/*
 * Colección Entomológica Virtual
 * Universidad Central
 * High Performance Computing Laboratory
 * Grupo COMMONS.
 * 
 * Sebastián Motavita Medellín
 * 
 * 2017 - 2018
 */
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

/**
 * Entidad mapeada desde el servicio de ORM usado y que homologa la tabla que
 * referencia en la anotación <tt>Table</tt>, a través de la opción de generar
 * entidades a partir de una conexión desde un motor JPA. Esta clase es un POJO
 * (Plain Old Java Object) extendido para su uso en administradores de entidades
 * JPA, y que permiten realizar operaciones como <tt>persist</tt>,
 * <tt>merge</tt>, <tt>delete</tt>, desde un proveedor de persistencia.
 * 
 * <p>
 * Esta entidad en particular, permite homologar las operaciones citadas
 * anteriormente para la tabla <tt>SPECIMEN</tt> de la base de datos conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "specimen")
@NamedQueries({ @NamedQuery(name = "Specimen.findAll", query = "SELECT s FROM Specimen s") })
public class Specimen implements Serializable {

	private static final long serialVersionUID = -5795215857648772533L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "SpecimenSeq", sequenceName = "specimen_id_specimen_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SpecimenSeq")
	@Basic(optional = false)
	@Column(name = "id_specimen")
	private Integer idSpecimen;

	/**
	 * Epíteto específico del espécimen
	 */
	@Size(max = 64)
	@Column(name = "specific_epithet")
	private String specificEpithet;

	/**
	 * Nombre comun del espécimen
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "common_name")
	private String commonName;

	/**
	 * Comentario de identificación
	 */
	@Size(max = 2048)
	@Column(name = "iden_comment")
	private String idenComment;

	/**
	 * Fecha de identificación
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "iden_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date idenDate;

	/**
	 * ID del registro biológico
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "id_bioreg")
	private String idBioreg;

	/**
	 * Fecha de colecta
	 */
	@Column(name = "collect_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date collectDate;

	/**
	 * Comentario de colecta
	 */
	@Size(max = 2048)
	@Column(name = "collect_comment")
	private String collectComment;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia al contenido gráfico del espécimen
	 */
	@OneToOne(mappedBy = "idSpecimen")
	private SpecimenContent specimenContent;

	/**
	 * Referencia a autor del epíteto específico
	 */
	@JoinColumn(name = "id_epithet_author", referencedColumnName = "id_author")
	@ManyToOne
	private Author idEpithetAuthor;

	/**
	 * Referencia a autor de colecta
	 */
	@JoinColumn(name = "id_collector", referencedColumnName = "id_author")
	@ManyToOne
	private Author idCollector;

	/**
	 * Referencia a autor de determinación
	 */
	@JoinColumn(name = "id_determiner", referencedColumnName = "id_author")
	@ManyToOne
	private Author idDeterminer;

	/**
	 * Referencia al catálogo que contiene este espécimen
	 */
	@JoinColumn(name = "id_catalog", referencedColumnName = "id_catalog")
	@ManyToOne
	private Catalog idCatalog;

	/**
	 * Referencia a la ubicación que contiene este espécimen
	 */
	@JoinColumn(name = "id_location", referencedColumnName = "id_location")
	@ManyToOne(optional = false)
	private Location idLocation;

	/**
	 * Referencia al tipo de registro de este espécimen
	 */
	@JoinColumn(name = "id_rety", referencedColumnName = "id_rety")
	@ManyToOne(optional = false)
	private RegType idRety;

	/**
	 * Referencia al tipo de ejemplar de este espécimen
	 */
	@JoinColumn(name = "id_saty", referencedColumnName = "id_saty")
	@ManyToOne(optional = false)
	private SampleType idSaty;

	/**
	 * Referencia a la clasificación taxonómica de este especímen
	 */
	@JoinColumn(name = "id_taxonomy", referencedColumnName = "id_taxonomy")
	@OneToOne(optional = false)
	private Taxonomy idTaxonomy;

	/**
	 * Referencia al usuario que crea este espécimen
	 */
	@JoinColumn(name = "id_user", referencedColumnName = "id_user")
	@ManyToOne
	private Users idUser;

	/**
	 * Constructor original
	 */
	public Specimen() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idSpecimen
	 *            Llave primaria que identifica al registro
	 */
	public Specimen(Integer idSpecimen) {
		this.idSpecimen = idSpecimen;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idSpecimen
	 *            Llave primaria que identifica al registro
	 * @param commonName
	 *            Nombre comun del espécimen
	 * @param idenDate
	 *            Fecha de identificación
	 * @param idBioreg
	 *            ID del registro biológico
	 */
	public Specimen(Integer idSpecimen, String commonName, Date idenDate, String idBioreg) {
		this.idSpecimen = idSpecimen;
		this.commonName = commonName;
		this.idenDate = idenDate;
		this.idBioreg = idBioreg;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdSpecimen() {
		return idSpecimen;
	}

	/**
	 * @param idSpecimen
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdSpecimen(Integer idSpecimen) {
		this.idSpecimen = idSpecimen;
	}

	/**
	 * @return Epíteto específico del espécimen
	 */
	public String getSpecificEpithet() {
		return specificEpithet;
	}

	/**
	 * @param specificEpithet
	 *            Epíteto específico del espécimen a definir
	 */
	public void setSpecificEpithet(String specificEpithet) {
		this.specificEpithet = specificEpithet;
	}

	/**
	 * @return Nombre comun del espécimen
	 */
	public String getCommonName() {
		return commonName;
	}

	/**
	 * @param commonName
	 *            Nombre comun del espécimen a definir
	 */
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	/**
	 * @return Comentario de identificación
	 */
	public String getIdenComment() {
		return idenComment;
	}

	/**
	 * @param idenComment
	 *            Comentario de identificación a definir
	 */
	public void setIdenComment(String idenComment) {
		this.idenComment = idenComment;
	}

	/**
	 * @return Fecha de identificación
	 */
	public Date getIdenDate() {
		return idenDate;
	}

	/**
	 * @param idenDate
	 *            Fecha de identificación a definir
	 */
	public void setIdenDate(Date idenDate) {
		this.idenDate = idenDate;
	}

	/**
	 * @return ID del registro biológico
	 */
	public String getIdBioreg() {
		return idBioreg;
	}

	/**
	 * @param idBioreg
	 *            ID del registro biológico a definir
	 */
	public void setIdBioreg(String idBioreg) {
		this.idBioreg = idBioreg;
	}

	/**
	 * @return Fecha de colecta
	 */
	public Date getCollectDate() {
		return collectDate;
	}

	/**
	 * @param collectDate
	 *            Fecha de colecta a definir
	 */
	public void setCollectDate(Date collectDate) {
		this.collectDate = collectDate;
	}

	/**
	 * @return Comentario de colecta
	 */
	public String getCollectComment() {
		return collectComment;
	}

	/**
	 * @param collectComment
	 *            Comentario de colecta a definir
	 */
	public void setCollectComment(String collectComment) {
		this.collectComment = collectComment;
	}

	/**
	 * @return Estado del registro en la base de datos, referenciando a la
	 *         enumeración <tt>StatusEnum</tt>
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            Estado del registro en la base de datos, referenciando a la
	 *            enumeración <tt>StatusEnum</tt> a definir
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return Referencia al contenido gráfico del espécimen
	 */
	public SpecimenContent getSpecimenContent() {
		return specimenContent;
	}

	/**
	 * @param specimenContent
	 *            Referencia al contenido gráfico del espécimen a definir
	 */
	public void setSpecimenContent(SpecimenContent specimenContent) {
		this.specimenContent = specimenContent;
	}

	/**
	 * @return Referencia a autor del epíteto específico
	 */
	public Author getIdEpithetAuthor() {
		return idEpithetAuthor;
	}

	/**
	 * @param idEpithetAuthor
	 *            Referencia a autor del epíteto específico a definir
	 */
	public void setIdEpithetAuthor(Author idEpithetAuthor) {
		this.idEpithetAuthor = idEpithetAuthor;
	}

	/**
	 * @return Referencia a autor de colecta
	 */
	public Author getIdCollector() {
		return idCollector;
	}

	/**
	 * @param idCollector
	 *            Referencia a autor de colecta a definir
	 */
	public void setIdCollector(Author idCollector) {
		this.idCollector = idCollector;
	}

	/**
	 * @return Referencia a autor de determinación
	 */
	public Author getIdDeterminer() {
		return idDeterminer;
	}

	/**
	 * @param idDeterminer
	 *            Referencia a autor de determinación a definir
	 */
	public void setIdDeterminer(Author idDeterminer) {
		this.idDeterminer = idDeterminer;
	}

	/**
	 * @return Referencia al catálogo que contiene este espécimen
	 */
	public Catalog getIdCatalog() {
		return idCatalog;
	}

	/**
	 * @param idCatalog
	 *            Referencia al catálogo que contiene este espécimen a definir
	 */
	public void setIdCatalog(Catalog idCatalog) {
		this.idCatalog = idCatalog;
	}

	/**
	 * @return Referencia a la ubicación que contiene este espécimen
	 */
	public Location getIdLocation() {
		return idLocation;
	}

	/**
	 * @param idLocation
	 *            Referencia a la ubicación que contiene este espécimen a definir
	 */
	public void setIdLocation(Location idLocation) {
		this.idLocation = idLocation;
	}

	/**
	 * @return Referencia al tipo de registro de este espécimen
	 */
	public RegType getIdRety() {
		return idRety;
	}

	/**
	 * @param idRety
	 *            Referencia al tipo de registro de este espécimen a definir
	 */
	public void setIdRety(RegType idRety) {
		this.idRety = idRety;
	}

	/**
	 * @return Referencia al tipo de ejemplar de este espécimen
	 */
	public SampleType getIdSaty() {
		return idSaty;
	}

	/**
	 * @param idSaty
	 *            Referencia al tipo de ejemplar de este espécimen a definir
	 */
	public void setIdSaty(SampleType idSaty) {
		this.idSaty = idSaty;
	}

	/**
	 * @return Referencia a la clasificación taxonómica de este especímen
	 */
	public Taxonomy getIdTaxonomy() {
		return idTaxonomy;
	}

	/**
	 * @param idTaxonomy
	 *            Referencia a la clasificación taxonómica de este especímen a
	 *            definir
	 */
	public void setIdTaxonomy(Taxonomy idTaxonomy) {
		this.idTaxonomy = idTaxonomy;
	}

	/**
	 * @return Referencia al usuario que crea este espécimen
	 */
	public Users getIdUser() {
		return idUser;
	}

	/**
	 * @param idUser
	 *            Referencia al usuario que crea este espécimen a definir
	 */
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
