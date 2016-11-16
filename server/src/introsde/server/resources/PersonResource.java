package introsde.server.resources;

import introsde.common.to.MeasureType;
import introsde.common.to.Person;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/person")
public class PersonResource {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response listPeople(@HeaderParam(HttpHeaders.ACCEPT) String mediaType) {
        System.out.println("/person GET");
        return ResourceProvider.listPeople(mediaType);
    }

    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPerson(@PathParam("id") Double personId){
        System.out.println("/person/" + personId + " GET");
        return ResourceProvider.getPerson(personId);
    }

    @Path("{id}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response putPerson(@PathParam("id") Double personId, Person person) {
        System.out.println("/person/" + personId + " PUT");
        System.out.println("Input param:\n" + person);
        return ResourceProvider.putPerson(personId, person);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postPerson(Person person) {
        System.out.println("/person POST");
        System.out.println("Input param:\n" + person);
        return ResourceProvider.postPerson(person);
    }

    @Path("{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deletePerson(@PathParam("id") Double personId) {
        System.out.println("/person/" + personId + " DELETE");
        return ResourceProvider.deletePerson(personId);
    }

    @Path("{id}/{measureType}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMeasureHistory(@PathParam("id") Double personId, @PathParam("measureType") String measureType) {
        System.out.println("/person/" + personId + "/" + measureType + " GET");
        return ResourceProvider.getMeasureHistory(personId, measureType);
    }

    @Path("{id}/{measureType}/{mid}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCurrentMeasure(@PathParam("id") Double personId, @PathParam("measureType") String measureType,
                                      @PathParam("mid") Double measureId) {
        System.out.println("/person/" + personId + "/" + measureType + "/" + measureId + " GET");
        return ResourceProvider.getCurrentMeasure(personId, measureType, measureId);
    }

    @Path("{id}/{measureType}")
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postNewMeasure(@PathParam("id") Double personId, @PathParam("measureType") String measureType,
                                   MeasureType measure) {
        System.out.println("/person/" + personId + "/" + measureType + " POST");
        System.out.println("Input param:\n" + measure);
        return ResourceProvider.postNewMeasure(personId, measureType, measure);
    }
}

