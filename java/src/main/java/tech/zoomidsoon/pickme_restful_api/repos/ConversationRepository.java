package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse.Error;
import tech.zoomidsoon.pickme_restful_api.mappers.ConversationRowMapper;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.models.Conversation;

public class ConversationRepository implements Repository<Conversation> {
	private static final Repository<Conversation> singleton = new ConversationRepository();

	private ConversationRepository() {
	}

	public static Repository<Conversation> getInstance() {
		return singleton;
	}

	@Override
	public Result<Conversation, Error> create(Connection conn, Conversation conversation) throws Exception {
		throw new UnsupportedOperationException("MatchStatus Repository does not support creating");
	}

	@Override
	public List<Conversation> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(ConversationRepository.class)) {
			throw new IllegalArgumentException("Criteria does not belong to the same nest host class");
		}

		try (ResultSet rs = criteria.query(conn)) {
			return ConversationRowMapper.getInstance().processResultSet(rs, Conversation.class);
		}
	}

	@Override
	public Result<Conversation, Error> update(Connection conn, Conversation conversation) throws Exception {
		throw new UnsupportedOperationException("Conversation Repository does not support updating");
	}

	@Override
	public Result<Conversation, Error> delete(Connection conn, Conversation conversation) throws Exception {
		throw new UnsupportedOperationException("Conversation Repository does not support deleting");
	}

	@Override
	public List<Conversation> readAll(Connection conn) throws Exception {
		throw new UnsupportedOperationException("Conversation Repository does not support readAll");
	}

	@AllArgsConstructor
	public static class FindByConversionIdAndUserId implements Criteria {
		private long conversationId;
		private long userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT tc.conversationId, \n"
							+ "CASE WHEN tc.userIdOne = @userId THEN tc.userIdTwo ELSE tc.userIdOne END as otherId, \n"
							+ "CASE WHEN tc.userIdOne = @userId THEN tup2.name ELSE tup1.name END as otherName, \n"
							+ "CASE WHEN tc.userIdOne = @userId THEN tup2.avatar ELSE tup1.avatar END as otherAvatar, \n"
							+ "@userId = tm2.sender as isSender, \n"
							+ "tm2.`time` as latestTime, tm2.content as latestMessage FROM tblConversation tc \n"
							+ "LEFT OUTER JOIN (SELECT tm.conversationId, MAX(messageId) as latest FROM tblMessage tm GROUP BY tm.conversationId) re \n"
							+ "ON tc.conversationId = re.conversationId \n"
							+ "LEFT OUTER JOIN tblMessage tm2 \n"
							+ "ON tc.conversationId = tm2.conversationId AND tm2.messageId = re.latest \n"
							+ "INNER JOIN tblUserProfile tup1 \n"
							+ "ON tc.userIdOne = tup1.userId \n"
							+ "INNER JOIN tblUserProfile tup2 \n"
							+ "ON tc.userIdTwo = tup2.userId \n"
							+ "WHERE tc.conversationId = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setLong(1, conversationId);
			stmt.execute("SET @userId = " + userId + ";");
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindByUserId implements Criteria {
		private int userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT tc.conversationId, \n"
							+ "CASE WHEN tc.userIdOne = @userId THEN tc.userIdTwo ELSE tc.userIdOne END as otherId, \n"
							+ "CASE WHEN tc.userIdOne = @userId THEN tup2.name ELSE tup1.name END as otherName, \n"
							+ "CASE WHEN tc.userIdOne = @userId THEN tup2.avatar ELSE tup1.avatar END as otherAvatar, \n"
							+ "@userId = tm2.sender as isSender, \n"
							+ "tm2.`time` as latestTime, tm2.content as latestMessage FROM tblConversation tc \n"
							+ "LEFT OUTER JOIN (SELECT tm.conversationId, MAX(messageId) as latest FROM tblMessage tm GROUP BY tm.conversationId) re \n"
							+ "ON tc.conversationId = re.conversationId \n"
							+ "LEFT OUTER JOIN tblMessage tm2 \n"
							+ "ON tc.conversationId = tm2.conversationId AND tm2.messageId = re.latest \n"
							+ "INNER JOIN tblUserProfile tup1 \n"
							+ "ON tc.userIdOne = tup1.userId \n"
							+ "INNER JOIN tblUserProfile tup2 \n"
							+ "ON tc.userIdTwo = tup2.userId \n"
							+ "WHERE tc.userIdOne = @userId OR tc.userIdTwo = @userId",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.execute("SET @userId = " + userId + ";");
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindByTwoUserId implements Criteria {
		private int userId;
		private int otherId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT tc.conversationId, \n"
							+ "CASE WHEN tc.userIdOne = @userId THEN tc.userIdTwo ELSE tc.userIdOne END as otherId, \n"
							+ "CASE WHEN tc.userIdOne = @userId THEN tup2.name ELSE tup1.name END as otherName, \n"
							+ "CASE WHEN tc.userIdOne = @userId THEN tup2.avatar ELSE tup1.avatar END as otherAvatar, \n"
							+ "@userId = tm2.sender as isSender, \n"
							+ "tm2.`time` as latestTime, tm2.content as latestMessage FROM tblConversation tc \n"
							+ "LEFT OUTER JOIN (SELECT tm.conversationId, MAX(messageId) as latest FROM tblMessage tm GROUP BY tm.conversationId) re \n"
							+ "ON tc.conversationId = re.conversationId \n"
							+ "LEFT OUTER JOIN tblMessage tm2 \n"
							+ "ON tc.conversationId = tm2.conversationId AND tm2.messageId = re.latest \n"
							+ "INNER JOIN tblUserProfile tup1 \n"
							+ "ON tc.userIdOne = tup1.userId \n"
							+ "INNER JOIN tblUserProfile tup2 \n"
							+ "ON tc.userIdTwo = tup2.userId \n"
							+ "WHERE (tc.userIdOne = @userId AND tc.userIdTwo = @otherId) \n"
							+ "OR (tc.userIdOne =  @otherId AND tc.userIdTwo = @userId)",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.execute("SET @userId = " + userId + ";");
			stmt.execute("SET @otherId = " + otherId + ";");
			return stmt.executeQuery();
		}
	}
}
