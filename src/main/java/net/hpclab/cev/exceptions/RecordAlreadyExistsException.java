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

package net.hpclab.cev.exceptions;

import java.io.Serializable;

/**
 * Esta excepción sirve para definir el error de un registro ya existente en el
 * servicio de <tt>AccessService</tt>, en el momento de definir un acceso a
 * algun conjunto que ya lo tiene
 * 
 * @author Sebasir
 * @since 1.0
 * @see AccessService
 */
public class RecordAlreadyExistsException extends Exception implements Serializable {

	private static final long serialVersionUID = 4021456654556968691L;

	/**
	 * Constructor de la excepción permitiendo definir un mensaje infomativo
	 * 
	 * @param message
	 *            Mensaje que deriva de la excepción
	 */
	public RecordAlreadyExistsException(String message) {
		super(message);
	}
}
