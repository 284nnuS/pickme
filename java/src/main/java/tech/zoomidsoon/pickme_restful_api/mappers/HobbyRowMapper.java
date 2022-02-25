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
	public Boolean mapRow(ResultSet rs, Hobby obj) throws SQLException {
		String hobbyName = rs.getString("hobbyName");

		obj.setHobbyName(hobbyName);
		obj.setDescription(rs.getString("description"));

		return null;
	}
}
