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

		FindById fid = new FindById(message.getMessageId());
		List<Message> list = this.read(conn, fid);

		if (list.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "Message does not exist", ""));

		Message inDB = list.get(0);

		if (message.getReact() != null && inDB.getContent() == null)
			return new Result<>(null, new JsonAPIResponse.Error(400, "Can't react to deleted message'", ""));

		if (message.getContent() != null && !message.getContent().equals(inDB.getContent()))
			return new Result<>(null, new JsonAPIResponse.Error(400, "Not allow to change content of message'", ""));

		Utils.copyNonNullFields(inDB, message, "messageId", "time", "sender, receiver");

		try (PreparedStatement stmt = conn.prepareStatement(
				"UPDATE tblMessage SET content = ?, react = ? WHERE messageId = ?")) {
			stmt.setString(1, message.getContent());
			stmt.setString(2, message.getReact());
			stmt.setLong(3, message.getMessageId());

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
	public static class FindLatestMessageByUserId implements Criteria {
		private int userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblMessage tm \n"
							+ "INNER JOIN (SELECT tms1.userIdTwo as matchedId FROM tblMatchStatus tms1 \n"
							+ "INNER JOIN tblMatchStatus tms2 \n"
							+ "ON tms1.userIdOne = tms2.userIdTwo AND tms1.userIdTwo = tms2.userIdOne AND tms1.`like` = 1 AND tms2.`like` = 1 AND tms1.userIdOne = ?) ma \n"
							+ "ON tm.sender = ma.matchedId OR tm.receiver = ma.matchedId \n"
							+ "WHERE tm.sender = ? OR tm.receiver = ? \n"
							+ "ORDER BY tm.`time` DESC \n"
							+ "LIMIT 1",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, userId);
			stmt.setInt(2, userId);
			stmt.setInt(3, userId);
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
