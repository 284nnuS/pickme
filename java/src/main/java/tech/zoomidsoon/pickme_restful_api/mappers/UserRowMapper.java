package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import tech.zoomidsoon.pickme_restful_api.models.User;

public class UserRowMapper extends RowMapper<User> {

	@Override
	boolean mapRow(ResultSet rs, User obj, boolean first, boolean isNew) throws SQLException {
		int userId = rs.getInt("userId");

		if (!isNew && userId != obj.getUserId())
			return true;

		obj.setUserId(userId);
		obj.setName(rs.getString("name"));
		obj.setAvatar(rs.getString("avatar"));
		obj.setBio(rs.getString("bio"));
		obj.setEmail(rs.getString("email"));
		obj.setGender(rs.getString("gender").charAt(0));
		obj.setRole(rs.getString("role"));
		return false;
	}
}
