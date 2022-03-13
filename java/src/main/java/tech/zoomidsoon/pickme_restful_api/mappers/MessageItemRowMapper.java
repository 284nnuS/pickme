package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import tech.zoomidsoon.pickme_restful_api.models.MessageItem;

public class MessageItemRowMapper extends RowMapper<MessageItem> {
	private static final RowMapper<MessageItem> singleton = new MessageItemRowMapper();

	private MessageItemRowMapper() {
	}

	public static RowMapper<MessageItem> getInstance() {
		return singleton;
	}

	@Override
	public Boolean mapRow(ResultSet rs, MessageItem obj) throws SQLException {
		Integer userId = rs.getInt("userId");

		obj.setUserId(userId);
		obj.setMessageId(rs.getLong("messageId"));
		Timestamp time = rs.getTimestamp("time");
		obj.setTime(time != null ? time.getTime() : null);
		obj.setName(rs.getString("name"));
		obj.setAvatar(rs.getString("avatar"));
		obj.setIsSender(rs.getBoolean("isSender"));
		obj.setContent(rs.getString("content"));

		return null;
	}
}
