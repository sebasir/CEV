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
	   } catch (Exception e) {

	   }
	   return t;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public T merge(T entity) {
	   try {
		  em.merge(entity);
	   } catch (Exception e) {

	   }
	   return entity;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean delete(T entity) {
	   boolean success = false;
	   try {
		  entity = em.merge(entity);
		  em.remove(entity);
		  success = true;
	   } catch (Exception e) {

	   }
	   return success;
    }

    public int count(String namedQueryName) {
	   int count = 0;
	   try {
		  count = ((Number) em.createNamedQuery(namedQueryName).getSingleResult()).intValue();
	   } catch (Exception e) {

	   }
	   return count;
    }

    public T find(Object id) {
	   T found = null;
	   try {
		  found = this.em.find(this.type, id);
	   } catch (Exception e) {

	   }
	   return found;
    }

    public T findById(Object id) {
	   T found = null;
	   try {
		  found = this.em.getReference(this.type, id);
	   } catch (Exception e) {

	   }
	   return found;
    }

    public T findSingleByQuery(String namedQueryName) {
	   T found = null;
	   try {
		  found = (T) this.em.createNamedQuery(namedQueryName).getSingleResult();
	   } catch (Exception e) {

	   }
	   return found;
    }

    public T findSingleBySQL(String sql) {
	   T found = null;
	   try {
		  found = (T) this.em.createNativeQuery(sql, type).getSingleResult();
	   } catch (Exception e) {

	   }
	   return found;
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
	   List<T> list = null;
	   try {
		  list = this.em.createNamedQuery(namedQueryName).getResultList();
	   } catch (Exception e) {

	   }
	   return list;
    }

    public List<T> findListByQuery(String namedQueryName, Map parameters) {
	   return findListByQuery(namedQueryName, parameters, 0);
    }

    public List<T> findListByQuery(String queryName, int resultLimit) {
	   List<T> list = null;
	   try {
		  list = this.em.createNamedQuery(queryName).setMaxResults(resultLimit).getResultList();
	   } catch (Exception e) {

	   }
	   return list;
    }

    public List<T> findListBySQL(String sql) {
	   List<T> list = null;
	   try {
		  list = this.em.createNativeQuery(sql, type).getResultList();
	   } catch (Exception e) {

	   }
	   return list;
    }

    public List<T> findListByQuery(String namedQueryName, Map parameters, int resultLimit) {
	   List<T> list = null;
	   try {
		  Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
		  Query query = this.em.createNamedQuery(namedQueryName);
		  if (resultLimit > 0) {
			 query.setMaxResults(resultLimit);
		  }
		  for (Map.Entry<String, Object> entry : rawParameters) {
			 query.setParameter(entry.getKey(), entry.getValue());
		  }
		  list = query.getResultList();
	   } catch (Exception e) {

	   }
	   return list;
    }

    public List<T> findListByQuery(String namedQueryName, int start, int end) {
	   List<T> list = null;
	   try {
		  Query query = this.em.createNamedQuery(namedQueryName);
		  query.setMaxResults(end - start);
		  query.setFirstResult(start);
		  list = query.getResultList();
	   } catch (Exception e) {

	   }
	   return list;
    }

    public List<T> findListByParams(String namedQuery, Map<String, Object> parameters) {
	   List<T> list = null;
	   try {
		  Query query = em.createNamedQuery(namedQuery);
		  if (parameters != null && !parameters.isEmpty()) {
			 Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
			 for (Map.Entry<String, Object> entry : rawParameters) {
				query.setParameter(entry.getKey(), entry.getValue());
			 }
		  }
		  list = query.getResultList();
		  em.refresh(list);
	   } catch (Exception e) {

	   }
	   return list;
    }

    public List<?> findListByQuery(String queryName, Class<?> type) {
	   List<?> list = null;
	   try {
		  list = this.em.createNamedQuery(queryName, type).getResultList();
	   } catch (Exception e) {
		  e.printStackTrace();
	   }
	   return list;
    }

    public List<T> listAll() {
	   return findListByQuery(type.getSimpleName() + ".findAll");
    }

    public void joinTransaction() {
	   try {
		  em.joinTransaction();
	   } catch (Exception e) {

	   }
    }
}
