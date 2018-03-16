package net.hpclab.cev.model;

import net.hpclab.cev.entities.Roles;

public class RolesModel implements Comparable<RolesModel> {
	private Roles roles;
	private boolean active;
	private boolean initialState;

	public RolesModel(Roles roles, boolean active) {
		this.roles = roles;
		this.active = active;
		restartUnchanged();
	}

	public Roles getRoles() {
		return roles;
	}

	public void setRoles(Roles roles) {
		this.roles = roles;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean hasChanged() {
		return initialState != active;
	}

	public void restartUnchanged() {
		this.initialState = active;
	}

	@Override
	public int hashCode() {
		return 2151 * this.roles.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RolesModel))
			return false;

		RolesModel m = (RolesModel) o;
		return m != null && m.roles != null && m.roles.getIdRole() != null && this.roles != null
				&& this.roles.getIdRole() != null && this.roles.getIdRole().equals(m.roles.getIdRole());
	}

	@Override
	public int compareTo(RolesModel o) {
		return this.roles.getIdRole().compareTo(o.getRoles().getIdRole());
	}
}
