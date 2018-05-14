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
 * Esta excepción sirve para definir el error de tipo de un usuario intentando
 * realizar una operación no permitida en algún servicio, sobre un módulo.
 * 
 * @author Sebasir
 * @since 1.0
 */
public class UnauthorizedAccessException extends Exception implements Serializable {

	private static final long serialVersionUID = -2045014671560211083L;

	/**
	 * Constructor de la excepción permitiendo definir un mensaje infomativo
	 * 
	 * @param message
	 *            Mensaje que deriva de la excepción
	 */
	public UnauthorizedAccessException(String message) {
		super(message);
	}
}
