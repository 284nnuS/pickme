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
import tech.zoomidsoon.pickme_restful_api.models.Interest;
import tech.zoomidsoon.pickme_restful_api.repos.InterestRepository;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@SuppressWarnings({ "unchecked" })
@Path("/interest")
public class InterestController {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllInterests() {
		try {
			try (Connection conn = DBContext.getConnection()) {
				List<Interest> interests = InterestRepository.getInstance().readAll(conn);

				return JsonAPIResponse.ok(interests);
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
	public Response createNewInterest(Interest interest) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Result<Interest, JsonAPIResponse.Error> result = InterestRepository.getInstance().create(conn, interest);

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
	public Response updateInterest(Interest interest) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Result<Interest, JsonAPIResponse.Error> result = InterestRepository.getInstance().update(conn, interest);

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
	@Path("/name/{interestName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteById(@PathParam("interestName") String interestName) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Interest interest = new Interest();
				interest.setInterestName(interestName);
				Result<Interest, JsonAPIResponse.Error> result = InterestRepository.getInstance().delete(conn, interest);

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
