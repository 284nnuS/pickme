package tech.zoomidsoon.pickme_restful_api.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.models.Conversation;
import tech.zoomidsoon.pickme_restful_api.repos.ConversationRepository;
import tech.zoomidsoon.pickme_restful_api.repos.ConversationRepository.FindByConversionIdAndUserId;
import tech.zoomidsoon.pickme_restful_api.repos.ConversationRepository.FindByTwoUserId;
import tech.zoomidsoon.pickme_restful_api.repos.ConversationRepository.FindByUserId;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@SuppressWarnings({ "unchecked" })
@Path("/conversation")
public class ConversationController {
	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findConversationsByUserId(@PathParam("userId") int userId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				FindByUserId findByUserId = new FindByUserId(userId);
				List<Conversation> conversations = ConversationRepository.getInstance().read(conn, findByUserId);

				return JsonAPIResponse.ok(conversations);
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
	@Path("/check/{conversationId}/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findConversationByTwoUserId(@PathParam("conversationId") long conversationId,
			@PathParam("userId") int userId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				FindByConversionIdAndUserId findByConversionIdAndUserId = new FindByConversionIdAndUserId(conversationId,
						userId);
				List<Conversation> conversations = ConversationRepository.getInstance().read(conn,
						findByConversionIdAndUserId);

				if (conversations.isEmpty())
					return JsonAPIResponse.handleError(404, "Conversation not found", "");

				return JsonAPIResponse.ok(conversations.get(0));
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
	@Path("/{userId}/{otherId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findConversationByTwoUserId(@PathParam("userId") int userId, @PathParam("otherId") int otherId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				FindByTwoUserId findByTwoUserId = new FindByTwoUserId(userId, otherId);
				List<Conversation> conversations = ConversationRepository.getInstance().read(conn, findByTwoUserId);

				if (conversations.isEmpty())
					return JsonAPIResponse.handleError(404, "Conversation not found", "");

				return JsonAPIResponse.ok(conversations.get(0));
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
