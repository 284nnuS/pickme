package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse.Error;
import tech.zoomidsoon.pickme_restful_api.mappers.MessageRowMapper;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.models.Message;
import tech.zoomidsoon.pickme_restful_api.utils.Utils;

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
				"INSERT INTO tblMessage (time, sender, receiver, content, react) VALUES (?,?,?,?,?)",
				Statement.RETURN_GENERATED_KEYS)) {
			Timestamp now = Timestamp.from(Instant.now());
			message.setTime(now.getTime());
			stmt.setTimestamp(1, now);
			stmt.setInt(2, message.getSender());
			stmt.setInt(3, message.getReceiver());
			stmt.setString(4, message.getContent());
			stmt.setString(5, message.getReact());

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

		try (PreparedStatement stmt = conn.prepareStatement(
				"UPDATE tblMessage SET react = ? WHERE messageId = ?")) {
			stmt.setString(1, message.getReact());
			stmt.setLong(2, message.getMessageId());

			if (stmt.executeUpdate() > 0)
				return new Result<>(message, null);

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
		private Long messageId;

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

	@NoArgsConstructor
	@Getter
	@Setter
	public static class FindByTimeAndUserId implements Criteria {
		private long time;
		private int userIdOne;
		private int userIdTwo;
		private int num;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblMessage WHERE ((sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)) AND time < ? ORDER BY time DESC LIMIT "
							+ num,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Timestamp timestamp = new Timestamp(time);
			stmt.setInt(1, userIdOne);
			stmt.setInt(2, userIdTwo);
			stmt.setInt(3, userIdTwo);
			stmt.setInt(4, userIdOne);
			stmt.setTimestamp(5, timestamp);
			return stmt.executeQuery();
		}
	}
}
