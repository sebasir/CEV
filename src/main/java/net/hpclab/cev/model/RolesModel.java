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

import net.hpclab.cev.entities.Roles;

/**
 * Este modelo permite tener un abstracción de la relación de un rol y un estado
 * inicial, comparado contra el estado activo de u rol para un usuario.
 * 
 * @author Sebasir
 * @since 1.0
 * @see Roles
 */
public class RolesModel implements Comparable<RolesModel> {

	/**
	 * Objeto que representa el rol, con la llave única.
	 */
	private Roles roles;

	/**
	 * Determina si el rol debe persistirse como activo o no
	 */
	private boolean active;

	/**
	 * Determina el estado inicial del rol
	 */
	private boolean initialState;

	/**
	 * Constructor que permite indicar el objeto del rol, así como el estado inicial
	 * para un usuario
	 * 
	 * @param roles
	 *            Objeto que representa el rol, con la llave única.
	 * @param active
	 *            Estado inicial indicando el estado del rol
	 */
	public RolesModel(Roles roles, boolean active) {
		this.roles = roles;
		this.active = active;
		restartUnchanged();
	}

	/**
	 * @return El objeto que representa el rol, con la llave única.
	 */
	public Roles getRoles() {
		return roles;
	}

	/**
	 * @param roles
	 *            Objeto que representa el rol, con la llave única a definir.
	 */
	public void setRoles(Roles roles) {
		this.roles = roles;
	}

	/**
	 * @return Estado de del rol para un usuario
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active
	 *            Define el estado de un rol para un usuario
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return Determina si el estado del rol para un usuario ha cambiado en la
	 *         edicion de roles
	 */
	public boolean hasChanged() {
		return initialState != active;
	}

	/**
	 * Reinicia el estado del rol para el usuario
	 */
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
