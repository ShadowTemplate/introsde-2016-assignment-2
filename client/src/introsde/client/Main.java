package introsde.client;

import introsde.common.to.Person;
import org.glassfish.jersey.client.ClientConfig;

import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

public class Main {

    private static final String ENDPOINT = "http://localhost:8080/";

    public static void main(String[] args) {
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);

        URI serverURI = UriBuilder.fromUri(ENDPOINT).build();
        WebTarget service = client.target(serverURI);


        System.out.println(service.path("person/8").request().accept(MediaType.APPLICATION_JSON).get().readEntity(Person.class));

        /*

        // // Accept: text/plain
        System.out.println(service.path("salutation").request().accept(MediaType.TEXT_PLAIN).get().readEntity(String.class));
        // // Get plain text
        System.out.println(service.path("salutation")
                .request().accept(MediaType.TEXT_PLAIN).get().readEntity(String.class));
        // Get XML
        System.out.println(service.path("salutation")
                .request()
                .accept(MediaType.TEXT_XML).get().readEntity(String.class));
        // // The HTML
        System.out.println(service.path("salutation").request()
                .accept(MediaType.TEXT_HTML).get().readEntity(String.class));
        */
    }
}