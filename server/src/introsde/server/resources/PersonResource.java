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
        return ResourceProvider.listPeople(mediaType);
    }

    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPerson(@PathParam("id") Double personId){
        return ResourceProvider.getPerson(personId);
    }

    @Path("{id}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response putPerson(@PathParam("id") Double personId, Person person) {
        return ResourceProvider.putPerson(personId, person);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postPerson(Person person) {
        return ResourceProvider.postPerson(person);
    }

    @Path("{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deletePerson(@PathParam("id") Double personId) {
        return ResourceProvider.deletePerson(personId);
    }

    @Path("{id}/{measureType}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMeasureHistory(@PathParam("id") Double personId, @PathParam("measureType") String measureType) {
        return ResourceProvider.getMeasureHistory(personId, measureType);
    }

    @Path("{id}/{measureType}/{mid}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCurrentMeasure(@PathParam("id") Double personId, @PathParam("measureType") String measureType,
                                      @PathParam("mid") Double measureId) {
        return ResourceProvider.getCurrentMeasure(personId, measureType, measureId);
    }

    @Path("{id}/{measureType}")
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postNewMeasure(@PathParam("id") Double personId, @PathParam("measureType") String measureType,
                                   MeasureType measure) {
        return ResourceProvider.postNewMeasure(personId, measureType, measure);
    }
}

