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

package net.hpclab.cev.model;

/**
 * Este modelo permite tener un abstracción de los tipos de autor para un autor
 * específico. Determina en si puede ser determinador, colector, o autor de
 * epíteto específico.
 * 
 * @author Sebasir
 * @since 1.0
 */
public class AuthorTypesModel {

	/**
	 * Identificador único del autor, representado por un numero
	 */
	private Integer idAuthor;

	/**
	 * Determina si el autor tiene calidad de determinador
	 */
	private boolean determiner;

	/**
	 * Determina si el autor tiene calidad de autor de epíteto específico
	 */
	private boolean authorEpithet;

	/**
	 * Determina si el autor tiene calidad de colector
	 */
	private boolean collector;

	/**
	 * Constructor que permite definir una instancia de este objeto con las
	 * propiedades básicas que lo definen
	 * 
	 * @param idAuthor
	 *            Identificador único del autor, representado por un numero
	 * @param determiner
	 *            Determina si el autor tiene calidad de determinador
	 * @param authorEpithet
	 *            Determina si el autor tiene calidad de autor de epíteto específico
	 * @param collector
	 *            Determina si el autor tiene calidad de colector
	 */
	public AuthorTypesModel(Integer idAuthor, boolean determiner, boolean authorEpithet, boolean collector) {
		super();
		this.idAuthor = idAuthor;
		this.determiner = determiner;
		this.authorEpithet = authorEpithet;
		this.collector = collector;
	}

	/**
	 * @return Identificador único del autor, representado por un numero
	 */
	public Integer getIdAuthor() {
		return idAuthor;
	}

	/**
	 * @return Determina si el autor tiene calidad de determinador
	 */
	public boolean isDeterminer() {
		return determiner;
	}

	/**
	 * @return Determina si el autor tiene calidad de autor de epíteto específico
	 */
	public boolean isAuthorEpithet() {
		return authorEpithet;
	}

	/**
	 * @return Determina si el autor tiene calidad de colector
	 */
	public boolean isCollector() {
		return collector;
	}

	/**
	 * @param idAuthor
	 *            Identificador único del autor, representado por un numero a
	 *            modificar
	 */
	public void setIdAuthor(Integer idAuthor) {
		this.idAuthor = idAuthor;
	}

	/**
	 * @return Determina la calidad de determinador para el autor
	 */
	public void setDeterminer(boolean determiner) {
		this.determiner = determiner;
	}

	/**
	 * @return Determina la calidad de autor de epíteto específico para el autor
	 */
	public void setAuthorEpithet(boolean authorEpithet) {
		this.authorEpithet = authorEpithet;
	}

	/**
	 * @return Determina la calidad de autor de colector para el autor
	 */
	public void setCollector(boolean collector) {
		this.collector = collector;
	}

	/**
	 * @return Un número que representa la selección de epíteto específico 0 siendo
	 *         no seleccionado y 1 si
	 */
	public int getAuthorAut() {
		return isAuthorEpithet() ? 1 : 0;
	}

	/**
	 * @return Un número que representa la selección de colector 0 siendo no
	 *         seleccionado y 1 si
	 */
	public int getAuthorCol() {
		return isCollector() ? 1 : 0;
	}

	/**
	 * @return Un número que representa la selección de determinador 0 siendo no
	 *         seleccionado y 1 si
	 */
	public int getAuthorDet() {
		return isDeterminer() ? 1 : 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idAuthor == null) ? 0 : idAuthor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthorTypesModel other = (AuthorTypesModel) obj;
		if (idAuthor == null) {
			if (other.idAuthor != null)
				return false;
		} else if (!idAuthor.equals(other.idAuthor))
			return false;
		return true;
	}
}
