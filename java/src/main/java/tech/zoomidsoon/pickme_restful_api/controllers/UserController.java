package tech.zoomidsoon.pickme_restful_api.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserController {

	/**
	 * @param userId
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGet(String userId) {
		return Response.ok().build();
	}
}
