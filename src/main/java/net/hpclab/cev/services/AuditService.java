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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.hpclab.cev.entities.AuditLog;
import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.entities.Users;
import net.hpclab.cev.enums.AuditEnum;

/**
 * Es un servicio creado para registrar los eventos de cualquier clase que se
 * realicen sobre la base de datos. Incluye alguas operaciones a nivel lógico,
 * como se describen en la enumeración <tt>AuditEnum</tt>. Una traza de log es
 * insertada en base de datos una vez el sistema solicite de su inserción, a
 * través del servicio de <tt>DataBaseService</tt>.
 * 
 * <p>
 * El servicio ofrece una manera de acceso único a través de un objeto
 * <tt>Singleton</tt> creado en memoria estática, y sincronizada, permitiendo
 * acceder la misma instancia desde varios hilos simultaneamente.
 * 
 * @since 1.0
 * @author Sebasir
 * @see DataBaseService
 * @see AuditLog
 */
public class AuditService implements Serializable {

	private static final long serialVersionUID = 4764154001268761150L;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(AuditService.class.getSimpleName());

	/**
	 * Objeto estático de esta clase el cual permite acceder siempre a la misma
	 * referencia.
	 */
	private static AuditService auditService;

	/**
	 * Objeto que parametriza el servicio <tt>DataBaseService</tt> con la clase
	 * <tt>AuditLog</tt>, lo cual permite extender todas las operaciones del
	 * servicio para esta clase.
	 */
	private DataBaseService<AuditLog> auditDBService;

	/**
	 * Objeto que representa una entidad a ser persistida.
	 */
	private AuditLog auditLog;

	/**
	 * Construye un servicio inicializando los servicios derivados de
	 * <tt>DataBaseService</tt>, para las etidades <tt>AuditLog</tt>.
	 */
	private AuditService() {
		try {
			auditDBService = new DataBaseService<>(AuditLog.class);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "El servicio de log no ha podido iniciar correctamente: {0}.", e.getMessage());
		}
	}

	/**
	 * Función que permite insertar una operación realizada, ya sea lógica o física,
	 * e insertarla en la tabla, identificando el usuario, el módulo, la dirección
	 * IP de origen, el tipo de acción ejecutada según describe la enumeración
	 * <tt>AuditEnum</tt>, y el objetivo de la operación.
	 * 
	 * @param idUser
	 *            Objeto que representa el usuario, con la llave única.
	 * @param idModule
	 *            Objeto que representa el módulo, con la llave única.
	 * @param aulogIpAddress
	 *            Dirección IPv4 del usuario quien realiza la operación
	 * @param aulogAction
	 *            Operacion a registrar en la base de datos
	 * @param aulogTarget
	 *            Objetivo de la operación a registrar.
	 */
	public void log(Users idUser, Modules idModule, String aulogIpAddress, AuditEnum aulogAction, String aulogTarget) {
		try {
			LOGGER.log(Level.INFO, "User: {0}, Module: {1}, IP: {2}, Action: {3}, target: {4}", new Object[] {
					idUser.getIdUser(), idModule.getIdModule(), aulogIpAddress, aulogAction.get(), aulogTarget });
			auditLog = new AuditLog();
			auditLog.setIdUser(idUser);
			auditLog.setIdModule(idModule);
			auditLog.setAulogTime(new Date());
			auditLog.setAulogIpAddress(aulogIpAddress);
			auditLog.setAulogAction(aulogAction.get());
			auditLog.setAulogTarget(aulogTarget);
			auditDBService.persist(auditLog);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error realizando inserción en AuditLog: {0}", e);
		}
	}

	/**
	 * Función que permite limpiar la instancia,
	 */
	public static void clearInstance() {
		auditService = null;
	}

	/**
	 * Método sincronizado que permite obtener la instancia <tt>Singleton</tt>,
	 * inicializandola en caso de no estar inicializada.
	 * 
	 * @return Objeto de instancia <tt>Singleton</tt> de la clase.
	 */
	public static synchronized AuditService getInstance() {
		return auditService == null ? (auditService = new AuditService()) : auditService;
	}
}
