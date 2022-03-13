package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import lombok.*;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse.Error;
import tech.zoomidsoon.pickme_restful_api.mappers.NotificationRowMapper;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.models.Notification;

public class NotificationRepository implements Repository<Notification> {
	private static final Repository<Notification> singleton = new NotificationRepository();

	private NotificationRepository() {
	}

	public static Repository<Notification> getInstance() {
		return singleton;
	}

	@Override
	public Result<Notification, Error> create(Connection conn, Notification notification) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO tblNotification (time, sourceUID, targetUID, eventType, seen, message, link) VALUES (?,?,?,?,?,?,?)",
				Statement.RETURN_GENERATED_KEYS)) {
			stmt.setTimestamp(1, new Timestamp(notification.getTime()));
			if (notification.getSourceUID() != null)
				stmt.setInt(2, notification.getSourceUID());
			else
				stmt.setNull(2, java.sql.Types.NULL);
			stmt.setInt(3, notification.getTargetUID());
			stmt.setString(4, notification.getEventType());
			stmt.setBoolean(5, notification.getSeen());
			stmt.setString(6, notification.getMessage());
			stmt.setString(7, notification.getLink());

			if (stmt.executeUpdate() != 1)
				return new Result<>(null, JsonAPIResponse.SERVER_ERROR);

			// Get id of recently created notification
			try (ResultSet rs = stmt.getGeneratedKeys()) {
				rs.next();
				notification.setNotificationId(rs.getInt(1));
			}

			ResultSet rs2 = stmt
					.executeQuery("SELECT avatar from tblUser WHERE userId = " + notification.getSourceUID());
			rs2.next();
			notification.setAvatar(rs2.getString(1));

			return new Result<>(notification, null);
		}
	}

	@Override
	public List<Notification> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(NotificationRepository.class)) {
			throw new IllegalArgumentException("Criteria does not belong to the same nest host class");
		}

		try (ResultSet rs = criteria.query(conn)) {
			return NotificationRowMapper.getInstance().processResultSet(rs, Notification.class);
		}
	}

	@Override
	public Result<Notification, Error> update(Connection conn, Notification notification) throws Exception {
		// Cannot update if notificationId is missing
		if (notification.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "notificationId are required", ""));

		try (PreparedStatement stmt = conn.prepareStatement(
				"UPDATE tblNotification SET seen = ? WHERE notificationId = ?")) {
			stmt.setBoolean(1, notification.getSeen());
			stmt.setInt(2, notification.getNotificationId());
			if (stmt.executeUpdate() != 1)
				return new Result<>(null, JsonAPIResponse.SERVER_ERROR);

			return new Result<>(notification, null);
		}
	}

	@Override
	public Result<Notification, Error> delete(Connection conn, Notification matchStatus) throws Exception {
		throw new UnsupportedOperationException("Notification Repository does not support deleting");
	}

	@Override
	public List<Notification> readAll(Connection conn) throws Exception {
		throw new UnsupportedOperationException("Notification Repository does not support readAll");
	}

	@AllArgsConstructor
	public static class FindByUserId implements Criteria {
		private int userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblNotification a \n"
							+ "LEFT OUTER JOIN tblUser b \n"
							+ "ON a.sourceUID = b.userId \n"
							+ "WHERE a.targetUID = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, userId);
			return stmt.executeQuery();
		}
	}
}
