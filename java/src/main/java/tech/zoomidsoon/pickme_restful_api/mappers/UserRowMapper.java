package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import tech.zoomidsoon.pickme_restful_api.models.User;

public class UserRowMapper extends RowMapper<User> {
	private static final RowMapper<User> singleton = new UserRowMapper();

	private UserRowMapper() {
	}

	public static RowMapper<User> getInstance() {
		return singleton;
	}

	@Override
	public Boolean mapRow(ResultSet rs, User obj) throws SQLException {
		int userId = rs.getInt("userId");

		obj.setUserId(userId);
		obj.setEmail(rs.getString("email"));
		obj.setRole(rs.getString("role"));
		obj.setCautionTimes(rs.getInt("cautionTimes"));
		obj.setDisabled(rs.getBoolean("disabled"));

		return null;
	}
}
