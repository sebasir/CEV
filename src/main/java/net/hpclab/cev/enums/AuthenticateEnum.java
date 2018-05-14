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
 * Enumeración que permite determinar las diferentes opciones de mensajería que
 * se complementa con el archivo de propiedades
 * 
 * @author Sebasir
 *
 */
public enum AuthenticateEnum {
	DB_INIT_ERROR, LOGIN_ERROR, LOGIN_SUCCESS, LOGIN_INVALID_FORMAT_ERROR, LOGIN_UNKNOWN_ERROR, LOGIN_USER_NOT_ACTIVE_ERROR, LOGIN_USER_LOGGED_IN_ERROR, LOGIN_RESTART_PASSWORD
}
