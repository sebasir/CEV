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
 * anteriormente para la tabla <tt>SPECIMEN_CONTENT</tt> de la base de datos
 * conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "specimen_content")
@NamedQueries({ @NamedQuery(name = "SpecimenContent.findAll", query = "SELECT s FROM SpecimenContent s") })
public class SpecimenContent implements Serializable {

	private static final long serialVersionUID = 3628121389378365021L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "SpecimenContentSeq", sequenceName = "specimen_content_id_speccont_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SpecimenContentSeq")
	@Basic(optional = false)
	@Column(name = "id_speccont")
	private Integer idSpeccont;

	/**
	 * Nombre del archivo del contenido grafico
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 128)
	@Column(name = "file_name")
	private String fileName;

	/**
	 * Archivo del contenido grafico
	 */
	@NotNull
	@Basic(optional = false, fetch = FetchType.LAZY)
	@Column(name = "file_content")
	private byte[] fileContent;

	/**
	 * Descripción corta
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 2048)
	@Column(name = "short_description")
	private String shortDescription;

	/**
	 * Indicador de publicación
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "publish")
	@Convert(converter = BooleanToCharConverter.class)
	private boolean publish;

	/**
	 * Fecha de cargue del archivo
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "file_upload_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fileUploadDate;

	/**
	 * Fecha de publicación
	 */
	@Column(name = "publish_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date publishDate;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia al espécimen que relaciona este contenido gráfico
	 */
	@JoinColumn(name = "id_specimen", referencedColumnName = "id_specimen")
	@OneToOne
	private Specimen idSpecimen;

	/**
	 * Constructor original
	 */
	public SpecimenContent() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idSpeccont
	 *            Llave primaria que identifica al registro
	 */
	public SpecimenContent(Integer idSpeccont) {
		this.idSpeccont = idSpeccont;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idSpeccont
	 *            Llave primaria que identifica al registro
	 * @param fileName
	 *            Nombre del archivo del contenido grafico
	 * @param fileContent
	 *            Archivo del contenido grafico
	 * @param shortDescription
	 *            Descripción corta
	 * @param publish
	 *            Indicador de publicación
	 * @param fileUploadDate
	 *            Fecha de cargue del archivo
	 */
	public SpecimenContent(Integer idSpeccont, String fileName, byte[] fileContent, String shortDescription,
			boolean publish, Date fileUploadDate) {
		this.idSpeccont = idSpeccont;
		this.fileName = fileName;
		this.fileContent = fileContent;
		this.shortDescription = shortDescription;
		this.publish = publish;
		this.fileUploadDate = fileUploadDate;
	}

	/**
	 * return Llave primaria que identifica al registro
	 */
	public Integer getIdSpeccont() {
		return idSpeccont;
	}

	/**
	 * @param idSpeccont
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdSpeccont(Integer idSpeccont) {
		this.idSpeccont = idSpeccont;
	}

	/**
	 * @return Nombre del archivo del contenido grafico
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            Nombre del archivo del contenido grafico a definir
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return Archivo del contenido grafico
	 */
	public byte[] getFileContent() {
		return fileContent;
	}

	/**
	 * @param fileContent
	 *            Archivo del contenido grafico a definir
	 */
	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	/**
	 * @return Descripción corta
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @param shortDescription
	 *            Descripción corta a definir
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	/**
	 * @return Indicador de publicación
	 */
	public boolean getPublish() {
		return publish;
	}

	/**
	 * @param publish
	 *            Indicador de publicación a definir
	 */
	public void setPublish(boolean publish) {
		this.publish = publish;
	}

	/**
	 * @return Fecha de cargue del archivo
	 */
	public Date getFileUploadDate() {
		return fileUploadDate;
	}

	/**
	 * @param fileUploadDate
	 *            Fecha de cargue del archivo a definir
	 */
	public void setFileUploadDate(Date fileUploadDate) {
		this.fileUploadDate = fileUploadDate;
	}

	/**
	 * @return Fecha de publicación
	 */
	public Date getPublishDate() {
		return publishDate;
	}

	/**
	 * @param publishDate
	 *            Fecha de publicación a definir
	 */
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
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
	 * @return Referencia al espécimen que relaciona este contenido gráfico
	 */
	public Specimen getIdSpecimen() {
		return idSpecimen;
	}

	/**
	 * @param idSpecimen
	 *            Referencia al espécimen que relaciona este contenido gráfico a
	 *            definir
	 */
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
