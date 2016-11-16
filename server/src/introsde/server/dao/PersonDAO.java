package introsde.server.dao;

import introsde.common.to.MeasureType;
import introsde.common.to.Person;
import introsde.server.util.PersistenceManager;

import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PersonDAO {

    public static List<Person> listPeople() {
        List<introsde.server.model.Person> people = PersistenceManager.instance.createQuery("SELECT p FROM Person p").getResultList();
        return people.stream().map(introsde.server.model.Person::buildTO).collect(Collectors.toList());
    }

    public static Person getPerson(Double personId) {
        Query query = PersistenceManager.instance.createQuery("SELECT p FROM Person p WHERE p.id=:arg1");
        query.setParameter("arg1", personId);
        introsde.server.model.Person person = (introsde.server.model.Person) query.getSingleResult();
        return person.buildTO();
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
        Map<String, Object> params = new HashMap<>();
        params.put("arg1", oldPersonTO.getFirstname());
        params.put("arg2", oldPersonTO.getLastname());
        params.put("arg3", oldPersonTO.getBirthdate());
        params.put("arg4", personId);
        PersistenceManager.instance.updateQuery("UPDATE Person p SET " +
                "p.firstname = :arg1, " +
                "p.lastname = :arg2, " +
                "p.birthdate = :arg3 " +
                "WHERE p.id = :arg4", params);
    }

    public static MeasureType addMeasure(Double personId, MeasureType measureType) {
        return PersistenceManager.instance.runInTransaction(entityManager -> {
            Query query = entityManager.createQuery("SELECT p FROM Person p WHERE p.id=:arg1");
            query.setParameter("arg1", personId);
            introsde.server.model.Person person = (introsde.server.model.Person) query.getSingleResult();

            System.out.println("\nAt first person: \n" + person);
            System.out.println("HP");
            for (introsde.server.model.MeasureType type : person.getHealthProfile().getMeasureTypes()) {
                System.out.println(type);
            }
            System.out.println("History");
            for (introsde.server.model.MeasureType type : person.getMeasureHistory().getMeasureTypes()) {
                System.out.println(type);
            }

            Predicate<? super introsde.server.model.MeasureType> sameMeasure =
                    (Predicate<introsde.server.model.MeasureType>) measure ->
                            measure.getMeasure().equals(measureType.getMeasure());

            Set<introsde.server.model.MeasureType> currentMeasures = person.getHealthProfile().getMeasureTypes();
            introsde.server.model.MeasureType oldMeasure = currentMeasures.stream().filter(sameMeasure).findFirst()
                    .orElse(null);
            if (oldMeasure != null) {
                currentMeasures.remove(oldMeasure);
                //oldMeasure.setMid(null);
                person.getMeasureHistory().getMeasureTypes().add(oldMeasure);
            }

            currentMeasures.add(new introsde.server.model.MeasureType(measureType));

            System.out.println("\nGoing to persist: \n" + person);
            for (introsde.server.model.MeasureType type : person.getHealthProfile().getMeasureTypes()) {
                System.out.println(type);
            }
            System.out.println("History");
            for (introsde.server.model.MeasureType type : person.getMeasureHistory().getMeasureTypes()) {
                System.out.println(type);
            }

            entityManager.persist(person);
            return person.getHealthProfile().getMeasureTypes().stream().filter(sameMeasure).findFirst().get().buildTO();
        });
    }

    public static void updateMeasureType(Double measureId, MeasureType measureTO) {
        Map<String, Object> params = new HashMap<>();
        params.put("arg1", measureTO.getValue());
        params.put("arg2", measureTO.getCreated());
        params.put("arg3", measureId);
        PersistenceManager.instance.updateQuery("UPDATE MeasureType m SET " +
                "m.value = :arg1, " +
                "m.created = :arg2 " +
                "WHERE m.mid = :arg3", params);
    }

    public static void resetDB() {
        PersistenceManager.instance.updateQuery("DELETE FROM HealthProfile p", new HashMap<>());
        PersistenceManager.instance.updateQuery("DELETE FROM MeasureHistory h", new HashMap<>());
        PersistenceManager.instance.updateQuery("DELETE FROM MeasureType m", new HashMap<>());
        PersistenceManager.instance.updateQuery("DELETE FROM Person p", new HashMap<>());
    }
}
