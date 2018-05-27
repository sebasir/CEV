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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;

import org.hibernate.Cache;
import org.hibernate.Session;

/**
 * Es un servicio creado para la encapsulación del servicio de inicio de
 * transacción con la base de datos. El servicio ofrece una manera de acceso
 * único a través de un objeto <tt>Singleton</tt> creado en memoria estática, y
 * sincronizada, permitiendo acceder la misma instancia desde varios hilos
 * simultaneamente.
 * 
 * @since 1.0
 * @author Sebasir
 * @see EntityManagerFactory
 * 
 */
public class EntityResourcer {

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(EntityResourcer.class.getSimpleName());

	/**
	 * Objeto estático de esta clase el cual permite acceder siempre a la misma
	 * referencia.
	 */
	private static EntityResourcer entityResourcer;

	/**
	 * Objeto que permite inyectar el componente de persistencia obtenido desde la
	 * conexión JTA que ofrece el servidor de despliegue
	 */
	@PersistenceUnit(unitName = Constant.PERSISTENCE_UNIT)
	private static EntityManagerFactory entityManagerFactory;

	/**
	 * Servicio que permite iniciar el servicio de conexión de base de datos, en
	 * caso de no ser inyectado.
	 */
	public void initService() {
		LOGGER.log(Level.INFO, "Obteniendo EntityManagerFactory");
		if (entityManagerFactory == null) {
			LOGGER.log(Level.INFO, "entityManagerFactory is null");
			entityManagerFactory = Persistence.createEntityManagerFactory(Constant.PERSISTENCE_UNIT);
		} else {
			LOGGER.log(Level.INFO, "entityManagerFactory is injected!");
		}
	}

	/**
	 * Constructor que permite iniciar el servicio.
	 * 
	 * @throws PersistenceException
	 *             Cuando no es posible conectarse a la base de datos
	 * @throws Exception
	 *             Cuando existe otra excepción
	 */
	private EntityResourcer() throws PersistenceException, Exception {
		initService();
	}

	/**
	 * Función que permite obtener un administrador de entidades desde la fábrica
	 * obtenida en el inicio del servicio
	 * 
	 * @return Administrador de entidades
	 */
	public EntityManager getEntityManager() {
		initService();
		return entityManagerFactory.createEntityManager();
	}

	/**
	 * Permite desconectar el administrador de persistencia de la base de datos
	 */
	public void disconnect() {
		if (entityManagerFactory != null) {
			clearCache();
			entityManagerFactory.close();
			entityManagerFactory = null;
			LOGGER.log(Level.INFO, "entityManagerFactory is closed");
		}
	}

	private void clearCache() {
		try {
			Session s = (Session) getEntityManager().getDelegate();
			s.clear();
			Cache cache = s.getSessionFactory().getCache();

			if (cache != null)
				cache.evictAllRegions();

			LOGGER.log(Level.INFO, "Session cleared");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error trying to clear session", e);
		}
	}

	/**
	 * Función que permite conocer si el servicio de base de datos está listo para
	 * ser usado
	 * 
	 * @return <tt>true</tt> si el servicio está inicializado<br>
	 *         <tt>false</tt> si no lo esta.
	 */
	public boolean isConnected() {
		return entityManagerFactory != null;
	}

	/**
	 * Método sincronizado que permite obtener la instancia <tt>Singleton</tt>,
	 * inicializandola en caso de no estar inicializada.
	 * 
	 * @return Objeto de instancia <tt>Singleton</tt> de la clase.
	 * @throws PersistenceException
	 *             Cuando una excepción específica de la base de datos a partir de
	 *             la conecxión con el componente de persistencia
	 * @throws Exception
	 *             Cuando hay un error cargando los accesos.
	 */
	public static synchronized EntityResourcer getInstance() throws PersistenceException, Exception {
		if (entityResourcer == null)
			entityResourcer = new EntityResourcer();
		return entityResourcer;
	}
}
