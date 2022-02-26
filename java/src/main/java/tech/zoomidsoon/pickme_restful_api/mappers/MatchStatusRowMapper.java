package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import tech.zoomidsoon.pickme_restful_api.models.MatchStatus;

public class MatchStatusRowMapper extends RowMapper<MatchStatus> {
	private static final RowMapper<MatchStatus> singleton = new MatchStatusRowMapper();

	private MatchStatusRowMapper() {
	}

	public static RowMapper<MatchStatus> getInstance() {
		return singleton;
	}

	@Override
	public Boolean mapRow(ResultSet rs, MatchStatus obj) throws SQLException {
		Integer userIdOne = rs.getInt("userIdOne");
		Integer userIdTwo = rs.getInt("userIdTwo");

		obj.setUserIdOne(userIdOne);
		obj.setUserIdTwo(userIdTwo);
		obj.setLike(rs.getBoolean("like"));

		return null;
	}
}
