package tech.zoomidsoon.pickme_restful_api.controllers;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tech.zoomidsoon.pickme_restful_api.models.User;
import tech.zoomidsoon.pickme_restful_api.repos.UserRepository;

@Path("/users")
public class UserController {

	@GET
	@Path("/id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("id") int userId) {
		UserRepository.FindById fbi = new UserRepository.FindById(userId);
		List<User> users = UserRepository.getInstance().read(fbi);

		if (users.isEmpty()) {
			return Response.noContent().build();
		}
		return Response.ok(users.get(0)).build();
	}

	@GET
	@Path("/email/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findByEmail(@PathParam("email") String email) {
		UserRepository.FindByEmail fbe = new UserRepository.FindByEmail(email);
		List<User> users = UserRepository.getInstance().read(fbe);

		if (users.isEmpty()) {
			return Response.noContent().build();
		}
		return Response.ok(users.get(0)).build();
	}
}
