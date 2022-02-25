package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import tech.zoomidsoon.pickme_restful_api.models.Message;

public class MessageRowMapper extends RowMapper<Message> {
	private static final RowMapper<Message> singleton = new MessageRowMapper();

	private MessageRowMapper() {
	}

	public static RowMapper<Message> getInstance() {
		return singleton;
	}

	@Override
	public boolean mapRow(ResultSet rs, Message obj) throws SQLException {
		Long messageId = rs.getLong("messageId");

		if (!obj.isEmpty() && messageId != null && !messageId.equals(obj.getMessageId()))
			return true;

		if (obj.isEmpty()) {
			Timestamp time = rs.getTimestamp("time");

			obj.setMessageId(messageId);
			obj.setTime(time != null ? time.getTime() : null);
			obj.setSender(rs.getInt("sender"));
			obj.setReceiver(rs.getInt("receiver"));
			obj.setContent(rs.getString("content"));
			obj.setReact(rs.getString("react"));
		}

		return false;
	}
}
