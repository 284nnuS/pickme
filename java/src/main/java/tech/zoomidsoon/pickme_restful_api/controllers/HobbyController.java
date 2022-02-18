package tech.zoomidsoon.pickme_restful_api.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.helpers.SQLErrors;
import tech.zoomidsoon.pickme_restful_api.models.Hobby;
import tech.zoomidsoon.pickme_restful_api.repos.HobbyRepository;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@Path("/hobby")
public class HobbyController {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllHobbies() {
		try {
			try (Connection conn = DBContext.getConnection()) {
				List<Hobby> hobbies = HobbyRepository.getInstance().readAll(conn);

				return JsonAPIResponse.ok(hobbies);
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
	public Response createNewHobby(Hobby hobby) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Result<Hobby, JsonAPIResponse.Error> result = HobbyRepository.getInstance().create(conn, hobby);

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
	public Response updateHobby(Hobby hobby) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Result<Hobby, JsonAPIResponse.Error> result = HobbyRepository.getInstance().update(conn, hobby);

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
	@Path("/name/{hobbyName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteById(@PathParam("hobbyName") String hobbyName) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Hobby hobby = new Hobby();
				hobby.setHobbyName(hobbyName);
				Result<Hobby, JsonAPIResponse.Error> result = HobbyRepository.getInstance().delete(conn, hobby);

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
