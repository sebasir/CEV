package net.hpclab.cev.services;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.hpclab.cev.enums.AuditEnum;
import net.hpclab.cev.entities.AuditLog;
import net.hpclab.cev.entities.Modules;
import net.hpclab.cev.entities.Users;

public class AuditService implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuditService.class.getSimpleName());
    private static AuditService auditService;
    private DataBaseService<AuditLog> auditDBService;
    private AuditLog auditLog;

    private AuditService() {
        try {
            auditDBService = new DataBaseService<>(AuditLog.class);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "El servicio de log no ha podido iniciar correctamente: {0}.", e.getMessage());
        }
    }

    public void log(Users idUser, Modules idModule, String aulogIpAddress, AuditEnum aulogAction, String aulogTarget) {
        try {
            LOGGER.log(Level.INFO, "User: {0}, Module: {1}, IP: {2}, Action: {3}, target: {4}", new Object[]{idUser.getIdUser(), idModule.getIdModule(), aulogIpAddress, aulogAction.get(), aulogTarget});
            auditLog = new AuditLog();
            auditLog.setIdUser(idUser);
            auditLog.setIdModule(idModule);
            auditLog.setAulogTime(new Date());
            auditLog.setAulogIpAddress(aulogIpAddress);
            auditLog.setAulogAction(aulogAction.get());
            auditLog.setAulogTarget(aulogTarget);
            auditDBService.persist(auditLog);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error realizando inserción en AuditLog: {0}", e.getMessage());
        }
    }

    public static synchronized AuditService getInstance() {
        return auditService == null ? (auditService = new AuditService()) : auditService;
    }
}
