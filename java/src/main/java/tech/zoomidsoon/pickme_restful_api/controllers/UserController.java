package tech.zoomidsoon.pickme_restful_api.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Pair;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.helpers.SQLErrors;
import tech.zoomidsoon.pickme_restful_api.mixin.MediaMixin;
import tech.zoomidsoon.pickme_restful_api.models.User;
import tech.zoomidsoon.pickme_restful_api.repos.UserRepository;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Path("/user")
public class UserController {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllUsers() {
		try {
			try (Connection conn = DBContext.getConnection()) {
				List<User> users = UserRepository.getInstance().readAll(conn);

				return JsonAPIResponse.ok(users, new Pair(Media.class, MediaMixin.class));
			} catch (SQLException e) {
				Response response = JsonAPIResponse.handleSQLError(e);
				if (response != null)
					return response;
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
	}

	@GET
	@Path("/id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("id") int userId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				UserRepository.FindById findById = new UserRepository.FindById(userId);
				List<User> users = UserRepository.getInstance().read(conn, findById);

				if (users.isEmpty())
					return JsonAPIResponse.handleError(404, "User does not exist", "");
				return JsonAPIResponse.ok(users.get(0), new Pair(Media.class, MediaMixin.class));
			} catch (SQLException e) {
				Response response = JsonAPIResponse.handleSQLError(e);
				if (response != null)
					return response;
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
	}

	@GET
	@Path("/email/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findByEmail(@PathParam("email") String email) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				UserRepository.FindByEmail findByEmail = new UserRepository.FindByEmail(email);
				List<User> users = UserRepository.getInstance().read(conn, findByEmail);

				if (users.isEmpty())
					return JsonAPIResponse.handleError(404, "User does not exist", "");
				return JsonAPIResponse.ok(users.get(0));
			} catch (SQLException e) {
				Response response = JsonAPIResponse.handleSQLError(e);
				if (response != null)
					return response;
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createNewUser(User user) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Result<User, JsonAPIResponse.Error> result = UserRepository.getInstance().create(conn, user);

				return JsonAPIResponse.handleResult(result);
			}
		} catch (SQLException e) {
			Response response = JsonAPIResponse.handleSQLError(e,
					SQLErrors.DUPLICATE_ENTRY,
					SQLErrors.DATA_TRUNCATED,
					SQLErrors.INCORRECT_DATA_TYPE,
					SQLErrors.CHECK_CONSTANT);
			if (response != null)
				return response;
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(User user) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Result<User, JsonAPIResponse.Error> result = UserRepository.getInstance().update(conn, user);

				return JsonAPIResponse.handleResult(result);
			}
		} catch (SQLException e) {
			Response response = JsonAPIResponse.handleSQLError(e,
					SQLErrors.DUPLICATE_ENTRY,
					SQLErrors.DATA_TRUNCATED,
					SQLErrors.INCORRECT_DATA_TYPE,
					SQLErrors.CHECK_CONSTANT);
			if (response != null)
				return response;
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
	}

	@DELETE
	@Path("/id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteById(@PathParam("id") int userId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				User user = new User();
				user.setUserId(userId);
				Result<User, JsonAPIResponse.Error> result = UserRepository.getInstance().delete(conn, user);

				return JsonAPIResponse.handleResult(result);
			}
		} catch (SQLException e) {
			Response response = JsonAPIResponse.handleSQLError(e);
			if (response != null)
				return response;
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
	}
}
