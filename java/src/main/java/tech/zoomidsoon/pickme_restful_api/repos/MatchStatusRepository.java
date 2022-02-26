package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse.Error;
import tech.zoomidsoon.pickme_restful_api.mappers.MatchStatusRowMapper;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.models.MatchStatus;

public class MatchStatusRepository implements Repository<MatchStatus> {
	private static final Repository<MatchStatus> singleton = new MatchStatusRepository();

	private MatchStatusRepository() {
	}

	public static Repository<MatchStatus> getInstance() {
		return singleton;
	}

	@Override
	public Result<MatchStatus, Error> create(Connection conn, MatchStatus matchStatus) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO tblMatchStatus (userIdOne, userIdTwo, `like`) VALUES (?,?,?)")) {
			stmt.setInt(1, matchStatus.getUserIdOne());
			stmt.setInt(2, matchStatus.getUserIdTwo());
			stmt.setBoolean(3, matchStatus.getLike());

			if (stmt.executeUpdate() != 1)
				return new Result<>(null, JsonAPIResponse.SERVER_ERROR);

			return new Result<>(matchStatus, null);
		}
	}

	@Override
	public List<MatchStatus> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(MatchStatusRepository.class)) {
			throw new IllegalArgumentException("Criteria does not belong to the same nest host class");
		}

		try (ResultSet rs = criteria.query(conn)) {
			return MatchStatusRowMapper.getInstance().processResultSet(rs, MatchStatus.class);
		}
	}

	@Override
	public Result<MatchStatus, Error> update(Connection conn, MatchStatus matchStatus) throws Exception {
		throw new UnsupportedOperationException("MatchStatus Repository does not support updating");
	}

	@Override
	public Result<MatchStatus, Error> delete(Connection conn, MatchStatus matchStatus) throws Exception {
		throw new UnsupportedOperationException("MatchStatus Repository does not support deleting");
	}

	@Override
	public List<MatchStatus> readAll(Connection conn) throws Exception {
		throw new UnsupportedOperationException("MatchStatus Repository does not support readAll");
	}

	@AllArgsConstructor
	public static class FindByUserId implements Criteria {
		private int userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblMatchStatus WHERE userIdOne = ? OR userIdTwo = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, userId);
			stmt.setInt(2, userId);
			return stmt.executeQuery();
		}
	}
}
