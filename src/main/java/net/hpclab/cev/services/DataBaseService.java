package net.hpclab.cev.services;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DataBaseService<T> implements Serializable {

    private static enum QueryMethod {
        MAP, ENTITY, NAMED_QUERY, QUERY_MAP
    };

    private static final long serialVersionUID = 1L;
    private static final String SELECT = "SELECT";
    private static final Logger LOGGER = Logger.getLogger(DataBaseService.class.getSimpleName());
    private Pager pager;
    private QueryMethod queryMethod;

    @PersistenceUnit(unitName = Constant.PERSISTENCE_UNIT)
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;
    private int currentPage;
    private int numberOfResults;
    private int queryMaxResults;
    private T entityParam;
    private HashMap<String, Object> mapParam;
    private String queryParam;
    protected Class<T> entityClass;

    public DataBaseService(Class<T> entityClass, int queryMaxResults) throws PersistenceException, Exception {
        this.entityClass = entityClass;
        this.queryMaxResults = queryMaxResults;
        numberOfResults = 0;
        this.pager = new Pager();
        getEntityManager();
    }

    public DataBaseService(Class<T> entityClass) throws PersistenceException, Exception {
        this(entityClass, Constant.QUERY_MAX_RESULTS);
    }

    public List<T> getList() throws NoResultException, Exception {
        if (queryMethod == null) {
            queryMethod = QueryMethod.MAP;
        }
        switch (queryMethod) {
            case MAP:
                return getList(mapParam);
            case ENTITY:
                return getList(entityParam);
            case NAMED_QUERY:
                return getList(queryParam);
            case QUERY_MAP:
                return getList(queryParam, mapParam);
            default:
                return getList(new HashMap<String, Object>());
        }
    }

    public List<T> getList(int page) throws NoResultException, Exception {
        currentPage = page;
        return getList();
    }

    public List<T> getList(T entityFilters) throws NoResultException, Exception {
        restartFilters(QueryMethod.ENTITY);
        entityParam = entityFilters;
        HashMap<String, Object> filters = new HashMap<>();
        if (entityFilters != null) {
            Class<?> cls = entityFilters.getClass();
            Field[] fields = cls.getDeclaredFields();
            Object value;
            for (Field field : fields) {
                field.setAccessible(true);
                value = field.get(entityFilters);
                if (!field.getName().equals("serialVersionUID") && value != null) {
                    if (value.getClass().getName().contains("hpclab")) {
                        value = getInnerValue(value);
                    }
                    filters.put(field.getName(), value);
                }
            }
        }
        LOGGER.log(Level.SEVERE, "Filters: {0}", filters);
        return getList(filters);
    }

    public Object getInnerValue(Object obj) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType().getSimpleName().equals("Integer")) {
                return field.get(obj);
            }
        }
        return null;
    }

    public List<T> getList(String query) throws NoResultException, Exception {
        restartFilters(QueryMethod.NAMED_QUERY);
        queryParam = query;
        mapParam = null;
        return getList(query, null);
    }

    public List<T> getList(String query, HashMap<String, Object> params) throws NoResultException, Exception {
        restartFilters(QueryMethod.QUERY_MAP);
        queryParam = query;
        mapParam = params;
        LOGGER.log(Level.INFO, "Listing {0}, params: '{'{1}'}'", new Object[]{entityClass.getSimpleName(), params == null ? "N/A" : params.size()});
        TypedQuery<T> finalQuery;
        if (query.toLowerCase().contains(SELECT)) {
            finalQuery = entityManager.createQuery(query, entityClass);
        } else {
            finalQuery = entityManager.createNamedQuery(query, entityClass);
        }
        if (params != null) {
            for (String param : params.keySet()) {
                finalQuery.setParameter(param, params.get(param));
            }
        }
        List<T> result = getListOfResults(finalQuery);
        LOGGER.log(Level.INFO, "Listing {0}, OK", entityClass.getSimpleName());
        return result;
    }

    public List<T> getList(HashMap<String, Object> params) throws NoResultException, Exception {
        restartFilters(QueryMethod.MAP);
        mapParam = params;
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
                if (value == null || (value instanceof String && value.toString().isEmpty())) {
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

    public void getCount() {
        LOGGER.log(Level.INFO, "Counting {0}, OK", entityClass.getSimpleName());
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> root = countQuery.from(entityClass);
        countQuery.select(criteriaBuilder.count(root));
        if (mapParam != null && !mapParam.isEmpty()) {
            Predicate predicate = criteriaBuilder.conjunction();
            Predicate auxPredicate;
            Object value;
            Path<T> path;
            String[] attributes;
            for (String param : mapParam.keySet()) {
                value = mapParam.get(param);
                if (value == null || (value instanceof String && value.toString().isEmpty())) {
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
            countQuery.select(criteriaBuilder.count(root)).where(predicate);
        } else {
            countQuery.select(criteriaBuilder.count(root));
        }
        numberOfResults = entityManager.createQuery(countQuery).getSingleResult().intValue();
        LOGGER.log(Level.INFO, "Count {0}, OK", entityClass.getSimpleName());
    }

    public int getActualNumberOfResults() {
        return numberOfResults;
    }

    public int getNumberOfPages() {
        return queryMaxResults > 0 ? (int) Math.ceil((double) numberOfResults / queryMaxResults) : 1;
    }

    public int getActualCurrentPage() {
        return currentPage;
    }

    private List<T> getListOfResults(TypedQuery<T> query) {
        getCount();
        if (currentPage > 0) {
            LOGGER.log(Level.INFO, "Page {0} of {1}", new Object[]{currentPage, getNumberOfPages()});
            query.setFirstResult(currentPage - 1);
        }
        return queryMaxResults > 0 ? query.setMaxResults(queryMaxResults).getResultList() : query.getResultList();
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
            return getSingleRecord(query, nParams);
        } else {
            throw new PersistenceException("No entities Updated");
        }
    }

    public T merge(T entity) throws Exception {
        LOGGER.log(Level.INFO, "Merging {0}", entityClass.getSimpleName());
        entity = entityManager.merge(entity);
        LOGGER.log(Level.INFO, "Merged {0}, OK", entityClass.getSimpleName());
        return entity;
    }

    public T persist(T entity) throws Exception {
        LOGGER.log(Level.INFO, "Persiting {0}", entityClass.getSimpleName());
        entityManager.persist(entity);
        entity = entityManager.merge(entity);
        LOGGER.log(Level.INFO, "Persist {0}, OK", entityClass.getSimpleName());
        return entity;
    }

    public void delete(T entity) throws Exception {
        LOGGER.log(Level.INFO, "Removing {0}", entityClass.getSimpleName());
        entityManager.remove(entity);
        LOGGER.log(Level.INFO, "Removed {0}, OK", entityClass.getSimpleName());
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
        } else {
            LOGGER.log(Level.INFO, "entityManagerFactory is injected!");
        }
        if (entityManager == null) {
            LOGGER.log(Level.INFO, "entityManager is null");
            entityManager = entityManagerFactory.createEntityManager();
        }
    }

    public boolean isConnected() {
        return entityManagerFactory != null && entityManager != null && entityManager.isOpen();
    }

    private void restartFilters(QueryMethod queryMethod) {
        if (this.queryMethod != queryMethod) {
            this.queryMethod = queryMethod;
            currentPage = 1;
            entityParam = null;
            queryParam = null;
            mapParam = null;
        }
    }

    public Pager getPager() {
        return pager;
    }

    public class Pager {

        public void firstPage() throws Exception {
            getPageResults(1);
        }

        public void lastPage() throws Exception {
            getPageResults(getNumberOfPages());
        }

        public void nextPage() throws Exception {
            if (getCurrentPage() + 1 <= getNumberOfPages()) {
                getPageResults(getCurrentPage() + 1);
            }
        }

        public void previousPage() throws Exception {
            if (getCurrentPage() - 1 > 0) {
                getPageResults(getCurrentPage() - 1);
            }
        }

        public List<T> getPageResults(int page) throws Exception {
            return getList(page);
        }

        public List<Integer> getPages() {
            ArrayList<Integer> pages = new ArrayList<>();
            int bottomIndex = getCurrentPage() - (Constant.MAX_PAGE_INDEX / 2);
            bottomIndex = bottomIndex <= 0 ? 1 : bottomIndex;
            int topIndex = bottomIndex + Constant.MAX_PAGE_INDEX;
            topIndex = topIndex > getNumberOfPages() ? getNumberOfPages() : topIndex;
            for (int i = bottomIndex; i <= topIndex; i++) {
                pages.add(i);
            }
            return pages;
        }

        public int getCurrentPage() {
            return getActualCurrentPage();
        }

        public int getNumberOfResults() {
            return getActualNumberOfResults();
        }
    }
}
