package introsde.server.resources;

import introsde.common.to.MeasureType;
import introsde.common.to.Person;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/person")
public class PersonResource {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response listPeople(@HeaderParam(HttpHeaders.ACCEPT) String mediaType,
                               @QueryParam("measureType") String measureType, @QueryParam("min") Double min,
                               @QueryParam("max") Double max) {
        System.out.println("/person GET");
        System.out.println("Query param 'measureType': " + measureType);
        System.out.println("Query param 'min': " + min);
        System.out.println("Query param 'max': " + max);
        if (measureType != null && (min != null || max != null)) {
            return ResourceDispatcher.listPeopleWithProperty(mediaType, measureType, min, max);
        } else {
            return ResourceDispatcher.listPeople(mediaType);
        }
    }

    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getPerson(@PathParam("id") Double personId) {
        System.out.println("/person/" + personId + " GET");
        return ResourceDispatcher.getPerson(personId);
    }

    @Path("{id}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response putPerson(@PathParam("id") Double personId, Person person) {
        System.out.println("/person/" + personId + " PUT");
        System.out.println("Input param:\n" + person);
        return ResourceDispatcher.putPerson(personId, person);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postPerson(Person person) {
        System.out.println("/person POST");
        System.out.println("Input param:\n" + person);
        return ResourceDispatcher.postPerson(person);
    }

    @Path("{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deletePerson(@PathParam("id") Double personId) {
        System.out.println("/person/" + personId + " DELETE");
        return ResourceDispatcher.deletePerson(personId);
    }

    @Path("{id}/{measureType}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMeasureHistory(@PathParam("id") Double personId, @PathParam("measureType") String measureType,
                                      @QueryParam("before") String before, @QueryParam("after") String after) {
        System.out.println("/person/" + personId + "/" + measureType + " GET");
        System.out.println("Query param 'before': " + before);
        System.out.println("Query param 'after': " + after);
        return before != null && after != null ?
                ResourceDispatcher.getMeasureHistoryInRange(personId, measureType, before, after) :
                ResourceDispatcher.getMeasureHistory(personId, measureType);
    }

    @Path("{id}/{measureType}/{mid}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCurrentMeasure(@PathParam("id") Double personId, @PathParam("measureType") String measureType,
                                      @PathParam("mid") Double measureId) {
        System.out.println("/person/" + personId + "/" + measureType + "/" + measureId + " GET");
        return ResourceDispatcher.getCurrentMeasure(personId, measureType, measureId);
    }

    @Path("{id}/{measureType}")
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postNewMeasure(@PathParam("id") Double personId, @PathParam("measureType") String measureType,
                                   MeasureType measure) {
        System.out.println("/person/" + personId + "/" + measureType + " POST");
        System.out.println("Input param:\n" + measure);
        return ResourceDispatcher.postNewMeasure(personId, measureType, measure);
    }

    @Path("{id}/{measureType}/{mid}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateMeasureValue(@PathParam("id") Double personId, @PathParam("measureType") String measureType,
                                       @PathParam("mid") Double mid, MeasureType measure) {
        System.out.println("/person/" + personId + "/" + measureType + "/" + mid + " PUT");
        System.out.println("Input param:\n" + measure);
        return ResourceDispatcher.updateMeasureValue(personId, measureType, mid, measure);
    }
}

