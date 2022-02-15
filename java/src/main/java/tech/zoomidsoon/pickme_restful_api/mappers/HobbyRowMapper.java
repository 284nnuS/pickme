package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import tech.zoomidsoon.pickme_restful_api.models.Hobby;

public class HobbyRowMapper extends RowMapper<Hobby> {
	private static final RowMapper<Hobby> singleton = new HobbyRowMapper();

	private HobbyRowMapper() {
	}

	public static RowMapper<Hobby> getInstance() {
		return singleton;
	}

	@Override
	public boolean mapRow(ResultSet rs, Hobby obj) throws SQLException {
		String hobbyName = rs.getString("hobbyName");

		if (!obj.isEmpty() && hobbyName != null && !hobbyName.equals(obj.getHobbyName()))
			return true;

		if (obj.isEmpty()) {
			obj.setHobbyName(hobbyName);
			obj.setDescription(rs.getString("description"));
		}

		return false;
	}
}
