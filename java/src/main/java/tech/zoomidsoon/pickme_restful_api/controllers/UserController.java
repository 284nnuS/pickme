package tech.zoomidsoon.pickme_restful_api.controllers;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tech.zoomidsoon.pickme_restful_api.models.User;
import tech.zoomidsoon.pickme_restful_api.repos.UserRepository;
import tech.zoomidsoon.pickme_restful_api.utils.SQLErrors;
import tech.zoomidsoon.pickme_restful_api.utils.JsonAPIResponse;

@Path("/users")
public class UserController {
	@GET
	@Path("/id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("id") int userId) {
		try {
			UserRepository.FindById fbi = new UserRepository.FindById(userId);
			List<User> users = UserRepository.getInstance().read(fbi);

			if (users.isEmpty())
				return JsonAPIResponse.error(404, "User does not exist", "");
			return JsonAPIResponse.ok(users.get(0));
		} catch (SQLException e) {
			Response response = JsonAPIResponse.sqlErrors(e);
			if (response != null)
				return response;
		} catch (Exception e) {
			System.out.println(e);
		}
		return Response.serverError().build();
	}

	@GET
	@Path("/email/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findByEmail(@PathParam("email") String email) {
		try {
			UserRepository.FindByEmail fbe = new UserRepository.FindByEmail(email);
			List<User> users = UserRepository.getInstance().read(fbe);

			if (users.isEmpty())
				return JsonAPIResponse.error(404, "User does not exist", "");
			return JsonAPIResponse.ok(users.get(0));
		} catch (SQLException e) {
			Response response = JsonAPIResponse.sqlErrors(e);
			if (response != null)
				return response;
		} catch (Exception e) {
			System.out.println(e);
		}
		return Response.serverError().build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createNewUser(User user) {
		try {
			user = UserRepository.getInstance().create(user);

			// Always not null
			return JsonAPIResponse.ok(user);
		} catch (SQLException e) {
			Response response = JsonAPIResponse.sqlErrors(e,
					SQLErrors.DUPLICATE_ENTRY,
					SQLErrors.INCORRECT_DATA_TYPE,
					SQLErrors.CHECK_CONSTANT);
			if (response != null)
				return response;
		} catch (Exception e) {
			System.out.println(e);
		}
		return Response.serverError().build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(User user) {
		try {
			user = UserRepository.getInstance().update(user);

			if (user == null)
				return JsonAPIResponse.error(404, "User does not exist", "");
			return JsonAPIResponse.ok(user);
		} catch (SQLException e) {
			Response response = JsonAPIResponse.sqlErrors(e,
					SQLErrors.DUPLICATE_ENTRY,
					SQLErrors.INCORRECT_DATA_TYPE,
					SQLErrors.CHECK_CONSTANT);
			if (response != null)
				return response;
		} catch (Exception e) {
			System.out.println(e);
		}
		return Response.serverError().build();
	}

	@DELETE
	@Path("/id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteById(@PathParam("id") int userId) {
		try {
			User user = new User();
			user.setUserId(userId);

			user = UserRepository.getInstance().delete(user);

			if (user == null)
				return JsonAPIResponse.error(404, "User does not exist", "");
			return JsonAPIResponse.ok(user);
		} catch (SQLException e) {
			Response response = JsonAPIResponse.sqlErrors(e);
			if (response != null)
				return response;
		} catch (Exception e) {
			System.out.println(e);
		}
		return Response.serverError().build();
	}
}
