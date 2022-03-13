package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse.Error;
import tech.zoomidsoon.pickme_restful_api.mappers.MessageItemRowMapper;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.models.MessageItem;

public class MessageListRepository implements Repository<MessageItem> {
	private static final Repository<MessageItem> singleton = new MessageListRepository();

	private MessageListRepository() {
	}

	public static Repository<MessageItem> getInstance() {
		return singleton;
	}

	@Override
	public Result<MessageItem, Error> create(Connection conn, MessageItem messageItem) throws Exception {
		throw new UnsupportedOperationException("MatchStatus Repository does not support creating");
	}

	@Override
	public List<MessageItem> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(MessageListRepository.class)) {
			throw new IllegalArgumentException("Criteria does not belong to the same nest host class");
		}

		try (ResultSet rs = criteria.query(conn)) {
			return MessageItemRowMapper.getInstance().processResultSet(rs, MessageItem.class);
		}
	}

	@Override
	public Result<MessageItem, Error> update(Connection conn, MessageItem messageItem) throws Exception {
		throw new UnsupportedOperationException("MatchStatus Repository does not support updating");
	}

	@Override
	public Result<MessageItem, Error> delete(Connection conn, MessageItem messageItem) throws Exception {
		throw new UnsupportedOperationException("MatchStatus Repository does not support deleting");
	}

	@Override
	public List<MessageItem> readAll(Connection conn) throws Exception {
		throw new UnsupportedOperationException("MatchStatus Repository does not support readAll");
	}

	@AllArgsConstructor
	public static class Find implements Criteria {
		private int userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT DISTINCT tu.userId, a.messageId, a.`time`, tu.name, tu.avatar, CASE WHEN a.sender = @userId THEN TRUE ELSE FALSE END as isSender, a.content \n"
							+ "FROM tblMessage a \n"
							+ "LEFT OUTER JOIN (SELECT \n"
							+ "CASE WHEN tm.sender = @userId THEN tm.receiver ELSE tm.sender END as userId, \n"
							+ "MAX(tm.`time`) as `time` FROM tblMessage tm \n"
							+ "GROUP BY least(tm.sender,tm.receiver), greatest(tm.sender,tm.receiver)) re \n"
							+ "ON ((a.sender = @userId AND a.receiver = re.userId) OR (a.receiver  = @userId AND a.sender = re.userId)) AND a.`time`= re.`time` \n"
							+ "RIGHT OUTER JOIN tblUser tu \n"
							+ "ON re.userId = tu.userId \n"
							+ "INNER JOIN (SELECT tms1.userIdTwo as matchedId FROM tblMatchStatus tms1 \n"
							+ "INNER JOIN tblMatchStatus tms2 \n"
							+ "ON tms1.userIdOne = tms2.userIdTwo AND tms1.userIdTwo = tms2.userIdOne AND tms1.`like` = 1 AND tms2.`like` = 1 AND tms1.userIdOne = @userId) ma \n"
							+ "ON tu.userId = ma.matchedId \n"
							+ "WHERE tu.userId != @userId",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.execute("SET sql_mode='';");
			stmt.execute("SET @userId = " + userId + ";");
			return stmt.executeQuery();
		}
	}
}
