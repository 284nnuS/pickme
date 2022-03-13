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
import tech.zoomidsoon.pickme_restful_api.models.MessageItem;
import tech.zoomidsoon.pickme_restful_api.repos.MessageListRepository;
import tech.zoomidsoon.pickme_restful_api.repos.MessageRepository;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@SuppressWarnings({ "unchecked" })
@Path("/message")
public class MessageController {
	@Path("/latest/userId/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserIdOfLatestMessage(@PathParam("id") int userId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				MessageRepository.FindLatestMessageByUserId findLatestMessageByUserId = new MessageRepository.FindLatestMessageByUserId(
						userId);
				List<Message> messages = MessageRepository.getInstance().read(conn, findLatestMessageByUserId);

				if (messages.size() > 0) {
					Message message = messages.get(0);
					return JsonAPIResponse.ok(message.getSender() == userId ? message.getReceiver() : message.getSender());
				}

				MessageListRepository.Find find = new MessageListRepository.Find(userId);
				List<MessageItem> messageList = MessageListRepository.getInstance().read(conn, find);

				if (messageList.size() > 0)
					return JsonAPIResponse.ok(messageList.get(0).getUserId());

				return JsonAPIResponse.handleError(204, "Not matched with anyone", "");

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

	@Path("/get")
	@POST
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