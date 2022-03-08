package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import lombok.*;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse.Error;
import tech.zoomidsoon.pickme_restful_api.mappers.NotificationRowMapper;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.models.Notification;

public class NotificationRepository implements Repository<Notification>  {
    private static final Repository<Notification> singleton = new NotificationRepository();

	private NotificationRepository() {
	}

	public static Repository<Notification> getInstance() {
		return singleton;
	}
    @Override
	public Result<Notification, Error> create(Connection conn, Notification notification) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO tblNotification (notificationId, sourceUID, targetUID, eventType, seen, message) VALUES (?,?,?,?,?,?)",
				Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, notification.getNotificationId());
                stmt.setInt(2, notification.getSourceUID());
                stmt.setInt(3, notification.getTargetUID());
                stmt.setString(4, notification.getEventType());
                stmt.setInt(5, notification.getSeen());
                stmt.setString(6, notification.getMessage());    
            if (stmt.executeUpdate() != 1)
				return new Result<>(null, JsonAPIResponse.SERVER_ERROR);

			return new Result<>(notification, null);
		}
	}
    @Override
	public List<Notification> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(MatchStatusRepository.class)) {
			throw new IllegalArgumentException("Criteria does not belong to the same nest host class");
		}

		try (ResultSet rs = criteria.query(conn)) {
			return NotificationRowMapper.getInstance().processResultSet(rs, Notification.class);
		}
	}

    @Override
	public Result<Notification, Error> update(Connection conn, Notification notification) throws Exception {
		// Cannot update if notificationID is missing
		if (notification.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "notificationID are required", ""));

		try (PreparedStatement stmt = conn.prepareStatement(
				"UPDATE tblNotification SET eventType = ?, seen = ?, message = ?, sourceUID = ?, targetUID=? WHERE notificationId = ?")) {
			stmt.setString(1, notification.getEventType());
			stmt.setInt(2, notification.getSeen());
			stmt.setString(3, notification.getMessage());
            stmt.setInt(4, notification.getSourceUID());
            stmt.setInt(5, notification.getTargetUID());
            stmt.setInt(6, notification.getNotificationId());
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
		try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblNotification",
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY)) {
			try (ResultSet rs = stmt.executeQuery()) {
				return NotificationRowMapper.getInstance().processResultSet(rs, Notification.class);
			}
		}
	}
    @AllArgsConstructor
	public static class FindByNotificationId implements Criteria {
		private int notificationID;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblNotification where notificationId = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, notificationID);
			return stmt.executeQuery();
		}
	}
}
