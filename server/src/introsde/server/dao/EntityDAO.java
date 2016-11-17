package introsde.server.dao;

import introsde.common.to.MeasureType;
import introsde.common.to.Person;
import introsde.server.persistence.PersistenceManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EntityDAO {

    public static List<Person> listPeople() {
        String query = "SELECT p FROM Person p";
        List<introsde.server.model.Person> people = PersistenceManager.instance.listResultQuery(query);
        return people.stream().map(introsde.server.model.Person::buildTO).collect(Collectors.toList());
    }

    public static Person getPerson(Double personId) {
        String query = "SELECT p FROM Person p WHERE p.id=:arg1";
        Map<String, Object> params = new HashMap<>();
        params.put("arg1", personId);
        return ((introsde.server.model.Person) PersistenceManager.instance.singleResultQuery(query, params)).buildTO();
    }

    public static Person putPerson(Person personTO) {
        introsde.server.model.Person person = new introsde.server.model.Person(personTO);
        PersistenceManager.instance.persist(person);
        return person.buildTO();
    }

    public static void deletePerson(Person personTO) {
        introsde.server.model.Person person = new introsde.server.model.Person(personTO);
        PersistenceManager.instance.remove(person);
    }

    public static void updatePerson(Double personId, Person oldPersonTO) {
        String query = "UPDATE Person p SET p.firstname = :arg1, p.lastname = :arg2, p.birthdate = :arg3 WHERE p.id = :arg4";
        Map<String, Object> params = new HashMap<>();
        params.put("arg1", oldPersonTO.getFirstname());
        params.put("arg2", oldPersonTO.getLastname());
        params.put("arg3", oldPersonTO.getBirthdate());
        params.put("arg4", personId);
        PersistenceManager.instance.updateQuery(query, params);
    }

    public static MeasureType addMeasure(Double personId, MeasureType measureType) {
        return PersistenceManager.instance.runViaProxy(entityManagerProxy -> {
            String query = "SELECT p FROM Person p WHERE p.id=:arg1";
            Map<String, Object> params = new HashMap<>();
            params.put("arg1", personId);
            introsde.server.model.Person person = (introsde.server.model.Person) entityManagerProxy.singleResultQuery(query, params);

            Predicate<? super introsde.server.model.MeasureType> sameMeasure =
                    (Predicate<introsde.server.model.MeasureType>) measure ->
                            measure.getMeasure().equals(measureType.getMeasure());

            Set<introsde.server.model.MeasureType> currentMeasures = person.getHealthProfile().getMeasureTypes();
            introsde.server.model.MeasureType oldMeasure = currentMeasures.stream().filter(sameMeasure).findFirst()
                    .orElse(null);
            if (oldMeasure != null) {
                currentMeasures.remove(oldMeasure);
                person.getMeasureHistory().getMeasureTypes().add(oldMeasure);
            }

            currentMeasures.add(new introsde.server.model.MeasureType(measureType));
            entityManagerProxy.persist(person);
            return person.getHealthProfile().getMeasureTypes().stream().filter(sameMeasure).findFirst().get().buildTO();
        });
    }

    public static void updateMeasureType(Double measureId, MeasureType measureTO) {
        String query = "UPDATE MeasureType m SET m.value = :arg1, m.created = :arg2 WHERE m.mid = :arg3";
        Map<String, Object> params = new HashMap<>();
        params.put("arg1", measureTO.getValue());
        params.put("arg2", measureTO.getCreated());
        params.put("arg3", measureId);
        PersistenceManager.instance.updateQuery(query, params);
    }

    public static List<String> listMeasure() {
        String query = "SELECT DISTINCT t.measure FROM MeasureType t";
        return PersistenceManager.instance.listResultQuery(query);
    }

    public static void initDatabase() {
        PersistenceManager.instance.updateQuery("DELETE FROM HealthProfile p");
        PersistenceManager.instance.updateQuery("DELETE FROM MeasureHistory h");
        PersistenceManager.instance.updateQuery("DELETE FROM MeasureType m");
        PersistenceManager.instance.updateQuery("DELETE FROM Person p");
    }
}
