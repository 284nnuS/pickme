package tech.zoomidsoon.pickme_restful_api.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.helpers.SQLErrors;
import tech.zoomidsoon.pickme_restful_api.models.Notification;
import tech.zoomidsoon.pickme_restful_api.repos.NotificationRepository;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@SuppressWarnings({ "unchecked" })
@Path("/notify")
public class NotificationController {
	@GET
	@Path("/userId/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNotifications(@PathParam("userId") int userId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				NotificationRepository.FindByUserId findByUserId = new NotificationRepository.FindByUserId(userId);
				List<Notification> notifications = NotificationRepository.getInstance().read(conn, findByUserId);

				return JsonAPIResponse.ok(notifications);
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
	@Path("/seenAll/userId/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response seenAll(@PathParam("userId") int userId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				try (PreparedStatement stmt = conn
						.prepareStatement("UPDATE tblNotification SET seen = 1 WHERE targetUID = ?")) {
					stmt.setInt(1, userId);
					stmt.executeUpdate();

					return JsonAPIResponse.ok(true);
				}
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
	@Path("/seen/{userId}/{notificationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response seenAll(@PathParam("userId") int userId, @PathParam("notificationId") long notificationId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				try (PreparedStatement stmt = conn
						.prepareStatement("UPDATE tblNotification SET seen = 1 WHERE targetUID = ? AND notificationId = ?")) {
					stmt.setInt(1, userId);
					stmt.setLong(2, notificationId);
					stmt.executeUpdate();

					return JsonAPIResponse.ok(true);
				}
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
	public Response createNewNotification(Notification notification) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Result<Notification, JsonAPIResponse.Error> result = NotificationRepository.getInstance().create(conn,
						notification);
				return JsonAPIResponse.handleResult(result);
			} catch (SQLException e) {
				Response response = JsonAPIResponse.handleSQLError(e, SQLErrors.INCORRECT_DATA_TYPE,
						SQLErrors.TRIGGER_EXCEPTION);
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
	@Path("/id/{notificationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response markAsSeen(@PathParam("notificationId") int notificationId) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				Notification notification = new Notification();
				notification.setNotificationId(notificationId);
				notification.setSeen(true);
				Result<Notification, JsonAPIResponse.Error> result = NotificationRepository.getInstance().update(conn,
						notification);
				return JsonAPIResponse.handleResult(result);
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
