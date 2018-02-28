package net.hpclab.cev.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;

public class EntityResourcer {
	private static final Logger LOGGER = Logger.getLogger(EntityResourcer.class.getSimpleName());
	private static EntityResourcer entityResourcer;

	@PersistenceUnit(unitName = Constant.PERSISTENCE_UNIT)
	private static EntityManagerFactory entityManagerFactory;

	public void initService() {
		LOGGER.log(Level.INFO, "Obteniendo EntityManager");
		if (entityManagerFactory == null) {
			LOGGER.log(Level.INFO, "entityManagerFactory is null");
			entityManagerFactory = Persistence.createEntityManagerFactory(Constant.PERSISTENCE_UNIT);
		} else {
			LOGGER.log(Level.INFO, "entityManagerFactory is injected!");
		}
	}
	
	private EntityResourcer() throws PersistenceException, Exception {
		initService();
	}

	public EntityManager getEntityManager() {
		initService();
		return entityManagerFactory.createEntityManager();
	}

	public boolean isConnected() {
		return entityManagerFactory != null;
	}

	public static synchronized EntityResourcer getInstance() throws PersistenceException, Exception {
		if (entityResourcer == null)
			entityResourcer = new EntityResourcer();
		return entityResourcer;
	}
}
