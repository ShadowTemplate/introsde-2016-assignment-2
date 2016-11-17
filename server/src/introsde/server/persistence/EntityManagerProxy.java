package introsde.server.persistence;

import javax.persistence.EntityManager;
import java.util.Map;

public class EntityManagerProxy {

    private EntityManager entityManager;

    EntityManagerProxy(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void persist(Object object) {
        PersistenceManager.instance.persist(entityManager, object);
    }

    public Object singleResultQuery(String query, Map<String, Object> parameters) {
        return PersistenceManager.instance.singleResultQuery(entityManager, query, parameters);
    }

}
