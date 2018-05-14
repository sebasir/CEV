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
 * Enumeración que permite determinar los diferentes resultados que puedan
 * obtenerse de una acción sobre la base de datos que se deriva en mensajería al
 * usuario
 * 
 * @author Sebasir
 * @since 1.0
 *
 */
public enum OutcomeEnum {
	CREATE_SUCCESS, UPDATE_SUCCESS, DELETE_SUCCESS, CREATE_ERROR, UPDATE_ERROR, DELETE_ERROR, FILE_REQUIRED, FILE_UPLOAD_SUCCESS, FILE_DELETE_SUCCESS, FILE_UPLOAD_ERROR, FILE_DELETE_ERROR, FILE_UPLOAD_WARNING, GENERIC_ERROR, GENERIC_INFO, GENERIC_WARNING, SELECT_NOT_GRANTED, UPDATE_NOT_GRANTED, DELETE_NOT_GRANTED, INSERT_NOT_GRANTED
}
