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

package net.hpclab.cev.enums;

/**
 * Enumeración que permite determinar los diferentes estados que representen un
 * registros sobre la base de datos, y que permiten obtener su nombre narrativo
 * 
 * @author Sebasir
 *
 */
public enum StatusEnum {
	ACTIVO("Activo"), DESHABILITADO("Deshabilitado"), BLOQUEADO("Bloqueado"), INCOMPLETO("Incompleto"), COMPLETO(
			"Completo");

	/**
	 * Propiedad que permite definir un valor para una de la enumeraciones
	 */
	private final String status;

	/**
	 * Constructor de los estados
	 * 
	 * @param audit
	 *            Valor de la enumeración
	 */
	private StatusEnum(String status) {
		this.status = status;
	}

	/**
	 * @return El valor de una enumeración
	 */
	public String get() {
		return status;
	}
}
