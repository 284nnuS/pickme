package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse.Error;
import tech.zoomidsoon.pickme_restful_api.mappers.MessageRowMapper;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.models.Message;

public class MessageRepository implements Repository<Message> {
	private static final Repository<Message> singleton = new MessageRepository();

	private MessageRepository() {
	}

	public static Repository<Message> getInstance() {
		return singleton;
	}

	@Override
	public Result<Message, Error> create(Connection conn, Message message) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO tblMessage (conversationId, time, content, sender) VALUES (?,?,?,?)",
				Statement.RETURN_GENERATED_KEYS)) {
			stmt.setLong(1, message.getConversationId());
			stmt.setTimestamp(2, new Timestamp(message.getTime()));
			stmt.setString(3, message.getContent());
			stmt.setInt(4, message.getSender());

			if (stmt.executeUpdate() != 1)
				return new Result<>(null, JsonAPIResponse.SERVER_ERROR);

			// Get id of recently created message
			try (ResultSet rs = stmt.getGeneratedKeys()) {
				rs.next();
				message.setMessageId(rs.getLong(1));
			}

			return new Result<>(message, null);
		}
	}

	@Override
	public List<Message> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(MessageRepository.class)) {
			throw new IllegalArgumentException("Criteria does not belong to the same nest host class");
		}

		try (ResultSet rs = criteria.query(conn)) {
			return MessageRowMapper.getInstance().processResultSet(rs, Message.class);
		}
	}

	@Override
	public Result<Message, Error> update(Connection conn, Message message) throws Exception {
		// Cannot update if messageId is missing
		if (message.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "messageId is required", ""));

		FindById fid = new FindById(message.getMessageId());
		List<Message> list = this.read(conn, fid);

		if (list.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "Message does not exist", ""));

		Message inDB = list.get(0);

		if (message.getReact() != "none" && inDB.getContent() == null)
			return new Result<>(null, new JsonAPIResponse.Error(400, "Can't react to deleted message'", ""));

		if (message.getContent() != null && !message.getContent().equals(inDB.getContent()))
			return new Result<>(null, new JsonAPIResponse.Error(400, "Not allow to change content of message'", ""));
		Message newMessage = inDB;
		if (message.getReact() == null)
			newMessage.setContent(message.getContent());
		else
			newMessage.setReact(message.getReact());

		try (PreparedStatement stmt = conn.prepareStatement(
				"UPDATE tblMessage SET content = ?, react = ? WHERE messageId = ?")) {
			stmt.setString(1, newMessage.getContent());
			stmt.setString(2, newMessage.getReact());
			stmt.setLong(3, newMessage.getMessageId());

			if (stmt.executeUpdate() > 0)
				return new Result<>(newMessage, null);

			return new Result<>(null, new JsonAPIResponse.Error(400, "messageId not found", ""));
		}
	}

	@Override
	public Result<Message, Error> delete(Connection conn, Message message) throws Exception {
		throw new UnsupportedOperationException("Message Repository does not support deleting");
	}

	@Override
	public List<Message> readAll(Connection conn) throws Exception {
		throw new UnsupportedOperationException("Message Repository does not support readAll");
	}

	@AllArgsConstructor
	public static class FindById implements Criteria {
		private long messageId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblMessage WHERE messageId = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setLong(1, messageId);
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindByConversationIdAndTime implements Criteria {
		private long conversationId;
		private long time;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblMessage WHERE conversationId = ? AND time < ? ORDER BY time DESC LIMIT 25",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setLong(1, conversationId);
			stmt.setTimestamp(2, new Timestamp(time));
			return stmt.executeQuery();
		}
	}
}
