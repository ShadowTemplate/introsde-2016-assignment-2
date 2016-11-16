package introsde.server.util;

import javax.persistence.*;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public enum PersistenceManager {
    instance;

    private static final String PERSISTENCE_UNIT_NAME = "introsde-jpa";

    private EntityManagerFactory entityManagerFactory;

    PersistenceManager() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
        entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    public <T> void persist(T object) {
        runInTransaction(entityManager -> {
            System.out.println("\nGoing to persist: " + object + "\n");
            entityManager.persist(object);
        });
    }

    public <T> void remove(T object) {
        runInTransaction(entityManager -> {
            System.out.println("\nGoing to remove: " + object + "\n");
            T mergedObject = entityManager.merge(object);
            entityManager.remove(mergedObject);
        });
    }

    public Query createQuery(String query) { //TODO TRY TO REMOVE
        return runInTransaction(entityManager -> {
            System.out.println("\nGoing to run query: " + query + "\n");
            return entityManager.createQuery(query);
        });
    }

    public Integer updateQuery(String query, Map<String, Object> parameters) {
        return runInTransaction(entityManager -> {
            System.out.println("\nGoing to run query: " + query+ "\n params: " + parameters + "\n");
            Query q = entityManager.createQuery(query);
            for (Map.Entry<String, Object> param : parameters.entrySet()) {
                q.setParameter(param.getKey(), param.getValue());
            }
            return q.executeUpdate();
        });
    }

    public void runInTransaction(Consumer<EntityManager> function) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        function.accept(entityManager);
        transaction.commit();
    }

    public <T> T runInTransaction(Function<EntityManager, T> function) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        T result = function.apply(entityManager);
        transaction.commit();
        return result;
    }
}
