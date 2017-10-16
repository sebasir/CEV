package net.hpclab.cev.entities;

import java.io.Serializable;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "modules_users")
@NamedQueries({ @NamedQuery(name = "ModulesUsers.findAll", query = "SELECT m FROM ModulesUsers m"),
		@NamedQuery(name = "ModulesUsers.findByKey", query = "SELECT m FROM ModulesUsers m WHERE m.idModule.idModule = :idModule AND m.idUser.idUser = :idUser") })
public class ModulesUsers implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "ModulesUsersSeq", sequenceName = "modules_users_id_mous_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ModulesUsersSeq")
	@Basic(optional = false)
	@Column(name = "id_mous")
	private Integer idMous;
	@Basic(optional = false)
	@NotNull
	@Column(name = "access_level")
	private int accessLevel;

	@Size(max = 32)
	@Column(name = "status")
	private String status;

	@JoinColumn(name = "id_module", referencedColumnName = "id_module")
	@ManyToOne
	private Modules idModule;
	@JoinColumn(name = "id_user", referencedColumnName = "id_user")
	@ManyToOne
	private Users idUser;

	public ModulesUsers() {
	}

	public ModulesUsers(Integer idMous) {
		this.idMous = idMous;
	}

	public ModulesUsers(Integer idMous, int accessLevel) {
		this.idMous = idMous;
		this.accessLevel = accessLevel;
	}

	public Integer getIdMous() {
		return idMous;
	}

	public void setIdMous(Integer idMous) {
		this.idMous = idMous;
	}

	public int getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Modules getIdModule() {
		return idModule;
	}

	public void setIdModule(Modules idModule) {
		this.idModule = idModule;
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
		hash += (idMous != null ? idMous.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof ModulesUsers)) {
			return false;
		}
		ModulesUsers other = (ModulesUsers) object;
		return !((this.idMous == null && other.idMous != null)
				|| (this.idMous != null && !this.idMous.equals(other.idMous)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.ModulesUsers[ idMous=" + idMous + " ]";
	}
}
