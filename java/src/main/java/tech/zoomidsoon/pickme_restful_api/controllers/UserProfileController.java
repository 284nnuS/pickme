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
import tech.zoomidsoon.pickme_restful_api.models.UserProfile;
import tech.zoomidsoon.pickme_restful_api.repos.UserProfileRepository;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Path("/profile")
public class UserProfileController {
	@GET
	@Path("/id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam("id") int userId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				UserProfileRepository.FindById findById = new UserProfileRepository.FindById(userId);
				List<UserProfile> profiles = UserProfileRepository.getInstance().read(conn, findById);

				if (profiles.isEmpty())
					return JsonAPIResponse.handleError(404, "User does not exist", "");
				return JsonAPIResponse.ok(profiles.get(0), new Pair(Media.class, MediaMixin.class));
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
	@Path("/unmatched/id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findUnmatchedUsersById(@PathParam("id") int userId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				UserProfileRepository.FindUnMatchedUsersById findUnMatchedUsersById = new UserProfileRepository.FindUnMatchedUsersById(
						userId);
				List<UserProfile> profiles = UserProfileRepository.getInstance().read(conn, findUnMatchedUsersById);

				return JsonAPIResponse.ok(profiles, new Pair(Media.class, MediaMixin.class));
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
	@Path("/matched/id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findMatchedUsersById(@PathParam("id") int userId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				UserProfileRepository.FindMatchedUsersById findMatchedUsersById = new UserProfileRepository.FindMatchedUsersById(
						userId);
				List<UserProfile> profiles = UserProfileRepository.getInstance().read(conn, findMatchedUsersById);

				return JsonAPIResponse.ok(profiles, new Pair(Media.class, MediaMixin.class));
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
	public Response createUserProfile(UserProfile profile) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Result<UserProfile, JsonAPIResponse.Error> result = UserProfileRepository.getInstance().create(conn,
						profile);

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
	public Response updateUserProfile(UserProfile profile) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Result<UserProfile, JsonAPIResponse.Error> result = UserProfileRepository.getInstance().update(conn,
						profile);

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
}
