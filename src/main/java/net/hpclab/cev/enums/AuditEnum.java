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
 * Enumeración que permite determinar las diferentes operaciones que se realizan
 * sobre algún módulo, y que posteriormente, pueden ser inscritas en una tabla
 * de auditoria
 * 
 * @author Sebasir
 * @since 1.0
 *
 */
public enum AuditEnum {
	LOGIN("LOGIN"), LOGOUT("LOGOUT"), INSERT("INSERT"), UPDATE("UPDATE"), DELETE("DELETE"), STATUS_CHANGE(
			"STATUS_CHANGE");

	/**
	 * Propiedad que permite definir un valor para una de la enumeraciones
	 */
	private final String audit;

	/**
	 * Constructor de las operaciones
	 * 
	 * @param audit
	 *            Valor de la enumeración
	 */
	private AuditEnum(String audit) {
		this.audit = audit;
	}

	/**
	 * @return El valor de una enumeración
	 */
	public String get() {
		return audit;
	}
}
