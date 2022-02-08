package tech.zoomidsoon.pickme_restful_api.controllers;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tech.zoomidsoon.pickme_restful_api.repos.UserRepository;

@Path("/user")
public class UserController {
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("id") int userId) {
		return Response.ok().build();
	}
}
