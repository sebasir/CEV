package net.hpclab.sessions;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolationException;

public abstract class Generic<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    @PersistenceContext(unitName = "bichosPU")
    private EntityManager em;
    private Class<T> type;
    private Class<?> auxType;

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

    public int count(String namedQueryName) {
	   return ((Number) em.createNamedQuery(namedQueryName).getSingleResult()).intValue();
    }

    public T find(Object id) {
	   return this.em.find(this.type, id);
    }

    public T findById(Object id) {
	   return this.em.getReference(this.type, id);
    }

    public T findSingleByQuery(String namedQueryName) {
	   return (T) this.em.createNamedQuery(namedQueryName).getSingleResult();
    }

    public T findSingleBySQL(String sql) {
	   return (T) this.em.createNativeQuery(sql, type).getSingleResult();
    }

    public T findSingleByParams(String namedQuery, Map<String, Object> parameters) {
	   T result = null;
	   try {
		  Query query = em.createNamedQuery(namedQuery);
		  if (parameters != null && !parameters.isEmpty()) {
			 Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
			 for (Map.Entry<String, Object> entry : rawParameters) {
				query.setParameter(entry.getKey(), entry.getValue());
			 }
		  }
		  result = (T) query.getSingleResult();
		  em.refresh(result);
	   } catch (Exception e) {

	   }
	   return result;
    }

    public List<T> findListByQuery(String namedQueryName) {
	   return this.em.createNamedQuery(namedQueryName).getResultList();
    }

    public List<T> findListByQuery(String namedQueryName, Map parameters) {
	   return findListByQuery(namedQueryName, parameters, 0);
    }

    public List<T> findListByQuery(String queryName, int resultLimit) {
	   return this.em.createNamedQuery(queryName).setMaxResults(resultLimit).getResultList();
    }

    public List<T> findListBySQL(String sql) {
	   return this.em.createNativeQuery(sql, type).getResultList();
    }

    public List<T> findListByQuery(String namedQueryName, Map parameters, int resultLimit) {
	   Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
	   Query query = this.em.createNamedQuery(namedQueryName);
	   if (resultLimit > 0) {
		  query.setMaxResults(resultLimit);
	   }
	   for (Map.Entry<String, Object> entry : rawParameters) {
		  query.setParameter(entry.getKey(), entry.getValue());
	   }
	   return query.getResultList();
    }

    public List<T> findListByQuery(String namedQueryName, int start, int end) {
	   Query query = this.em.createNamedQuery(namedQueryName);
	   query.setMaxResults(end - start);
	   query.setFirstResult(start);
	   return query.getResultList();
    }

    public List<T> findListByParams(String namedQuery, Map<String, Object> parameters) {
	   List<T> result = null;
	   try {
		  Query query = em.createNamedQuery(namedQuery);
		  if (parameters != null && !parameters.isEmpty()) {
			 Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
			 for (Map.Entry<String, Object> entry : rawParameters) {
				query.setParameter(entry.getKey(), entry.getValue());
			 }
		  }
		  result = query.getResultList();
		  em.refresh(result);
	   } catch (Exception e) {

	   }
	   return result;
    }

    public List<?> findListByQuery(String queryName, Class<?> type) {
	   return this.em.createNamedQuery(queryName, type).getResultList();
    }

    public List<T> listAll() {
	   return findListByQuery(type.getSimpleName() + ".findAll");
    }

    public void joinTransaction() {
	   em.joinTransaction();
    }
}
