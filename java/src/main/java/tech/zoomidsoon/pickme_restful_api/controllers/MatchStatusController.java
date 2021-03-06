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
import tech.zoomidsoon.pickme_restful_api.models.MatchStatus;
import tech.zoomidsoon.pickme_restful_api.repos.MatchStatusRepository;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@SuppressWarnings({ "unchecked" })
@Path("/matchStatus")
public class MatchStatusController {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createNewMatchStatus(MatchStatus matchStatus) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Result<MatchStatus, JsonAPIResponse.Error> result = MatchStatusRepository.getInstance().create(conn,
						matchStatus);

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
	public Response updateMatchStatus(MatchStatus matchStatus) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Result<MatchStatus, JsonAPIResponse.Error> result = MatchStatusRepository.getInstance().update(conn,
						matchStatus);

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

	@Path("/{userId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMatchStatusOfUser(@PathParam("userId") int userId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				MatchStatusRepository.FindByUserId findByUserId = new MatchStatusRepository.FindByUserId(userId);
				List<MatchStatus> matchStatuses = MatchStatusRepository.getInstance().read(conn, findByUserId);
				return JsonAPIResponse.ok(matchStatuses);
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

	// Check match status from 2 id, return List<MatchStatus> , if size=2, match, if
	// size<2 , unmatch
	@Path("/{userIdOne}/{userIdTwo}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkMatch(@PathParam("userIdOne") int userIdOne, @PathParam("userIdTwo") int userIdTwo) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				MatchStatusRepository.FindByUserIdOneAndTwo findByUserIdOneAndTwo = new MatchStatusRepository.FindByUserIdOneAndTwo(
						userIdOne, userIdTwo);
				List<MatchStatus> matchStatuses = MatchStatusRepository.getInstance().read(conn, findByUserIdOneAndTwo);

				return JsonAPIResponse
						.ok(matchStatuses.size() == 2 && matchStatuses.get(0).getLike() && matchStatuses.get(1).getLike());
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
}