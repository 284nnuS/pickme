package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import tech.zoomidsoon.pickme_restful_api.models.Media;
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

		if (!obj.isEmpty() && userId != obj.getUserId())
			return true;

		if (obj.isEmpty()) {
			obj.setUserId(userId);
			obj.setName(rs.getString("name"));
			obj.setAvatar(rs.getString("avatar"));
			obj.setBio(rs.getString("bio"));
			obj.setEmail(rs.getString("email"));
			obj.setGender(rs.getString("gender"));
			obj.setRole(rs.getString("role"));
			obj.setCautionTimes(rs.getInt("cautionTimes"));
			obj.setHobbies(new ArrayList<>());
			obj.setMedias(new ArrayList<>());
		}

		String hobbyName = rs.getString("hobbyName");
		if (hobbyName != null && !obj.getHobbies().contains(hobbyName))
			obj.getHobbies().add(hobbyName);

		Media media = new Media();
		MediaRowMapper.getInstance().mapRow(rs, media);
		if (!media.isEmpty() && !obj.getMedias().contains(media))
			obj.getMedias().add(media);

		return false;
	}
}
