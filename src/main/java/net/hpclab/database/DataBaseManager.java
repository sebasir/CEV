package net.hpclab.database;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DataBaseManager<T> {

    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    public DataBaseManager() {
        entityManagerFactory = Persistence.createEntityManagerFactory("bichosPU");
        entityManager = entityManagerFactory.createEntityManager();
    }

    public T persist(T entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.refresh(entity);
        entityManager.getTransaction().commit();
        return entity;
    }

    public T merge(T entity) {
        return entityManager.merge(entity);
    }

    public void delete(T entity) {
        entity = entityManager.merge(entity);
        entityManager.remove(entity);
    }

    public T find(Class<T> type, Integer id) {
        return entityManager.find(type, id);
    }

    public T findById(Class<T> type, Integer id) {
        return entityManager.getReference(type, id);
    }

    public List<T> listAll(Class<T> type) {
        return findListByQuery(type.getSimpleName() + ".findAll", type);
    }

    public List<T> findListByQuery(String namedQueryName, Class<T> type) {
        return entityManager.createNamedQuery(namedQueryName, type).getResultList();
    }
}
