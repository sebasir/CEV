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
 * Esta excepción sirve para definir el error de operación no conocida
 * 
 * @author Sebasir
 * @since 1.0
 */
public class UnexpectedOperationException extends Exception implements Serializable {

	private static final long serialVersionUID = -7775083240796208170L;

	/**
	 * Constructor de la excepción permitiendo definir un mensaje infomativo
	 * 
	 * @param message
	 *            Mensaje que deriva de la excepción
	 */
	public UnexpectedOperationException(String message) {
		super(message);
	}
}
