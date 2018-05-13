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

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Properties;
import net.hpclab.cev.enums.AuthenticateEnum;

/**
 * Servicio diseñado para la administración de mensajes para las páginas de
 * inicio a partir de un archivo de propiedades cargado por un usuario. El
 * servicio ofrece una manera de acceso único a través de un objeto
 * <tt>Singleton</tt> creado en memoria estática, y sincronizada, permitiendo
 * acceder la misma instancia desde varios hilos simultaneamente.
 * 
 * @since 1.0
 * @author Sebasir
 * @see Properties
 * 
 */
public class MessagesService implements Serializable {

	private static final long serialVersionUID = 1271212272377001258L;

	/**
	 * Objeto estático de esta clase el cual permite acceder siempre a la misma
	 * referencia.
	 */
	private static MessagesService messagesService;

	/**
	 * Representación del archivo de propiedades que contiene los mensajes a
	 * administrar.
	 */
	private static Properties messages;

	/**
	 * Función que carga los mensajes en el servicio
	 * 
	 * @param messages
	 *            Mensajes a cargar
	 */
	public void loadMessages(Properties messages) {
		MessagesService.messages = messages;
	}

	/**
	 * Función que obtiene un mensaje a partir de un valor de la enumeración
	 * <tt>AuthenticateEnum</tt>, y además, añade los parámetros para los cuales el
	 * mensaje está diseñado
	 * 
	 * @param ae
	 *            Operación del mensaje a obtener y formatear, desde la enumeración
	 * @param params
	 *            Arreglo de objetos para añadir al mensaje
	 * @return Mensaje desde el archivo de propiedades con los parámetros cargados
	 */
	public String getMessage(AuthenticateEnum ae, Object... params) {
		return getMessage(ae.name(), params);
	}

	/**
	 * Función que obtiene un mensaje a partir de un valor de la enumeración
	 * <tt>AuthenticateEnum</tt>, y además, añade los parámetros para los cuales el
	 * mensaje está diseñado
	 * 
	 * @param prop
	 *            Nombre de la operación que existe en la enumeración
	 * @param params
	 *            Arreglo de objetos para añadir al mensaje
	 * @return Mensaje desde el archivo de propiedades con los parámetros cargados
	 */
	public String getMessage(String prop, Object... params) {
		String message = messages.getProperty(prop);
		if (params != null && params.length > 0) {
			message = MessageFormat.format(message, params);
		}
		return message;
	}

	/**
	 * Método sincronizado que permite obtener la instancia <tt>Singleton</tt>,
	 * inicializandola en caso de no estar inicializada.
	 * 
	 * @return Objeto de instancia <tt>Singleton</tt> de la clase.
	 */
	public static synchronized MessagesService getInstance() {
		return messagesService == null ? (messagesService = new MessagesService()) : messagesService;
	}
}
