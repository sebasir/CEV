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
 * Enumeración que permite determinar los diferentes módulos a los cuales se
 * tienen acceso como interceptables de módulos, que permiten obtener su nombre
 * narrativo
 * 
 * @author Sebasir
 *
 */
public enum ModulesEnum {
	LOGIN("Login"), COLLECTION("Colecciones y Catalogos"), CONTENT("Contenido Gráfico"), USR_INS(
			"Usuarios e Instituciones"), AUDIT("Auditoria"), ACCESSES("Accesos"), SPECIMEN("Especímenes"), AUTHOR(
					"Autores"), LOCATION("Ubicaciones"), TAXONOMY("Clasificaciones"), WIZARD(
							"Asistente de Creación"), REPORTER("Reporteador"), SETTINGS("Configuración");

	/**
	 * Propiedad que permite definir un valor para una de la enumeraciones
	 */
	private final String status;

	/**
	 * Constructor de los módulos
	 * 
	 * @param audit
	 *            Valor de la enumeración
	 */
	private ModulesEnum(String status) {
		this.status = status;
	}

	/**
	 * @return El valor de una enumeración
	 */
	public String get() {
		return status;
	}
}
