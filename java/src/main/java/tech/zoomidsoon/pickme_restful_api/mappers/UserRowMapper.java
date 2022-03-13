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
			obj.setBirthday(rs.getDate("birthday").getTime());
			obj.setAvatar(rs.getString("avatar"));
			obj.setBio(rs.getString("bio"));
			obj.setEmail(rs.getString("email"));
			obj.setGender(rs.getString("gender"));
			try {
				obj.setRole(rs.getString("role"));
				obj.setCautionTimes(rs.getInt("cautionTimes"));
			} catch (Exception e) {
			}
			obj.setInterests(new ArrayList<>());
			obj.setMedias(new ArrayList<>());
		}

		String interestName = rs.getString("interestName");
		if (interestName != null && !obj.getInterests().contains(interestName))
			obj.getInterests().add(interestName);

		Media media = new Media();
		MediaRowMapper.getInstance().mapRow(rs, media);
		if (!media.isEmpty() && !obj.getMedias().contains(media))
			obj.getMedias().add(media);

		return false;
	}
}
