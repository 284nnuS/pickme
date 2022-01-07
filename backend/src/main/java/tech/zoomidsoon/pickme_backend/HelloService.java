package tech.zoomidsoon.pickme_backend;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@Path("/hello")
public class HelloService {
    @GET
    @Produces("application/json")
    public Response doGet() {
        return Response.ok(new Hello("Hello world!").toJson()).build();
    }
}
