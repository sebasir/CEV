package net.hpclab.cev.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import net.hpclab.cev.entities.Institution;
import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.enums.ModulesEnum;

@WebListener
public class ApplicationListener implements ServletContextListener, HttpSessionListener, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ApplicationListener.class.getSimpleName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
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

            LOGGER.info("Inicializando dominios de instituciones");
            DataBaseService<Institution> institutionService = new DataBaseService<>(Institution.class);
            Util.setInstitutions(institutionService.getList("Institution.findAll"));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error inicializando: {0}", e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        SessionService.removeUser(se.getSession().getId());
    }
}
