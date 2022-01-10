package tech.zoomidsoon.pickme_backend;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class HelloService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGet() {
        return Response.ok(new Hello("Hello world!")).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPost(Hello hello) {
        return Response.ok(hello).build();
    }
}
