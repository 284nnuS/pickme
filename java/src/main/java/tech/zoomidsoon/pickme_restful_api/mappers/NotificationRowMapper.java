package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import tech.zoomidsoon.pickme_restful_api.models.Notification;

public class NotificationRowMapper extends RowMapper<Notification> {

    private static final RowMapper<Notification> singleton = new NotificationRowMapper();

    private NotificationRowMapper(){

    }

    public static RowMapper<Notification> getInstance() {
		return singleton;
	}

    @Override
	public Boolean mapRow(ResultSet rs, Notification obj) throws SQLException{
        Integer notificationID = rs.getInt("notificationId");
        if (!obj.isEmpty() && notificationID != null && notificationID != obj.getNotificationId())
			return true;
        obj.setNotificationId(notificationID);
        obj.setSourceUID(rs.getInt("sourceUID"));
        obj.setTargetUID(rs.getInt("targetUID"));
        obj.setEventType(rs.getString("eventType"));
        obj.setSeen(rs.getInt("seen"));
        obj.setMessage(rs.getString("message"));
        return false;
        
    }
}
