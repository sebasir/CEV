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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.enums.ModulesEnum;

/**
 * Es un servicio creado para interceptar el inicio de la aplicación, y así
 * poder inicializar los elementos que dependen todo el sistema.
 * 
 * <p>
 * El servicio es invocado cuando desde la interfaz
 * <tt>ServletContextListener</tt> el servicio inicia el despliegue de la
 * aplicación. Los servicios a iniciar son, centralizador de listas de dominio
 * <tt>DataWarehouse</tt>, el servicio de accesos <tt>AccessService</tt>, y el
 * servicio de mensajes <tt>MessagesService</tt>.
 * 
 * @since 1.0
 * @author Sebasir
 * @see ServletContextListener
 * @see AccessService
 * @see DataWarehouse
 * @see MessagesService
 * @see Properties
 */

@WebListener
public class ApplicationListener implements ServletContextListener, HttpSessionListener, Serializable {

	private static final long serialVersionUID = 7442440188928511327L;

	/**
	 * Mantiene una manera de identificar los orígenes de impresiones de mensajes de
	 * log, a través del nombre de la clase, centralizando estos mensajes en el log
	 * del servidor de despliegue.
	 */
	private static final Logger LOGGER = Logger.getLogger(ApplicationListener.class.getSimpleName());

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			DataWarehouse.getInstance().initLists();
			AccessService.getInstance().loadAccesses();
			LOGGER.info("Inicializando nombres de entidades...");
			HashMap<String, String> entityNames = new HashMap<>();
			entityNames.put("AuditLog", "Auditoria de Usuario");
			entityNames.put("Author", "Autor");
			entityNames.put("Catalog", "Catalogo");
			entityNames.put("Collection", "Colección");
			entityNames.put("Institution", "Instituto");
			entityNames.put("Location", "Ubicación");
			entityNames.put("LocationLevel", "Nivel de Ubicación");
			entityNames.put("Modules", "Modulo");
			entityNames.put("ModulesUsers", "Modulo de Usuario");
			entityNames.put("RegType", "Tipo de Registro");
			entityNames.put("Roles", "Rol");
			entityNames.put("RolesModules", "Roles de Módulo");
			entityNames.put("RolesUsers", "Roles de Usuario");
			entityNames.put("SampleType", "Tipo de Ejemplar");
			entityNames.put("Specimen", "Espécimen");
			entityNames.put("SpecimenContent", "Contenido");
			entityNames.put("Taxonomy", "Clasificación Taxonómica");
			entityNames.put("TaxonomyLevel", "Nivel de Clasificación");
			entityNames.put("Users", "Usuario");
			Util.setEntityNames(entityNames);

			LOGGER.info("Inicializando objetos [Modules]...");
			HashMap<ModulesEnum, Modules> modules = new HashMap<>();
			modules.put(ModulesEnum.LOGIN, new Modules(2));
			Util.setModules(modules);

			Properties messages = new Properties();
			messages.load(sce.getServletContext().getResourceAsStream(Constant.MESSAGES_FILE));
			MessagesService.getInstance().loadMessages(messages);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error inicializando: {0}", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		System.out.println("session " + se.getSession().getId() + " created!");
		// se.getSession().setMaxInactiveInterval(Constant.MAX_IDLE_SESSION_NO_LOGGED);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		try {
			AuditService.clearInstance();
		} catch (Exception e) {

		}
		SessionService.getInstance().removeUser(se.getSession().getId());
		System.out.println("session " + se.getSession().getId() + " destroyed!");
	}
}
