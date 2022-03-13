package tech.zoomidsoon.pickme_restful_api.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.SQLErrors;
import tech.zoomidsoon.pickme_restful_api.models.MessageItem;
import tech.zoomidsoon.pickme_restful_api.repos.MessageListRepository;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@SuppressWarnings({ "unchecked" })
@Path("/messageList")
public class MessageListController {
	@Path("/userId/{userId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMarchStatusOfUser(@PathParam("userId") int userId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				MessageListRepository.Find find = new MessageListRepository.Find(userId);
				List<MessageItem> messageList = MessageListRepository.getInstance().read(conn, find);
				return JsonAPIResponse.ok(messageList);
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