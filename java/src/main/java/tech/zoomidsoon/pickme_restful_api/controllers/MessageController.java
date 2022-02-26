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
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@Path("/message")
public class MessageController {
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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMessages(MessageRepository.FindByTimeAndUserId findByTimeAndUserId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				List<Message> messages = MessageRepository.getInstance().read(conn, findByTimeAndUserId);
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
}