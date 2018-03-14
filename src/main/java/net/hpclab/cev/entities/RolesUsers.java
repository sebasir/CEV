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
import javax.validation.constraints.Size;

@Entity
@Table(name = "roles_users")
@NamedQueries({ @NamedQuery(name = "RolesUsers.findAll", query = "SELECT r FROM RolesUsers r"),
		@NamedQuery(name = "RolesUsers.findByKey", query = "SELECT r FROM RolesUsers r WHERE r.idRole.idRole = :idRole AND r.idUser.idUser = :idUser") })
public class RolesUsers implements Serializable {

	private static final long serialVersionUID = -8261058445815970905L;
	@Id
	@SequenceGenerator(name = "RolesUsersSeq", sequenceName = "roles_users_id_rous_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RolesUsersSeq")
	@Basic(optional = false)
	@Column(name = "id_rous")
	private Integer idRous;

	@Size(max = 32)
	@Column(name = "status")
	private String status;

	@JoinColumn(name = "id_role", referencedColumnName = "id_role")
	@ManyToOne
	private Roles idRole;
	@JoinColumn(name = "id_user", referencedColumnName = "id_user")
	@ManyToOne
	private Users idUser;

	public RolesUsers() {
	}

	public RolesUsers(Integer idRous) {
		this.idRous = idRous;
	}

	public Integer getIdRous() {
		return idRous;
	}

	public void setIdRous(Integer idRous) {
		this.idRous = idRous;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Roles getIdRole() {
		return idRole;
	}

	public void setIdRole(Roles idRole) {
		this.idRole = idRole;
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
		hash += (idRous != null ? idRous.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof RolesUsers)) {
			return false;
		}
		RolesUsers other = (RolesUsers) object;
		return !((this.idRole == null && other.idRole != null)
				|| (this.idRole != null && !this.idRole.equals(other.idRole)))
				&& !((this.idUser == null && other.idUser != null)
						|| (this.idUser != null && !this.idUser.equals(other.idUser)));
	}

	@Override
	public String toString() {
		return "net.hpclab.entities.RolesUsers[ idRous=" + idRous + " ]";
	}
}
