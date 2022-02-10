package tech.zoomidsoon.pickme_restful_api.controllers;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tech.zoomidsoon.pickme_restful_api.models.User;
import tech.zoomidsoon.pickme_restful_api.repos.UserRepository;
import tech.zoomidsoon.pickme_restful_api.utils.JsonAPIResponse;

@Path("/users")
public class UserController {
	@GET
	@Path("/id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("id") int userId) {
		UserRepository.FindById fbi = new UserRepository.FindById(userId);
		List<User> users = UserRepository.getInstance().read(fbi);

		if (users.isEmpty()) {
			return JsonAPIResponse.error(404, "No users found");
		}
		return JsonAPIResponse.ok(users.get(0));
	}

	@GET
	@Path("/email/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findByEmail(@PathParam("email") String email) {
		UserRepository.FindByEmail fbe = new UserRepository.FindByEmail(email);
		List<User> users = UserRepository.getInstance().read(fbe);

		if (users.isEmpty()) {
			return JsonAPIResponse.error(404, "No users found");
		}
		return JsonAPIResponse.ok(users.get(0));
	}

	/**
	 * Register
	 *
	 * @param User
	 * @return User
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(User user) {

		try {
		    UserRepository.getInstance().create(user);
		} catch (Exception e) {
			return Response.noContent().build();
		}
		return Response.ok(user).build();

	}
}
