package net.hpclab.sessions;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class Generic<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    @PersistenceContext(unitName = "bichosPU")
    private EntityManager em;
    private Class<T> type;

    public Generic() {
    }

    public Generic(Class<T> type) {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro la Clase - " + type.getName());
        this.type = type;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public T persist(T t) {
        try {
            this.em.persist(t);
            this.em.flush();
            this.em.refresh(t);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return t;
    }

    public T find(Object id) {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo buscar - " + type.getName());
        return this.em.find(this.type, id);
    }

    public T findById(Object id) {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo buscarReferencia - " + type.getName());
        return this.em.getReference(this.type, id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public T merge(T entity) {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo actualizar - " + type.getName());
        em.merge(entity);
        return entity;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(T entity) {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo eliminar - " + type.getName());
        entity = em.merge(entity);
        em.remove(entity);
    }

    public List<T> listAll() {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo listAll - " + type.getName());
        return this.em.createNamedQuery(type.getSimpleName() + ".findAll").getResultList();
    }
    
    public void joinTransaction() {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo joinTransaction - " + type.getName());
        em.joinTransaction();
    }

    public List findByQuery(String namedQueryName) {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo buscarXConsulta - " + type.getName());
        return this.em.createNamedQuery(namedQueryName).getResultList();
    }

    public List findByQuery(String namedQueryName, Map parameters) {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo buscarXConsulta - " + type.getName());
        return findByQuery(namedQueryName, parameters, 0);
    }

    public List findByQuery(String queryName, int resultLimit) {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo buscarXConsulta - " + type.getName());
        return this.em.createNamedQuery(queryName).
                setMaxResults(resultLimit).
                getResultList();
    }

    public List<T> findBySQL(String sql) {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo buscarXConsulta - " + type.getName());
        return this.em.createNativeQuery(sql, type).getResultList();
    }

    public int count(String namedQueryName) {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo count - " + type.getName());
        return ((Number)em.createNamedQuery(namedQueryName).getSingleResult()).intValue();
    }

    public List findByQuery(String namedQueryName, Map parameters, int resultLimit) {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo buscarXConsulta - " + type.getName());
        Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = this.em.createNamedQuery(namedQueryName);
        if (resultLimit > 0)
            query.setMaxResults(resultLimit);
        for (Map.Entry<String, Object> entry : rawParameters)
            query.setParameter(entry.getKey(), entry.getValue());
        return query.getResultList();
    }

    public List findByQuery(String namedQueryName, int start, int end) {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo buscarXConsulta - " + type.getName());
        Query query = this.em.createNamedQuery(namedQueryName);
        query.setMaxResults(end - start);
        query.setFirstResult(start);
        return query.getResultList();
    }

    public T findByParams(String namedQuery, Map<String, Object> parameters) {
        Logger.getLogger(type.getName()).log(Level.DEBUG, "--> Entro al metodo buscarRegistro - " + type.getName());
        T result = null;
        try {
            Query query = em.createNamedQuery(namedQuery);
            if (parameters != null && !parameters.isEmpty()) {
                Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
                for (Map.Entry<String, Object> entry : rawParameters)
                    query.setParameter(entry.getKey(), entry.getValue());
            }
            result = (T) query.getSingleResult();
            em.refresh(result);
        } catch (NoResultException e) {
            Logger.getLogger(type.getName()).log(Level.ERROR, "No result found for named query: " + namedQuery, e);
        } catch (Exception e) {
            Logger.getLogger(type.getName()).log(Level.ERROR, "Error while running query: ", e);
        }
        return result;
    }
}