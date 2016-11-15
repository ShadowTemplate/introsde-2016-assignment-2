package introsde.server.dao;

import introsde.server.util.PersistenceManager;

import java.util.List;

public class MeasureTypeDAO {

    public static List<String> listMeasure() {
        return PersistenceManager.instance.createQuery("SELECT DISTINCT t.measure FROM MeasureType t").getResultList();
    }
}
