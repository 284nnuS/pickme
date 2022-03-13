package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import tech.zoomidsoon.pickme_restful_api.models.Notification;

public class NotificationRowMapper extends RowMapper<Notification> {

	private static final RowMapper<Notification> singleton = new NotificationRowMapper();

	private NotificationRowMapper() {

	}

	public static RowMapper<Notification> getInstance() {
		return singleton;
	}

	@Override
	public Boolean mapRow(ResultSet rs, Notification obj) throws SQLException {
		Integer notificationID = rs.getInt("notificationId");
		if (!obj.isEmpty() && notificationID != null && notificationID != obj.getNotificationId())
			return true;

		obj.setNotificationId(notificationID);
		obj.setTime(rs.getTimestamp("time").getTime());
		obj.setSourceUID(rs.getInt("sourceUID"));
		obj.setSourceUID(rs.wasNull() ? null : obj.getSourceUID());
		obj.setTargetUID(rs.getInt("targetUID"));
		obj.setAvatar(rs.getString("avatar"));
		obj.setEventType(rs.getString("eventType"));
		obj.setSeen(rs.getBoolean("seen"));
		obj.setMessage(rs.getString("message"));
		obj.setLink(rs.getString("link"));

		return false;

	}
}
