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
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.enums.ModulesEnum;

/**
 * Es un servicio creado para el manejo de utilidades de comparación de cadenas,
 * patrones de email, comparación de numeros, encripción de claves, entre otros
 * 
 * @since 1.0
 * @author Sebasir
 * @see Pattern
 */

public class Util implements Serializable {

	private static final long serialVersionUID = -3495639167934741566L;

	/**
	 * Mapa de objetos así como los propiedades de los módulos de la enumeración
	 */
	private static HashMap<String, String> entityNames;

	/**
	 * Mapa de nombres de clase junto a sus coincidencias narrativas
	 */
	private static HashMap<ModulesEnum, Modules> modules;

	/**
	 * Patron de coincidencia parra correo electrónico
	 */
	private static final Pattern EMAIL_PATTERN = Pattern.compile(Constant.EMAIL_REGEX);

	/**
	 * Función que compara una cadena contra un valor null, o bien vacía
	 * 
	 * @param string
	 *            Cadena de texto a comparar
	 * @return <tt>true</tt> Si la cadena es nula o vacía<br>
	 *         <tt>false</tt> Cuando nó
	 */
	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}

	/**
	 * Función que compara un número contra un valor null, o bien 0
	 * 
	 * @param number
	 *            Número a comparar
	 * @return <tt>true</tt> Si el número es nulo o igual a 0<br>
	 *         <tt>false</tt> Cuando no.
	 */
	public static boolean isEmpty(Long number) {
		return number == null || number == 0;
	}

	/**
	 * Función que compara un número contra un valor null, o bien 0
	 * 
	 * @param number
	 *            Número a comparar
	 * @return <tt>true</tt> Si el número es nulo o igual a 0<br>
	 *         <tt>false</tt> Cuando no.
	 */
	public static boolean isEmpty(Integer number) {
		return number == null || number == 0;
	}

	/**
	 * Función que compara un número contra un valor null, o bien 0
	 * 
	 * @param number
	 *            Número a comparar
	 * @return <tt>true</tt> Si el número es nulo o igual a 0<br>
	 *         <tt>false</tt> Cuando no.
	 */
	public static boolean isEmpty(BigDecimal number) {
		return number == null || number.compareTo(BigDecimal.ZERO) == 0;
	}

	/**
	 * Función que dada una cadena de texto, la encripta según la llave de CEV.
	 * 
	 * @param pass
	 *            Cadena de texto a encriptar
	 * @return Cadena de texto encriptada
	 * @throws Exception
	 *             Cuando hubo un error obteniendo el encriptor.
	 */
	public static String encrypt(String pass) throws Exception {
		pass += Constant.ENCRYPT_KEY;
		MessageDigest md = MessageDigest.getInstance(Constant.HASH_ALGORITHM);
		md.update(pass.getBytes(Constant.CHAR_ENCODING));
		pass = DatatypeConverter.printBase64Binary(md.digest());
		return pass;
	}

	/**
	 * Función que dada una cadena de texto, la encripta según la llave de CEV.
	 * 
	 * @param email
	 *            Cadena de texto del email a procesar
	 * @return <tt>true</tt> Si cumple con el patrón<br>
	 *         <tt>false</tt> Si no lo cumple
	 * @throws Exception
	 *             Cuando hubo un error obteniendo el encriptor.
	 */
	public static boolean checkEmail(final String email) {
		return EMAIL_PATTERN.matcher(email).matches();
	}

	/**
	 * Función que retorna el nombre de una lista global dado su valor de la
	 * enumeración <tt>ModulesEnum</tt>
	 * 
	 * @param moduleName
	 *            Valor del módulo según la enumeración
	 * @return El objeto del Módulo según su entidad
	 */
	public static Modules getModule(ModulesEnum moduleName) {
		return modules.get(moduleName);
	}

	/**
	 * Función encargada de evaluar un nombre de clase y obtener su valor narrativo
	 * 
	 * @param className
	 *            Nombre de la clase a obtener.
	 * @return Nombre narrativo de la clase.
	 */
	public static String getEntityNames(String className) {
		return entityNames.get(className);
	}

	/**
	 * @return Mapa de nombres de clase junto a sus coincidencias narrativas
	 */
	public static HashMap<String, String> getEntityNames() {
		return entityNames;
	}

	/**
	 * @param aEntityNames
	 *            Mapa de nombres a definir
	 */
	public static void setEntityNames(HashMap<String, String> aEntityNames) {
		entityNames = aEntityNames;
	}

	/**
	 * @return El mapa de objetos así como los propiedades de los módulos de la
	 *         enumeración.
	 */
	public static HashMap<ModulesEnum, Modules> getModules() {
		return modules;
	}

	/**
	 * @param aModules
	 *            Mapa de objetos así como los propiedades de los módulos de la
	 *            enumeración a definir.
	 */
	public static void setModules(HashMap<ModulesEnum, Modules> aModules) {
		modules = aModules;
	}
}
