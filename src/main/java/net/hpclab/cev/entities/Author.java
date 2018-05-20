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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
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
 * anteriormente para la tabla <tt>AUTHOR</tt> de la base de datos conectada.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Entity
 *
 */
@Entity
@Table(name = "author")
@NamedQueries({ @NamedQuery(name = "Author.findAll", query = "SELECT a FROM Author a") })
public class Author implements Serializable {

	private static final long serialVersionUID = -176757548805059415L;

	/**
	 * Clave primaria de la tabla, referenciando a un generador autosecuencial
	 */
	@Id
	@SequenceGenerator(name = "AuthorSeq", sequenceName = "author_id_author_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AuthorSeq")
	@Basic(optional = false)
	@Column(name = "id_author")
	private Integer idAuthor;

	/**
	 * Nombre del autor
	 */
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "author_name")
	private String authorName;

	/**
	 * Indica si el autor es o no un determinador, <tt>1</tt> si lo es, <tt>0</tt>
	 * si no
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "author_det")
	private int authorDet;

	/**
	 * Indica si el autor es o no un autor de epíteto específico, <tt>1</tt> si lo
	 * es, <tt>0</tt> si no
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "author_aut")
	private int authorAut;

	/**
	 * Indica si el autor es o no un colector, <tt>1</tt> si lo es, <tt>0</tt> si no
	 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "author_col")
	private int authorCol;

	/**
	 * Estado del registro en la base de datos, referenciando a la enumeración
	 * <tt>StatusEnum</tt>
	 */
	@Size(max = 32)
	@Column(name = "status")
	private String status;

	/**
	 * Referencia al usuario que el autor representa
	 */
	@JoinColumn(name = "id_user", referencedColumnName = "id_user")
	@ManyToOne
	private Users idUser;

	/**
	 * Referencia a los ejemplares del autor como colector
	 */
	@OneToMany(mappedBy = "idCollector")
	private List<Specimen> specimenList;

	/**
	 * Referencia a los ejemplares del autor como determinador
	 */
	@OneToMany(mappedBy = "idDeterminer")
	private List<Specimen> specimenList1;

	/**
	 * Constructor original
	 */
	public Author() {
	}

	/**
	 * Constructor con referencia a la llave primaria
	 * 
	 * @param idAuthor
	 *            Llave primaria que identifica al registro
	 */
	public Author(Integer idAuthor) {
		this.idAuthor = idAuthor;
	}

	/**
	 * Constructor con las propiedades del registro
	 * 
	 * @param idAuthor
	 *            Llave primaria que identifica al registro
	 * @param authorName
	 *            Nombre del autor
	 * @param authorDet
	 *            Indicador si es determinador, <tt>1</tt> si lo es, <tt>0</tt> si
	 *            no
	 * @param authorAut
	 *            Indicador si es autor de epíteto específico, <tt>1</tt> si lo es,
	 *            <tt>0</tt> si no
	 * @param authorCol
	 *            Indicador si es colector, <tt>1</tt> si lo es, <tt>0</tt> si no
	 */
	public Author(Integer idAuthor, String authorName, int authorDet, int authorAut, int authorCol) {
		this.idAuthor = idAuthor;
		this.authorName = authorName;
		this.authorDet = authorDet;
		this.authorAut = authorAut;
		this.authorCol = authorCol;
	}

	/**
	 * @return Llave primaria que identifica al registro
	 */
	public Integer getIdAuthor() {
		return idAuthor;
	}

	/**
	 * @param idAuthor
	 *            Llave primaria que identifica al registro a definir
	 */
	public void setIdAuthor(Integer idAuthor) {
		this.idAuthor = idAuthor;
	}

	/**
	 * @return Nombre del autor
	 */
	public String getAuthorName() {
		return authorName;
	}

	/**
	 * @param authorName
	 *            Nombre del autor a definir
	 */
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	/**
	 * @return Indica si el autor es o no un determinador, <tt>1</tt> si lo es,
	 *         <tt>0</tt> si no
	 */
	public int getAuthorDet() {
		return authorDet;
	}

	/**
	 * @param authorDet
	 *            Indica si el autor es o no un determinador, <tt>1</tt> si lo es,
	 *            <tt>0</tt> si no
	 */
	public void setAuthorDet(int authorDet) {
		this.authorDet = authorDet;
	}

	/**
	 * @return Indica si el autor es o no un autor de epíteto específico, <tt>1</tt>
	 *         si lo es, <tt>0</tt> si no
	 */
	public int getAuthorAut() {
		return authorAut;
	}

	/**
	 * @param authorAut
	 *            Indica si el autor es o no un autor de epíteto específico,
	 *            <tt>1</tt> si lo es, <tt>0</tt> si no
	 */
	public void setAuthorAut(int authorAut) {
		this.authorAut = authorAut;
	}

	/**
	 * @return Indica si el autor es o no un colector, <tt>1</tt> si lo es,
	 *         <tt>0</tt> si no
	 */
	public int getAuthorCol() {
		return authorCol;
	}

	/**
	 * @param authorCol
	 *            Indica si el autor es o no un colector, <tt>1</tt> si lo es,
	 *            <tt>0</tt> si no
	 */
	public void setAuthorCol(int authorCol) {
		this.authorCol = authorCol;
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
	 *            enumeración <tt>StatusEnum</tt>
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return Referencia al usuario que el autor representa
	 */
	public Users getIdUser() {
		return idUser;
	}

	/**
	 * @param idUser
	 *            Referencia al usuario que el autor representa a definir
	 */
	public void setIdUser(Users idUser) {
		this.idUser = idUser;
	}

	/**
	 * @return Referencia a los ejemplares del autor como colector
	 */
	public List<Specimen> getSpecimenList() {
		return specimenList;
	}

	/**
	 * @param specimenList
	 *            Referencia a los ejemplares del autor como colector a definir
	 */
	public void setSpecimenList(List<Specimen> specimenList) {
		this.specimenList = specimenList;
	}

	/**
	 * @return Referencia a los ejemplares del autor como determinador
	 */
	public List<Specimen> getSpecimenList1() {
		return specimenList1;
	}

	/**
	 * @param specimenList1
	 *            Referencia a los ejemplares del autor como determinador a definir
	 */
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
		if (!(object instanceof Author)) {
			return false;
		}
		Author other = (Author) object;
		return !((this.idAuthor == null && other.idAuthor != null)
				|| (this.idAuthor != null && !this.idAuthor.equals(other.idAuthor)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.Author[ idAuthor=" + idAuthor + " ]";
	}

}
