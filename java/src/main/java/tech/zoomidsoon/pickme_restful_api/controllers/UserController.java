package tech.zoomidsoon.pickme_restful_api.controllers;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tech.zoomidsoon.pickme_restful_api.models.User;
import tech.zoomidsoon.pickme_restful_api.repos.UserRepository;

@Path("/user")
public class UserController {

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("id") int userId) {
		return Response.ok().build();
	}

	/**
	 * Login
	 *
	 * @param email
	 * @return User
	 */
	@GET
	@Path("login/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@PathParam("email") String email) {

		UserRepository.FindByEmail fbe = new UserRepository.FindByEmail(email);
		List<User> user = UserRepository.getInstance().read(fbe);

		// Check list user is empty, true: return no content
		if (user.isEmpty()) {
			return Response.noContent().build();
		}
		return Response.ok(user.get(0)).build();

	}
}
