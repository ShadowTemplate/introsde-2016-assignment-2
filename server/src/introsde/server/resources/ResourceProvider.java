package introsde.server.resources;

import introsde.server.dao.MeasureTypeDAO;
import introsde.common.to.HealthProfile;
import introsde.common.to.MeasureHistory;
import introsde.common.to.MeasureType;
import introsde.common.to.Person;
import introsde.server.dao.PersonDAO;
import org.glassfish.jersey.internal.util.Producer;


import javax.persistence.NoResultException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceProvider {

    private static Response executeRequest(Producer<Response> producer) {
        try {
            return producer.call();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    public static Response listPeople(String mediaType) {
        Producer<Response> producer = () -> {
            List<Person> peopleTO = PersonDAO.listPeople();
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
        };
        return executeRequest(producer);
    }

    public static Response getPerson(Double personId) {
        Producer<Response> producer = () -> {
            try {
                Person personTO = PersonDAO.getPerson(personId);
                personTO.setMeasureHistory(null);
                return Response.status(Response.Status.OK).entity(personTO).build();
            } catch (NoResultException ex) {
                return Response.status(Response.Status.NOT_FOUND).build();
        }
        };
        return executeRequest(producer);
    }

    public static Response putPerson(Double personId, Person personTO) {
        Producer<Response> producer = () -> {
            Person oldPersonTO;
            Response response;
            try {
                oldPersonTO = PersonDAO.getPerson(personId);
                if (personTO.getFirstname() != null) {
                    oldPersonTO.setFirstname(personTO.getFirstname());
                }
                if (personTO.getLastname() != null) {
                    oldPersonTO.setLastname(personTO.getLastname());
                }
                if (personTO.getBirthdate() != null) {
                    oldPersonTO.setBirthdate(personTO.getBirthdate());
                }
                PersonDAO.updatePerson(personId, oldPersonTO);
                response = Response.status(Response.Status.OK).entity(PersonDAO.getPerson(personId)).build();
            } catch (NoResultException ex) {
                personTO.setMeasureHistory(new MeasureHistory(null, new ArrayList<>()));
                personTO.setHealthProfile(new HealthProfile(null, new HashSet<>()));
                PersonDAO.putPerson(personTO);
                response = Response.status(Response.Status.CREATED).entity(personTO).build();
            }
            return response;
        };
        return executeRequest(producer);
    }

    public static Response postPerson(Person personTO) {
        Producer<Response> producer = () -> {
            personTO.setId(null);
            if (personTO.getMeasureHistory() == null || personTO.getMeasureHistory().getMeasureTypes() == null) {
                personTO.setMeasureHistory(new MeasureHistory(null, new ArrayList<>()));
            }
            if (personTO.getHealthProfile() == null || personTO.getHealthProfile().getMeasureTypes() == null) {
                personTO.setHealthProfile(new HealthProfile(null, new HashSet<>()));
            }
            Person newPersonTO = PersonDAO.putPerson(personTO);
            return Response.status(Response.Status.CREATED).entity(newPersonTO).build();
        };
        return executeRequest(producer);
    }

    public static Response deletePerson(Double personId) {
        Producer<Response> producer = () -> {
            Response response;
            try {
                Person personTO = PersonDAO.getPerson(personId);
                PersonDAO.deletePerson(personTO);
                response = Response.status(Response.Status.OK).build();
            } catch (NoResultException ex) {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
            return response;
        };
        return executeRequest(producer);
    }

    public static Response getMeasureHistory(Double personId, String measureType) {
        Producer<Response> producer = () -> {
            Response response;
            try {
                Person personTO = PersonDAO.getPerson(personId);
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

    public static Response getCurrentMeasure(Double personId, String measureType, Double measureId) {
        Producer<Response> producer = () -> {
            Response response;
            try {
                Person personTO = PersonDAO.getPerson(personId);
                List<MeasureType> measuresList = personTO.getHealthProfile().getMeasureTypes().stream()
                        .filter(m -> m.getMeasure().equals(measureType) && m.getMid().equals(measureId))
                        .collect(Collectors.toList());
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

    public static Response postNewMeasure(Double personId, String measureType, MeasureType measure) {
        Producer<Response> producer = () -> {
            Response response;
            try {
                measure.setMid(null);
                measure.setMeasure(measureType);
                MeasureType newMeasure = PersonDAO.addMeasure(personId, measure);
                response = Response.status(Response.Status.OK).entity(newMeasure).build();
            } catch (NoResultException | NullPointerException ex) {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
            return response;
        };
        return executeRequest(producer);
    }

    public static Response listMeasureTypes(String mediaType) {
        Producer<Response> producer = () -> {
            Response response;
            try {
                List<String> types = MeasureTypeDAO.listMeasure();

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
}
