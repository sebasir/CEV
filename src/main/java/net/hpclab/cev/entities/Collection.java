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

@Entity
@Table(name = "collection")
@NamedQueries({ @NamedQuery(name = "Collection.findAll", query = "SELECT c FROM Collection c") })
public class Collection implements Serializable {

	private static final long serialVersionUID = 1967542873786951060L;
	@Id
	@SequenceGenerator(name = "CollectionSeq", sequenceName = "collection_id_collection_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CollectionSeq")

	@Basic(optional = false)
	@Column(name = "id_collection")
	private Integer idCollection;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "collection_name")
	private String collectionName;

	@Size(max = 32)
	@Column(name = "status")
	private String status;

	@OneToMany(mappedBy = "idCollection")
	private List<Catalog> catalogList;
	@JoinColumn(name = "id_institution", referencedColumnName = "id_institution")
	@ManyToOne
	private Institution idInstitution;

	public Collection() {
	}

	public Collection(Integer idCollection) {
		this.idCollection = idCollection;
	}

	public Collection(Integer idCollection, String collectionName) {
		this.idCollection = idCollection;
		this.collectionName = collectionName;
	}

	public Integer getIdCollection() {
		return idCollection;
	}

	public void setIdCollection(Integer idCollection) {
		this.idCollection = idCollection;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Catalog> getCatalogList() {
		return catalogList;
	}

	public void setCatalogList(List<Catalog> catalogList) {
		this.catalogList = catalogList;
	}

	public Institution getIdInstitution() {
		return idInstitution;
	}

	public void setIdInstitution(Institution idInstitution) {
		this.idInstitution = idInstitution;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idCollection != null ? idCollection.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Collection)) {
			return false;
		}
		Collection other = (Collection) object;
		return !((this.idCollection == null && other.idCollection != null)
				|| (this.idCollection != null && !this.idCollection.equals(other.idCollection)));
	}

	@Override
	public String toString() {
		return this.collectionName;
	}
}
