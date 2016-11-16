package introsde.server.resources;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/measureTypes")
public class MeasureTypeResource {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response listMeasureTypes(@HeaderParam(HttpHeaders.ACCEPT) String mediaType) {
        System.out.println("/measureTypes GET");
        return ResourceProvider.listMeasureTypes(mediaType);
    }
}
