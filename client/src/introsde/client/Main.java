package introsde.client;

import introsde.common.to.HealthProfile;
import introsde.common.to.MeasureType;
import introsde.common.to.MeasureTypes;
import introsde.common.to.Person;
import org.glassfish.jersey.client.ClientConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBException;

public class Main {

    //private static final String ENDPOINT = "http://localhost:8080/";
    private static final String ENDPOINT = "https://introsde-a2-server.herokuapp.com/";

    private static final URI SERVER_URI = UriBuilder.fromUri(ENDPOINT).build();
    private static final WebTarget SERVER = ClientBuilder.newClient(new ClientConfig()).target(SERVER_URI);

    private static final Map<String, Object> SHARED_VALUES = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Server URL: " + ENDPOINT + "\n");
        try (PrintWriter xmlOut = new PrintWriter(new FileOutputStream(new File("client-server-xml.log")));
             PrintWriter jsonOut = new PrintWriter(new FileOutputStream(new File("client-server-json.log")))) {
            initDatabase();
            for (int i = 1; i <= 12; i++) {
                Method method = Main.class.getMethod("request" + i, String.class);
                RequestLog response = (RequestLog) method.invoke(null, MediaType.APPLICATION_XML);
                response.log(xmlOut);
                response = (RequestLog) method.invoke(null, MediaType.APPLICATION_JSON);
                response.log(jsonOut);
            }
            cleanDatabase();
        } catch (Exception ex) {
            System.err.println("An error occurred while executing request.");
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static RequestLog request1(String mediaType) throws JAXBException {
        System.out.println("Executing request1 with " + mediaType);
        String url = "person";
        Response response = SERVER.path(url).request().accept(mediaType).get();
        response.bufferEntity();
        List<Person> people;
        switch (mediaType) {
            case MediaType.APPLICATION_JSON:
                Map<String, List<Person>> peopleMap = response.readEntity(new GenericType<Map<String, List<Person>>>() {
                });
                people = peopleMap.get("people");
                break;
            case MediaType.APPLICATION_XML:
                people = response.readEntity(new GenericType<List<Person>>() {
                });
                break;
            default:
                throw new RuntimeException("Invalid media type: " + mediaType);
        }

        String result;
        if (people.size() >= 2) {
            result = "OK";
            SHARED_VALUES.put("first_person_id", people.get(0).getId());
            SHARED_VALUES.put("last_person_id", people.get(people.size() - 1).getId());
        } else {
            result = "ERROR";
        }
        String body = RequestLog.prettify(mediaType, response.readEntity(String.class));
        RequestLog requestLog = new RequestLog(1, HttpMethod.GET, url, mediaType, mediaType, result,
                response.getStatus(), body);
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request2(String mediaType) {
        System.out.println("Executing request2 with " + mediaType);
        String url = "person/" + SHARED_VALUES.get("first_person_id");
        Response response = SERVER.path(url).request().accept(mediaType).get();
        response.bufferEntity();
        String body = RequestLog.prettify(mediaType, response.readEntity(String.class));
        int status = response.getStatus();
        String result = (status == Response.Status.OK.getStatusCode() ||
                status == Response.Status.ACCEPTED.getStatusCode()) ? "OK" : "ERROR";
        RequestLog requestLog = new RequestLog(2, HttpMethod.GET, url, mediaType, mediaType, result,
                status, body);
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request3(String mediaType) {
        System.out.println("Executing request3 with " + mediaType);
        String url = "person/" + SHARED_VALUES.get("first_person_id");
        Person newPerson = new Person();
        newPerson.setId((Double) SHARED_VALUES.get("first_person_id"));
        newPerson.setFirstname("NewPersonName");
        Entity<Person> entity = Entity.entity(newPerson, mediaType);
        Response response = SERVER.path(url).request().accept(mediaType).header(HttpHeaders.CONTENT_TYPE, mediaType).put(entity);
        response.bufferEntity();
        String body = RequestLog.prettify(mediaType, response.readEntity(String.class));
        Person updatedPerson = response.readEntity(Person.class);
        String result = newPerson.getFirstname().equals(updatedPerson.getFirstname()) ? "OK" : "ERROR";
        RequestLog requestLog = new RequestLog(3, HttpMethod.PUT, url, mediaType, mediaType, result,
                response.getStatus(), body);
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request4(String mediaType) {
        System.out.println("Executing request4 with " + mediaType);
        String url = "person";

        Person person = new Person();
        person.setFirstname("Chuck");
        person.setLastname("Norris");
        person.setBirthdate("1945-01-01");
        HealthProfile healthProfile = new HealthProfile();
        Set<MeasureType> measures = new HashSet<>();
        measures.add(new MeasureType(null, "weight", 78.9, null));
        measures.add(new MeasureType(null, "height", 172d, null));
        healthProfile.setMeasureTypes(measures);
        person.setHealthProfile(healthProfile);

        Entity<Person> entity = Entity.entity(person, mediaType);
        Response response = SERVER.path(url).request().accept(mediaType).header(HttpHeaders.CONTENT_TYPE, mediaType).post(entity);
        response.bufferEntity();
        String body = RequestLog.prettify(mediaType, response.readEntity(String.class));
        Person newPerson = response.readEntity(Person.class);
        SHARED_VALUES.put("new_person_id" + mediaType, newPerson.getId());
        int status = response.getStatus();
        String result = ((status == Response.Status.OK.getStatusCode() ||
                status == Response.Status.CREATED.getStatusCode() || status == Response.Status.ACCEPTED.getStatusCode())
                && newPerson.getId() != null) ? "OK" : "ERROR";
        RequestLog requestLog = new RequestLog(4, HttpMethod.POST, url, mediaType, mediaType, result,
                status, body);
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request5(String mediaType) {
        System.out.println("Executing request5 with " + mediaType);
        String url = "person/" + SHARED_VALUES.get("new_person_id" + mediaType);
        Response response = SERVER.path(url).request().accept(mediaType).delete();
        response.bufferEntity();
        response = SERVER.path(url).request().accept(mediaType).get();
        response.bufferEntity();
        String result = response.getStatus() == Response.Status.NOT_FOUND.getStatusCode() ? "OK" : "ERROR";
        RequestLog requestLog = new RequestLog(5, HttpMethod.DELETE, url, mediaType, mediaType, result,
                response.getStatus(), "");
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request6(String mediaType) {
        System.out.println("Executing request6 with " + mediaType);
        String url = "/measureTypes";
        Response response = SERVER.path(url).request().accept(mediaType).get();
        response.bufferEntity();
        List<String> measures;
        switch (mediaType) {
            case MediaType.APPLICATION_JSON:
                Map<String, List<String>> typesMap = response.readEntity(new GenericType<Map<String, List<String>>>() {
                });
                measures = typesMap.get("measureType");
                break;
            case MediaType.APPLICATION_XML:
                MeasureTypes measureTypes = response.readEntity(MeasureTypes.class);
                measures = Arrays.asList(measureTypes.getMeasureType());
                break;
            default:
                throw new RuntimeException("Invalid media type: " + mediaType);
        }
        SHARED_VALUES.put("measure_types", measures);
        String result = measures.size() >= 2 ? "OK" : "SUCCESS";
        String body = RequestLog.prettify(mediaType, response.readEntity(String.class));
        RequestLog requestLog = new RequestLog(6, HttpMethod.GET, url, mediaType, mediaType, result,
                response.getStatus(), body);
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request7(String mediaType) {
        System.out.println("Executing request7 with " + mediaType);
        List<Double> peopleIds = new ArrayList<>();
        peopleIds.add((Double) SHARED_VALUES.get("first_person_id"));
        peopleIds.add((Double) SHARED_VALUES.get("last_person_id"));
        String result = "ERROR";
        String url = "";
        int status = 0;
        String body = "";
        for (Double personId : peopleIds) {
            for (String measure : (List<String>) SHARED_VALUES.get("measure_types")) {
                url = "person/" + personId + "/" + measure;
                Response response = SERVER.path(url).request().accept(mediaType).get();
                response.bufferEntity();
                status = response.getStatus();
                body = RequestLog.prettify(mediaType, response.readEntity(String.class));
                System.out.println(body);
                List<MeasureType> measuresList = response.readEntity(new GenericType<List<MeasureType>>() {
                });
                result = measuresList.isEmpty() ? result : "OK";
                SHARED_VALUES.put("measured_person_id", personId);
                SHARED_VALUES.put("measure_id", measuresList.get(0).getMid());
                SHARED_VALUES.put("measure_type", measuresList.get(0).getMeasure());
            }
        }
        RequestLog requestLog = new RequestLog(7, HttpMethod.GET, url, mediaType, mediaType, result, status,
                body);
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request8(String mediaType) {
        System.out.println("Executing request8 with " + mediaType);
        String url = "person/" + SHARED_VALUES.get("measured_person_id") + "/" + SHARED_VALUES.get("measure_type") +
                "/" + SHARED_VALUES.get("measure_id");
        Response response = SERVER.path(url).request().accept(mediaType).get();
        response.bufferEntity();
        int status = response.getStatus();
        String body = status == Response.Status.NOT_FOUND.getStatusCode() ? "" : RequestLog.prettify(mediaType, response.readEntity(String.class));
        String result = status == Response.Status.OK.getStatusCode() ? "OK" : "ERROR";
        RequestLog requestLog = new RequestLog(8, HttpMethod.GET, url, mediaType, mediaType, result, status, body);
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request9(String mediaType) {
        System.out.println("Executing request9 with " + mediaType);

        String measureType = ((List<String>) SHARED_VALUES.get("measure_types")).get(0);
        String url = "person/" + SHARED_VALUES.get("first_person_id") + "/" + measureType;
        Response response = SERVER.path(url).request().accept(mediaType).get();
        response.bufferEntity();
        List<MeasureType> measureTypes = response.readEntity(new GenericType<List<MeasureType>>() {
        });

        MeasureType newMeasure = new MeasureType(null, null, 72d, "2011-12-09");
        Entity<MeasureType> entity = Entity.entity(newMeasure, mediaType);
        response = SERVER.path(url).request().accept(mediaType).header(HttpHeaders.CONTENT_TYPE, mediaType).post(entity);
        response.bufferEntity();
        int status = response.getStatus();
        String body = RequestLog.prettify(mediaType, response.readEntity(String.class));

        response = SERVER.path(url).request().accept(mediaType).get();
        response.bufferEntity();
        List<MeasureType> newMeasureTypes = response.readEntity(new GenericType<List<MeasureType>>() {
        });

        String result = measureTypes.size() + 1 == newMeasureTypes.size() ? "OK" : "ERROR";
        RequestLog requestLog = new RequestLog(9, HttpMethod.POST, url, mediaType, mediaType, result, status, body);
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request10(String mediaType) {
        System.out.println("Executing request10 with " + mediaType);
        RequestLog requestLog = new RequestLog();
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request11(String mediaType) {
        System.out.println("Executing request11 with " + mediaType);
        RequestLog requestLog = new RequestLog();
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request12(String mediaType) {
        System.out.println("Executing request12 with " + mediaType);
        RequestLog requestLog = new RequestLog();
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    private static void initDatabase() {
        String mediaType = MediaType.APPLICATION_JSON;
        System.out.println("Going to init database...");

        List<Person> newPeople = new ArrayList<>();
        newPeople.add(new Person(null, "Alan", "Turing", "1912-06-23", null, null));
        newPeople.add(new Person(null, "Christopher", "McCandless", "1968-02-12", null, null));
        for (Person person : newPeople) {
            Entity<Person> entity = Entity.entity(person, mediaType);
            Response response = SERVER.path("person").request().accept(mediaType).header(HttpHeaders.CONTENT_TYPE, mediaType).post(entity);
            response.bufferEntity();
        }

        Response response = SERVER.path("person").request().accept(mediaType).get();
        response.bufferEntity();
        System.out.println(RequestLog.prettify(mediaType, response.readEntity(String.class)));
        Map<String, List<Person>> peopleMap = response.readEntity(new GenericType<Map<String, List<Person>>>() {
        });

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        for (Person person : peopleMap.get("people")) {
            Double id = person.getId();
            for(int i = 0; i < 3; i++) {
                Entity<MeasureType> entity = Entity.entity(new MeasureType(null, null, 70d + i, date), mediaType);
                response = SERVER.path("person/" + id + "/weight").request().accept(mediaType).post(entity);
                response.bufferEntity();
                entity = Entity.entity(new MeasureType(null, null, 1.8d + (((double) i)/5), date), mediaType);
                response = SERVER.path("person/" + id + "/height").request().accept(mediaType).post(entity);
                response.bufferEntity();
            }
        }

        response = SERVER.path("person").request().accept(mediaType).get();
        response.bufferEntity();
        System.out.println(RequestLog.prettify(mediaType, response.readEntity(String.class)));

        System.out.println("Initialization completed.\n");
    }

    private static void cleanDatabase() {
        String mediaType = MediaType.APPLICATION_JSON;
        System.out.println("Cleaning up the database...");
        Response response = SERVER.path("person").request().accept(mediaType).get();
        response.bufferEntity();
        System.out.println(RequestLog.prettify(mediaType, response.readEntity(String.class)));
        Map<String, List<Person>> peopleMap = response.readEntity(new GenericType<Map<String, List<Person>>>() {
        });
        for (Person person : peopleMap.get("people")) {
            response = SERVER.path("person/" + person.getId()).request().accept(mediaType).delete();
            response.bufferEntity();
        }
        response = SERVER.path("person").request().accept(mediaType).get();
        response.bufferEntity();
        System.out.println(RequestLog.prettify(mediaType, response.readEntity(String.class)));

        System.out.println("Cleaning completed.");
    }
}