package tech.zoomidsoon.pickme_restful_api.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.SQLErrors;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@Path("/superLike")
public class SuperLikeController {
	@POST
	@Path("/{userId}/{otherId}")
	public Response addSuperLike(@PathParam("userId") int userId, @PathParam("otherId") int otherId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				try (PreparedStatement stmt = conn
						.prepareStatement("INSERT INTO tblUserProfileReact(userId, reactUID) VALUES(?, ?)")) {
					stmt.setInt(1, otherId);
					stmt.setInt(2, userId);

					if (stmt.executeUpdate() == 1)
						return Response.ok().build();
				}
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
