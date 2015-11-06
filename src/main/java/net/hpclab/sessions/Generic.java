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

public abstract class Generic<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    @PersistenceContext(unitName = "bichosPU")
    private EntityManager em;
    private Class<T> type;

    public Generic() {
    }

    public Generic(Class<T> type) {
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
        return this.em.find(this.type, id);
    }

    public T findById(Object id) {
        return this.em.getReference(this.type, id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public T merge(T entity) {
        em.merge(entity);
        return entity;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(T entity) {
        entity = em.merge(entity);
        em.remove(entity);
    }

    public List<T> listAll() {
        return this.em.createNamedQuery(type.getSimpleName() + ".findAll").getResultList();
    }
    
    public void joinTransaction() {
        em.joinTransaction();
    }

    public List findByQuery(String namedQueryName) {
        return this.em.createNamedQuery(namedQueryName).getResultList();
    }

    public List findByQuery(String namedQueryName, Map parameters) {
        return findByQuery(namedQueryName, parameters, 0);
    }

    public List findByQuery(String queryName, int resultLimit) {
        return this.em.createNamedQuery(queryName).
                setMaxResults(resultLimit).
                getResultList();
    }

    public List<T> findBySQL(String sql) {
        return this.em.createNativeQuery(sql, type).getResultList();
    }

    public int count(String namedQueryName) {
        return ((Number)em.createNamedQuery(namedQueryName).getSingleResult()).intValue();
    }

    public List findByQuery(String namedQueryName, Map parameters, int resultLimit) {
        Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = this.em.createNamedQuery(namedQueryName);
        if (resultLimit > 0)
            query.setMaxResults(resultLimit);
        for (Map.Entry<String, Object> entry : rawParameters)
            query.setParameter(entry.getKey(), entry.getValue());
        return query.getResultList();
    }

    public List findByQuery(String namedQueryName, int start, int end) {
        Query query = this.em.createNamedQuery(namedQueryName);
        query.setMaxResults(end - start);
        query.setFirstResult(start);
        return query.getResultList();
    }

    public T findByParams(String namedQuery, Map<String, Object> parameters) {
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

        } catch (Exception e) {

        }
        return result;
    }
}