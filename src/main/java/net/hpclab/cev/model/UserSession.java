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

import java.io.Serializable;
import net.hpclab.cev.entities.Users;

/**
 * Este modelo permite tener la información de un usuario que es loggeado y
 * posteriormente, es puesto en una lista global que permite identificar si el
 * usuario ha ingresado
 * 
 * @author Sebasir
 * @since 1.0
 * @see Users
 */

public class UserSession implements Serializable {

	private static final long serialVersionUID = 6507038081295505722L;

	/**
	 * Dirección IPv4 del usuario que se loggea en el sistema.
	 */
	private String ipAddress;

	/**
	 * Objeto del usuario que se loggea, para obtener su información a lo largo de
	 * todos los módulos del sistema
	 */
	private Users user;

	/**
	 * Constructor que define las propiedades básicas de este modelo
	 * 
	 * @param user
	 *            Objeto que representa el usuario, con la llave única.
	 * @param ipAddress
	 *            Dirección IPv4 del usuario que se loggea
	 */
	public UserSession(Users user, String ipAddress) {
		this.ipAddress = ipAddress;
		this.user = user;
	}

	/**
	 * @return La dirección IPv4 del usuario que se loggea
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            IPv4 del usuario que se loggea
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return Objeto que representa el usuario, con la llave única.
	 */
	public Users getUser() {
		return user;
	}

	/**
	 * @param user
	 *            Objeto que representa el usuario a definir
	 */
	public void setUser(Users user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "{User: " + user + ", ipAddress: " + ipAddress + "}";
	}
}
