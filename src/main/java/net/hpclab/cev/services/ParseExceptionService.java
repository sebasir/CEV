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

package net.hpclab.cev.services;

/**
 * Es un servicio creado para la encapsulación del manejo de errores de los que
 * se obtienen desde el servicio de <tt>DataBaseService</tt>. El servicio ofrece
 * una manera de acceso único a través de un objeto <tt>Singleton</tt> creado en
 * memoria estática, y sincronizada, permitiendo acceder la misma instancia
 * desde varios hilos simultaneamente.
 * 
 * @since 1.0
 * @author Sebasir
 * @see DataBaseService
 */

public class ParseExceptionService {

	/**
	 * Objeto estático de esta clase el cual permite acceder siempre a la misma
	 * referencia.
	 */
	private static ParseExceptionService service;

	/**
	 * Función que traduce una excepción en un mensaje legible para un usuario, y
	 * luego ser presentado en pantalla
	 * 
	 * @param e
	 *            Excepción a traducir
	 * @return Mensaje traducido
	 */
	public String parse(Exception e) {
		String errorMessage = null;
		try {
			Throwable t = e;
			while (!t.getClass().getName().equals("org.postgresql.util.PSQLException") && t != null)
				t = t.getCause();
			errorMessage = t.getMessage();
		} catch (Exception ex) {
			errorMessage = "No se pudo obtener a causa de error:\nError original = " + e.getMessage();
		}
		return errorMessage;
	}

	/**
	 * Método sincronizado que permite obtener la instancia <tt>Singleton</tt>,
	 * inicializandola en caso de no estar inicializada.
	 * 
	 * @return Objeto de instancia <tt>Singleton</tt> de la clase.
	 */
	public static synchronized ParseExceptionService getInstance() {
		if (service == null)
			service = new ParseExceptionService();
		return service;
	}
}
