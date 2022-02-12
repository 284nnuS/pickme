package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import tech.zoomidsoon.pickme_restful_api.models.Hobby;
import tech.zoomidsoon.pickme_restful_api.models.User;

public class UserRowMapper extends RowMapper<User> {
	private static final RowMapper<User> singleton = new UserRowMapper();

	private UserRowMapper() {
	}

	public static RowMapper<User> getInstance() {
		return singleton;
	}

	@Override
	public boolean mapRow(ResultSet rs, User obj) throws SQLException {
		int userId = rs.getInt("userId");

		if (!obj.isEmpty() && userId != obj.getUserId())
			return true;

		if (obj.isEmpty()) {
			obj.setUserId(userId);
			obj.setName(rs.getString("name"));
			obj.setAvatar(rs.getString("avatar"));
			obj.setBio(rs.getString("bio"));
			obj.setEmail(rs.getString("email"));
			obj.setGender(rs.getString("gender").charAt(0));
			obj.setRole(rs.getString("role"));
		}
		
		Hobby hobby = new Hobby();
		HobbyRowMapper.getInstance().mapRow(rs, hobby);
		if(!hobby.isEmpty())
			obj.getHobbies().add(hobby);
		
		return false;
	}
}
