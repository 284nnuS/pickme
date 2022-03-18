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
import tech.zoomidsoon.pickme_restful_api.models.Message;
import tech.zoomidsoon.pickme_restful_api.repos.MessageRepository;
import tech.zoomidsoon.pickme_restful_api.repos.MessageRepository.FindByConversationIdAndTime;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@SuppressWarnings({ "unchecked" })
@Path("/message")
public class MessageController {
	@GET
	@Path("/{conversationId}/{time}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMessages(@PathParam("conversationId") long conversationId, @PathParam("time") long time) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				FindByConversationIdAndTime findByConversationIdAndTime = new FindByConversationIdAndTime(conversationId,
						time);
				List<Message> messages = MessageRepository.getInstance().read(conn, findByConversationIdAndTime);
				return JsonAPIResponse.ok(messages);
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
	public Response createNewMessage(Message message) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Result<Message, JsonAPIResponse.Error> result = MessageRepository.getInstance().create(conn, message);

				return JsonAPIResponse.handleResult(result);
			}
		} catch (SQLException e) {
			Response response = JsonAPIResponse.handleSQLError(e,
					SQLErrors.DATA_TRUNCATED,
					SQLErrors.INCORRECT_DATA_TYPE);
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
	public Response updateMessage(Message message) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Result<Message, JsonAPIResponse.Error> result = MessageRepository.getInstance().update(conn, message);
				return JsonAPIResponse.handleResult(result);
			} catch (SQLException e) {
				Response response = JsonAPIResponse.handleSQLError(e, SQLErrors.DATA_TRUNCATED,
						SQLErrors.INCORRECT_DATA_TYPE);
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