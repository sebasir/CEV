package net.hpclab.cev.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "location_level")
@NamedQueries({ @NamedQuery(name = "LocationLevel.findAll", query = "SELECT l FROM LocationLevel l") })
public class LocationLevel implements Serializable {

	private static final long serialVersionUID = 2691092896015284215L;
	@Id
	@SequenceGenerator(name = "LocationLevelSeq", sequenceName = "location_level_id_loclevel_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LocationLevelSeq")
	@Basic(optional = false)
	@Column(name = "id_loclevel")
	private Integer idLoclevel;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 32)
	@Column(name = "loclevel_name")
	private String loclevelName;
	@Basic(optional = false)
	@NotNull
	@Column(name = "loclevel_rank")
	private int loclevelRank;

	@Size(max = 32)
	@Column(name = "status")
	private String status;

	@OneToMany(mappedBy = "idLoclevel")
	private List<Location> locationList;

	public LocationLevel() {
	}

	public LocationLevel(Integer idLoclevel) {
		this.idLoclevel = idLoclevel;
	}

	public LocationLevel(Integer idLoclevel, String loclevelName, int loclevelRank) {
		this.idLoclevel = idLoclevel;
		this.loclevelName = loclevelName;
		this.loclevelRank = loclevelRank;
	}

	public Integer getIdLoclevel() {
		return idLoclevel;
	}

	public void setIdLoclevel(Integer idLoclevel) {
		this.idLoclevel = idLoclevel;
	}

	public String getLoclevelName() {
		return loclevelName;
	}

	public void setLoclevelName(String loclevelName) {
		this.loclevelName = loclevelName;
	}

	public int getLoclevelRank() {
		return loclevelRank;
	}

	public void setLoclevelRank(int loclevelRank) {
		this.loclevelRank = loclevelRank;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idLoclevel != null ? idLoclevel.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof LocationLevel)) {
			return false;
		}
		LocationLevel other = (LocationLevel) object;
		return !((this.idLoclevel == null && other.idLoclevel != null)
				|| (this.idLoclevel != null && !this.idLoclevel.equals(other.idLoclevel)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.LocationLevel[ idLoclevel=" + idLoclevel + " ]";
	}
}
