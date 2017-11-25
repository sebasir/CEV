package net.hpclab.cev.model;

public class AuthorTypesModel {
	private Integer idAuthor;
	private boolean determiner;
	private boolean authorEpithet;
	private boolean collector;

	public AuthorTypesModel(Integer idAuthor, boolean determiner, boolean authorEpithet, boolean collector) {
		super();
		this.idAuthor = idAuthor;
		this.determiner = determiner;
		this.authorEpithet = authorEpithet;
		this.collector = collector;
	}

	public Integer getIdAuthor() {
		return idAuthor;
	}

	public boolean isDeterminer() {
		return determiner;
	}

	public boolean isAuthorEpithet() {
		return authorEpithet;
	}

	public boolean isCollector() {
		return collector;
	}

	public void setIdAuthor(Integer idAuthor) {
		this.idAuthor = idAuthor;
	}

	public void setDeterminer(boolean determiner) {
		this.determiner = determiner;
	}

	public void setAuthorEpithet(boolean authorEpithet) {
		this.authorEpithet = authorEpithet;
	}

	public void setCollector(boolean collector) {
		this.collector = collector;
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
