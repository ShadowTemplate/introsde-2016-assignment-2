package introsde.server.resources;

import introsde.common.to.*;
import introsde.server.dao.EntityDAO;
import org.glassfish.jersey.internal.util.Producer;


import javax.persistence.NoResultException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ResourceDispatcher {

    private static Response executeRequest(Producer<Response> producer) {
        try {
            return producer.call();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static Response wrapPeopleList(String mediaType, List<Person> peopleTO) {
        for (Person personTO : peopleTO) {
            personTO.setMeasureHistory(null);
        }
        Object returnValue;
        switch (mediaType) {
            case MediaType.APPLICATION_JSON:
                Map<String, List<Person>> peopleMap = new HashMap<>();
                peopleMap.put("people", peopleTO);
                returnValue = peopleMap;
                break;
            case MediaType.APPLICATION_XML:
                returnValue = new GenericEntity<List<Person>>(peopleTO) {
                };
                break;
            default:
                throw new RuntimeException("Invalid media type: " + mediaType);
        }
        return Response.status(Response.Status.OK).entity(returnValue).build();
    }

    static Response listPeople(String mediaType) {
        return executeRequest(() -> wrapPeopleList(mediaType, EntityDAO.listPeople()));
    }

    static Response listPeopleWithProperty(String mediaType, String measureType, Double min, Double max) {
        Producer<Response> producer = () -> {
            List<Person> peopleTO = EntityDAO.listPeople();
            Predicate<Person> filterByProperty = person -> {
                for (MeasureType measure : person.getHealthProfile().getMeasureTypes()) {
                    if (measure.getMeasure().equals(measureType) && ((min != null && measure.getValue() < min) ||
                            (max != null && measure.getValue() > max))) {
                        return false;
                    }
                }
                return true;
            };
            List<Person> filteredPeopleTO = peopleTO.stream().filter(filterByProperty).collect(Collectors.toList());
            return wrapPeopleList(mediaType, filteredPeopleTO);
        };
        return executeRequest(producer);
    }

    static Response getPerson(Double personId) {
        Producer<Response> producer = () -> {
            try {
                Person personTO = EntityDAO.getPerson(personId);
                personTO.setMeasureHistory(null);
                return Response.status(Response.Status.OK).entity(personTO).build();
            } catch (NoResultException ex) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        };
        return executeRequest(producer);
    }

    static Response putPerson(Double personId, Person personTO) {
        Producer<Response> producer = () -> {
            Person oldPersonTO;
            Response response;
            try {
                oldPersonTO = EntityDAO.getPerson(personId);
                if (personTO.getFirstname() != null) {
                    oldPersonTO.setFirstname(personTO.getFirstname());
                }
                if (personTO.getLastname() != null) {
                    oldPersonTO.setLastname(personTO.getLastname());
                }
                if (personTO.getBirthdate() != null) {
                    oldPersonTO.setBirthdate(personTO.getBirthdate());
                }
                EntityDAO.updatePerson(personId, oldPersonTO);
                response = Response.status(Response.Status.OK).entity(EntityDAO.getPerson(personId)).build();
            } catch (NoResultException ex) {
                personTO.setMeasureHistory(new MeasureHistory(null, new ArrayList<>()));
                personTO.setHealthProfile(new HealthProfile(null, new HashSet<>()));
                EntityDAO.putPerson(personTO);
                response = Response.status(Response.Status.CREATED).entity(personTO).build();
            }
            return response;
        };
        return executeRequest(producer);
    }

    static Response postPerson(Person personTO) {
        Producer<Response> producer = () -> {
            personTO.setId(null);
            if (personTO.getMeasureHistory() == null || personTO.getMeasureHistory().getMeasureTypes() == null) {
                personTO.setMeasureHistory(new MeasureHistory(null, new ArrayList<>()));
            }
            if (personTO.getHealthProfile() == null || personTO.getHealthProfile().getMeasureTypes() == null) {
                personTO.setHealthProfile(new HealthProfile(null, new HashSet<>()));
            }
            Person newPersonTO = EntityDAO.putPerson(personTO);
            return Response.status(Response.Status.CREATED).entity(newPersonTO).build();
        };
        return executeRequest(producer);
    }

    static Response deletePerson(Double personId) {
        Producer<Response> producer = () -> {
            Response response;
            try {
                Person personTO = EntityDAO.getPerson(personId);
                EntityDAO.deletePerson(personTO);
                response = Response.status(Response.Status.OK).build();
            } catch (NoResultException ex) {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
            return response;
        };
        return executeRequest(producer);
    }

    static Response getMeasureHistory(Double personId, String measureType) {
        Producer<Response> producer = () -> {
            Response response;
            try {
                Person personTO = EntityDAO.getPerson(personId);
                List<MeasureType> measuresList = personTO.getMeasureHistory().getMeasureTypes().stream()
                        .filter(m -> m.getMeasure().equals(measureType)).collect(Collectors.toList());
                Object returnValue = new GenericEntity<List<MeasureType>>(measuresList) {
                };
                response = Response.status(Response.Status.OK).entity(returnValue).build();
            } catch (NoResultException | NullPointerException ex) {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
            return response;
        };
        return executeRequest(producer);
    }

    private static Date toDate(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Response getMeasureHistoryInRange(Double personId, String measureType, String before, String after) {
        Producer<Response> producer = () -> {
            Response response;
            try {
                Date beforeDate = toDate(before), afterDate = toDate(after);
                Predicate<MeasureType> filterRange = m -> {
                    Date mDate = toDate(m.getCreated());
                    return m.getMeasure().equals(measureType) && mDate.before(beforeDate) && mDate.after(afterDate);
                };
                Person personTO = EntityDAO.getPerson(personId);
                List<MeasureType> measuresList = personTO.getMeasureHistory().getMeasureTypes().stream()
                        .filter(filterRange).collect(Collectors.toList());
                Object returnValue = new GenericEntity<List<MeasureType>>(measuresList) {
                };
                response = Response.status(Response.Status.OK).entity(returnValue).build();
            } catch (NoResultException | NullPointerException ex) {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
            return response;
        };
        return executeRequest(producer);
    }

    static Response getCurrentMeasure(Double personId, String measureType, Double measureId) {
        Producer<Response> producer = () -> {
            Response response;
            try {
                Person personTO = EntityDAO.getPerson(personId);
                List<MeasureType> measuresList = personTO.getHealthProfile().getMeasureTypes().stream()
                        .filter(m -> m.getMeasure().equals(measureType) && m.getMid().equals(measureId))
                        .collect(Collectors.toList());
                if (measuresList.isEmpty()) {
                    throw new NoResultException();
                } else {
                    Object returnValue = new GenericEntity<MeasureType>(measuresList.get(0)) {
                    };
                    response = Response.status(Response.Status.OK).entity(returnValue).build();
                }
            } catch (NoResultException | NullPointerException ex) {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
            return response;
        };
        return executeRequest(producer);
    }

    static Response postNewMeasure(Double personId, String measureType, MeasureType measure) {
        Producer<Response> producer = () -> {
            Response response;
            try {
                measure.setMid(null);
                measure.setMeasure(measureType);
                MeasureType newMeasure = EntityDAO.addMeasure(personId, measure);
                response = Response.status(Response.Status.OK).entity(newMeasure).build();
            } catch (NoResultException | NullPointerException ex) {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
            return response;
        };
        return executeRequest(producer);
    }

    static Response listMeasureTypes(String mediaType) {
        Producer<Response> producer = () -> {
            Response response;
            try {
                List<String> types = EntityDAO.listMeasure();

                Object returnValue;
                switch (mediaType) {
                    case MediaType.APPLICATION_JSON:
                        Map<String, List<String>> typesMap = new HashMap<>();
                        typesMap.put("measureType", types);
                        returnValue = typesMap;
                        break;
                    case MediaType.APPLICATION_XML:
                        returnValue = new MeasureTypes(types.toArray(new String[types.size()]));
                        break;
                    default:
                        throw new RuntimeException("Invalid media type: " + mediaType);
                }
                response = Response.status(Response.Status.OK).entity(returnValue).build();
            } catch (NoResultException ex) {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
            return response;
        };
        return executeRequest(producer);
    }

    static Response updateMeasureValue(Double personId, String measureType, Double measureId, MeasureType measureTO) {
        Producer<Response> producer = () -> {
            Response response;
            try {
                measureTO.setMid(measureId);
                measureTO.setMeasure(measureType);
                EntityDAO.updateMeasureType(measureId, measureTO);
                return Response.status(Response.Status.OK).build();
            } catch (NoResultException | NullPointerException ex) {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
            return response;
        };
        return executeRequest(producer);
    }

    static Response initDatabase() {
        Producer<Response> producer = () -> {
            EntityDAO.initDatabase();
            return Response.status(Response.Status.OK).build();
        };
        return executeRequest(producer);
    }
}
