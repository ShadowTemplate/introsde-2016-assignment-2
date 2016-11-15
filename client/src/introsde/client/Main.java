package introsde.client;

import introsde.common.to.HealthProfile;
import introsde.common.to.MeasureType;
import introsde.common.to.Person;
import org.glassfish.jersey.client.ClientConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URI;
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
            for (int i = 1; i <= 12; i++) {
                Method method = Main.class.getMethod("request" + i, String.class);
                RequestLog response = (RequestLog) method.invoke(null, MediaType.APPLICATION_XML);
                response.log(xmlOut);
                response = (RequestLog) method.invoke(null, MediaType.APPLICATION_JSON);
                response.log(jsonOut);
            }
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
                response.getStatus(), body);
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
                response.getStatus(), body);
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
        RequestLog requestLog = new RequestLog();
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request7(String mediaType) {
        System.out.println("Executing request7 with " + mediaType);
        RequestLog requestLog = new RequestLog();
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request8(String mediaType) {
        System.out.println("Executing request8 with " + mediaType);
        RequestLog requestLog = new RequestLog();
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request9(String mediaType) {
        System.out.println("Executing request9 with " + mediaType);
        RequestLog requestLog = new RequestLog();
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
}