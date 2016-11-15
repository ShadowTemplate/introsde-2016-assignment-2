package introsde.client;

import introsde.common.to.Person;
import org.glassfish.jersey.client.ClientConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.ClientBuilder;
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
        String body;
        switch (mediaType) {
            case MediaType.APPLICATION_JSON:
                Map<String, List<Person>> peopleMap = response.readEntity(new GenericType<Map<String, List<Person>>>() {
                });
                people = peopleMap.get("people");
                body = RequestLog.jsonToPrettyString(response.readEntity(String.class));
                break;
            case MediaType.APPLICATION_XML:
                people = response.readEntity(new GenericType<List<Person>>() {
                });
                body = RequestLog.xmlToPrettyString(response.readEntity(String.class));
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
        RequestLog requestLog = new RequestLog(1, HttpMethod.GET, url, mediaType, mediaType, result,
                response.getStatus(), body);
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request2(String mediaType) {
        System.out.println("Executing request2 with " + mediaType);
        RequestLog requestLog = new RequestLog();
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request3(String mediaType) {
        System.out.println("Executing request3 with " + mediaType);
        RequestLog requestLog = new RequestLog();
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request4(String mediaType) {
        System.out.println("Executing request4 with " + mediaType);
        RequestLog requestLog = new RequestLog();
        System.out.println(requestLog + "\n");
        return requestLog;
    }

    public static RequestLog request5(String mediaType) {
        System.out.println("Executing request5 with " + mediaType);
        RequestLog requestLog = new RequestLog();
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