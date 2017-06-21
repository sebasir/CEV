package net.hpclab.cev.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DataBaseService<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String SELECT = "SELECT";
    private static final Logger LOGGER = Logger.getLogger(DataBaseService.class.getSimpleName());
    private static int currentPage;
    private static int numberOfResults;
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private int queryMaxResults;
    protected Class<T> entityClass;

    public DataBaseService(Class<T> entityClass, int queryMaxResults) throws PersistenceException, Exception {
        this.entityClass = entityClass;
        this.queryMaxResults = queryMaxResults;
        numberOfResults = 0;
        getEntityManager();
        if (entityManagerFactory != null) {
            LOGGER.log(Level.INFO, "DataBaseService ya había iniciado...");
        }
    }

    public DataBaseService(Class<T> entityClass) throws PersistenceException, Exception {
        this(entityClass, Constant.QUERY_MAX_RESULTS);
    }

    public List<T> getList() throws NoResultException, Exception {
        return getList(new HashMap<String, Object>());
    }

    public List<T> getList(int page) throws NoResultException, Exception {
        currentPage = page;
        return getList(new HashMap<String, Object>());
    }

    public List<T> getList(String query) throws NoResultException, Exception {
        return getList(query, null);
    }

    public List<T> getList(String query, int page) throws NoResultException, Exception {
        currentPage = page;
        return getList(query, null);
    }

    public void getCount() {
        LOGGER.log(Level.INFO, "Counting {0}, OK", entityClass.getSimpleName());
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        countQuery.select(criteriaBuilder.count(countQuery.from(entityClass)));
        numberOfResults = entityManager.createQuery(countQuery).getSingleResult().intValue();
        LOGGER.log(Level.INFO, "Count {0}, OK", entityClass.getSimpleName());
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public int getNumberOfPages() {
        return (int) Math.ceil((double) numberOfResults / queryMaxResults);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public List<T> getList(String query, HashMap<String, Object> params) throws NoResultException, Exception {
        LOGGER.log(Level.INFO, "Listing {0}, params: '{'{1}'}'", new Object[]{entityClass.getSimpleName(), params == null ? "N/A" : params.size()});
        TypedQuery<T> typedQuery = entityManager.createQuery(query, entityClass);
        if (params != null) {
            for (String param : params.keySet()) {
                typedQuery.setParameter(param, params.get(param));
            }
        }
        List<T> result = getListOfResults(typedQuery);
        LOGGER.log(Level.INFO, "Listing {0}, OK", entityClass.getSimpleName());
        return result;
    }

    public List<T> getList(HashMap<String, Object> params) throws NoResultException, Exception {
        LOGGER.log(Level.INFO, "Listing (CriteriaQuery) {0}, params: '{'{1}'}'", new Object[]{entityClass.getSimpleName(), params == null ? "N/A" : params.size()});
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);

        if (params != null && !params.isEmpty()) {
            Predicate predicate = criteriaBuilder.conjunction();
            Predicate auxPredicate;
            Object value;
            Path<T> path;
            String[] attributes;
            for (String param : params.keySet()) {
                value = params.get(param);
                if (value == null) {
                    continue;
                }
                path = null;
                attributes = param.split(Constant.POINT);
                for (String attribute : attributes) {
                    if (path == null) {
                        path = root.get(attribute);
                    } else {
                        path = path.get(attribute);
                    }
                }
                auxPredicate = criteriaBuilder.equal(path, value);
                predicate = criteriaBuilder.and(predicate, auxPredicate);
            }
            criteriaQuery.select(root).where(predicate);
        }
        List<T> result = getListOfResults(entityManager.createQuery(criteriaQuery));
        LOGGER.log(Level.INFO, "Listing {0}, OK", entityClass.getSimpleName());
        return result;
    }

    private List<T> getListOfResults(Query query) {
        if (numberOfResults == 0) {
            getCount();
        }
        if (currentPage > 0) {
            LOGGER.log(Level.INFO, "Page {0} of {1}", new Object[]{currentPage, getNumberOfPages()});
            query.setFirstResult(currentPage - 1);
        }
        return query.setMaxResults(queryMaxResults).getResultList();
    }

    private T getSingleRecord(TypedQuery<T> typedQuery, HashMap<String, Object> params) throws NoResultException, Exception {
        if (params != null) {
            for (String param : params.keySet()) {
                typedQuery.setParameter(param, params.get(param));
            }
        }
        T result = typedQuery.getSingleResult();
        LOGGER.log(Level.INFO, "GetSingleRecord {0}, OK", entityClass.getSimpleName());
        return result;
    }

    public T getSingleRecord(String query, HashMap<String, Object> params) throws NoResultException, Exception {
        LOGGER.log(Level.INFO, "GetSingleRecord {0}, params: '{'{1}'}'", new Object[]{entityClass.getSimpleName(), params == null ? "N/A" : params.size()});
        if (query.toLowerCase().contains(SELECT)) {
            return getSingleRecord(entityManager.createQuery(query, entityClass), params);
        } else {
            return getSingleRecord(entityManager.createNamedQuery(query, entityClass), params);
        }
    }

    public T mergeFromQuery(String queryUpdate, String query, HashMap<String, Object> params) throws NoResultException, Exception {
        entityManager.getTransaction().begin();
        LOGGER.log(Level.INFO, "Merging {0} from Query: {1}", new Object[]{entityClass.getSimpleName(), queryUpdate});
        Query typedQuery = entityManager.createQuery(queryUpdate);
        HashMap<String, Object> nParams = new HashMap<>();
        if (params != null) {
            for (String param : params.keySet()) {
                if (param.contains("new_")) {
                    nParams.put(param.replaceAll("new_", ""), params.get(param));
                }
                typedQuery.setParameter(param, params.get(param));
            }
        }
        if (typedQuery.executeUpdate() == 1) {
            LOGGER.log(Level.INFO, "Merged {0}, OK", entityClass.getSimpleName());
            entityManager.getTransaction().commit();
            return getSingleRecord(query, nParams);
        } else {
            entityManager.getTransaction().rollback();
            throw new PersistenceException("No entities Updated");
        }
    }

    public T merge(T entity) throws Exception {
        entityManager.getTransaction().begin();
        LOGGER.log(Level.INFO, "Merging {0}", entityClass.getSimpleName());
        entity = entityManager.merge(entity);
        LOGGER.log(Level.INFO, "Merged {0}, OK", entityClass.getSimpleName());
        entityManager.getTransaction().commit();
        return entity;
    }

    public T persist(T entity) throws Exception {
        LOGGER.log(Level.INFO, "Persiting {0}", entityClass.getSimpleName());
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entity = entityManager.merge(entity);
        LOGGER.log(Level.INFO, "Persist {0}, OK", entityClass.getSimpleName());
        entityManager.getTransaction().commit();
        return entity;
    }

    public void delete(T entity) throws Exception {
        entityManager.getTransaction().begin();
        LOGGER.log(Level.INFO, "Removing {0}", entityClass.getSimpleName());
        entityManager.remove(entity);
        LOGGER.log(Level.INFO, "Removed {0}, OK", entityClass.getSimpleName());
        entityManager.getTransaction().commit();
    }

    public void disconnect() {
        if (entityManager != null) {
            LOGGER.log(Level.INFO, "Desconectando EntityManager...");
            entityManager.close();
            entityManager = null;
        }
        if (entityManagerFactory != null) {
            LOGGER.log(Level.INFO, "Desconectando EntityManagerFactory...");
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    private void getEntityManager() throws PersistenceException, Exception {
        LOGGER.log(Level.INFO, "Obteniendo EntityManager");
        if (entityManagerFactory == null) {
            LOGGER.log(Level.INFO, "entityManagerFactory is null");
            entityManagerFactory = Persistence.createEntityManagerFactory(Constant.PERSISTENCE_UNIT);
        }
        if (entityManager == null) {
            LOGGER.log(Level.INFO, "entityManager is null");
            entityManager = entityManagerFactory.createEntityManager();
        }
    }

    public boolean isConnected() {
        return entityManagerFactory != null && entityManager != null && entityManager.isOpen();
    }
}
