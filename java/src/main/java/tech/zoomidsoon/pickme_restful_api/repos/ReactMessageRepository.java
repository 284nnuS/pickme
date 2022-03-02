package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse.Error;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.mappers.ReactMessageRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.ReactMessage;
import tech.zoomidsoon.pickme_restful_api.utils.Utils;

public class ReactMessageRepository implements Repository<ReactMessage> {
	private static final Repository<ReactMessage> singleton = new ReactMessageRepository();

	public ReactMessageRepository() {
	}

	public static Repository<ReactMessage> getInstance() {
		return singleton;
	}

	@Override
	public Result<ReactMessage, Error> create(Connection conn, ReactMessage reactMessage) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO tblReactMessage (messageId, react) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)) {
			stmt.setInt(1, reactMessage.getMessageId());
			stmt.setString(2, reactMessage.getReact());

			if (stmt.executeUpdate() == 0)
				return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));

			return new Result<>(reactMessage, null);
		}
	}

	@Override
	public List<ReactMessage> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(ReactMessageRepository.class)) {
			throw new IllegalArgumentException("Criteria does not belong to the same nest host class");
		}

		try (ResultSet rs = criteria.query(conn)) {
			return ReactMessageRowMapper.getInstance().processResultSet(rs, ReactMessage.class);
		}
	}

	@Override
	public Result<ReactMessage, Error> update(Connection conn, ReactMessage reactMessage) throws Exception {
		if (reactMessage.isEmpty())
			return null;

		FindByMessageId fin = new FindByMessageId(reactMessage.getMessageId());
		List<ReactMessage> list = ReactMessageRowMapper.getInstance().processResultSet(fin.query(conn),
				ReactMessage.class);

		if (list.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "This react message does not exist", ""));

		ReactMessage inDB = list.get(0);

		Utils.copyNonNullFields(inDB, reactMessage);

		try (PreparedStatement stmt = conn
				.prepareStatement("UPDATE tblReactMessage SET react = ? WHERE messageId = ?")) {
			stmt.setString(1, inDB.getReact());
			stmt.setInt(2, inDB.getMessageId());

			if (stmt.executeUpdate() > 0)
				return new Result<>(inDB, null);
		}

		return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));
	}

	@Override
	public Result<ReactMessage, Error> delete(Connection conn, ReactMessage reactMessage) throws Exception {
		try {
			FindByMessageId fin = new FindByMessageId(reactMessage.getMessageId());
			List<ReactMessage> list = ReactMessageRowMapper.getInstance().processResultSet(fin.query(conn),
					ReactMessage.class);

			if (list.isEmpty())
				return new Result<>(null, new JsonAPIResponse.Error(400, "This react message does not exist", ""));

			reactMessage = list.get(0);

			try (PreparedStatement stmt = conn
					.prepareStatement("DELETE FROM tblReactMessage WHERE messageId = ? and react = ?")) {
				stmt.setInt(1, reactMessage.getMessageId());
				stmt.setString(2, reactMessage.getReact());

				if (stmt.executeUpdate() > 0)
					return new Result<>(reactMessage, null);
			}
		} catch (Exception e) {
		}

		return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));
	}

	@Override
	public List<ReactMessage> readAll(Connection conn) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblReactMessage",
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
			try (ResultSet rs = stmt.executeQuery()) {
				return ReactMessageRowMapper.getInstance().processResultSet(rs, ReactMessage.class);
			}
		}
	}

	@AllArgsConstructor
	public static class FindByMessageId implements Criteria {
		private Integer id;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblReactMessage WHERE messageId = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, id);
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindByMessageIdList implements Criteria {
		private List<Integer> messageIds;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			String query = String.format("SELECT * FROM tblReactMessage WHERE messageId =?",
					messageIds.stream().map(el -> "?").collect(Collectors.joining(", ")));

			PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			for (int i = 0; i < messageIds.size(); i++)
				stmt.setInt(i + 1, messageIds.get(i));

			return stmt.executeQuery();
		}
	}

}
