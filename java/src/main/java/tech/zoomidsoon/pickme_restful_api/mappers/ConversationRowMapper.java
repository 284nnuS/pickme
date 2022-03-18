package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import tech.zoomidsoon.pickme_restful_api.models.Conversation;

public class ConversationRowMapper extends RowMapper<Conversation> {
	private static final RowMapper<Conversation> singleton = new ConversationRowMapper();

	private ConversationRowMapper() {
	}

	public static RowMapper<Conversation> getInstance() {
		return singleton;
	}

	@Override
	public Boolean mapRow(ResultSet rs, Conversation obj) throws SQLException {
		obj.setConversationId(rs.getLong("conversationId"));
		obj.setOtherId(rs.getInt("otherId"));
		obj.setOtherName(rs.getString("otherName"));
		obj.setOtherAvatar(rs.getString("otherAvatar"));
		obj.setSender(rs.getBoolean("isSender"));
		Timestamp time = rs.getTimestamp("latestTime");
		obj.setLatestTime(time == null ? null : time.getTime());
		obj.setLatestMessage(rs.getString("latestMessage"));

		return null;
	}
}
