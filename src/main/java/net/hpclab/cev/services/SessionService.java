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
import java.util.HashMap;

import net.hpclab.cev.entities.Users;
import net.hpclab.cev.model.UserSession;

/**
 * Es un servicio creado para la encapsulación de la verificación de la sesión
 * de un usuario que se ha loggeado. El servicio ofrece una manera de acceso
 * único a través de un objeto <tt>Singleton</tt> creado en memoria estática, y
 * sincronizada, permitiendo acceder la misma instancia desde varios hilos
 * simultaneamente.
 * 
 * <p>
 * Una vez el usuario abandona la administración del CEV, el servicio ofrece la
 * manera de eliminar las propiedades de su sesión.
 * 
 * @since 1.0
 * @author Sebasir
 * @see UserSession
 */
public class SessionService implements Serializable {

	private static final long serialVersionUID = -6123381839231323244L;

	/**
	 * Mapa global que permite guardar una sesión de usuario indexada a través del
	 * identificador de sesión único que presta el servidor de aplicaciones.
	 */
	private static final HashMap<String, UserSession> ONLINE_USERS = new HashMap<>();

	/**
	 * Objeto estático de esta clase el cual permite acceder siempre a la misma
	 * referencia.
	 */
	private static SessionService sessionService;

	/**
	 * Construye un servicio
	 */
	private SessionService() {
		super();
	}

	/**
	 * Función que añade a un usuario al mapa global de usuarios.
	 * 
	 * @param sessionId
	 *            Identificador de sesión único que presta el servidor de
	 *            aplicaciones.
	 * @param userSession
	 *            Objeto de sesión de usuario.
	 */
	public void addUser(String sessionId, UserSession userSession) {
		ONLINE_USERS.put(sessionId, userSession);
	}

	/**
	 * Función que remueve del mapa global de usuarios una sesión de usuario.
	 * 
	 * @param sessionId
	 *            Identificador de sesión único que presta el servidor de
	 *            aplicaciones.
	 */
	public void removeUser(String sessionId) {
		ONLINE_USERS.remove(sessionId);
	}

	/**
	 * Función que verifica la existencia de un usuario en el mapa global.
	 * 
	 * @param users
	 * @return <tt>true</tt> Si el usuario existe.<br>
	 *         <tt>false</tt> si el usuario no existe en tal mapa
	 * 
	 */
	public boolean isUserOnline(Users users) {
		for (String sessionId : ONLINE_USERS.keySet())
			if (ONLINE_USERS.get(sessionId).getUser().equals(users))
				return true;
		return false;
	}

	/**
	 * Función que obtiene un objeto de información de sesión del usuario
	 * 
	 * @param sessionId
	 *            Identificador de sesión único que presta el servidor de
	 *            aplicaciones.
	 * @return Objeto de las sesión de un usuario
	 */
	public UserSession getUserSession(String sessionId) {
		return ONLINE_USERS.get(sessionId);
	}

	/**
	 * Método sincronizado que permite obtener la instancia <tt>Singleton</tt>,
	 * inicializandola en caso de no estar inicializada.
	 * 
	 * @return Objeto de instancia <tt>Singleton</tt> de la clase.
	 * 
	 */
	public synchronized static SessionService getInstance() {
		return sessionService == null ? (sessionService = new SessionService()) : sessionService;
	}
}
