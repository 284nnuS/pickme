package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import tech.zoomidsoon.pickme_restful_api.models.MatchedUser;
import tech.zoomidsoon.pickme_restful_api.models.UserProfile;

public class UserProfileRowMapper extends RowMapper<UserProfile> {
	private static final RowMapper<UserProfile> singleton = new UserProfileRowMapper();

	private UserProfileRowMapper() {
	}

	public static RowMapper<UserProfile> getInstance() {
		return singleton;
	}

	@Override
	public Boolean mapRow(ResultSet rs, UserProfile obj) throws SQLException {
		int userId = rs.getInt("userId");

		if (!obj.isEmpty() && userId != obj.getUserId())
			return true;

		if (obj.isEmpty()) {
			obj.setUserId(userId);
			obj.setName(rs.getString("name"));
			obj.setGender(rs.getString("gender"));
			obj.setAvatar(rs.getString("avatar"));
			obj.setBio(rs.getString("bio"));
			obj.setBirthday(rs.getDate("birthday").getTime());
			obj.setAddress(rs.getString("address"));
			obj.setStatusEmoji(rs.getString("statusEmoji"));
			obj.setStatusText(rs.getString("statusText"));
			obj.setPhone(rs.getString("phone"));
			obj.setInterests(new ArrayList<>());
			obj.setMatches(new ArrayList<>());
			try {
				obj.setLikes(rs.getInt("likes"));
			} catch (Exception e) {
			}
		}

		String interestName = rs.getString("interestName");
		if (interestName != null && !obj.getInterests().contains(interestName))
			obj.getInterests().add(interestName);

		try {
			int matchedId = rs.getInt("matchedId");
			if (!rs.wasNull() && obj.getMatches().stream().filter(el -> el.getUserId() == matchedId).count() == 0)
				obj.getMatches().add(new MatchedUser(matchedId, rs.getString("matchedName"),
						rs.getString("matchedAvatar")));
		} catch (

		Exception e) {
		}

		return false;
	}
}
