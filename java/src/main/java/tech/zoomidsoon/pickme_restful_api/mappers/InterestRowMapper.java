package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import tech.zoomidsoon.pickme_restful_api.models.Interest;

public class InterestRowMapper extends RowMapper<Interest> {
	private static final RowMapper<Interest> singleton = new InterestRowMapper();

	private InterestRowMapper() {
	}

	public static RowMapper<Interest> getInstance() {
		return singleton;
	}

	@Override
	public Boolean mapRow(ResultSet rs, Interest obj) throws SQLException {
		String interestName = rs.getString("interestName");

		obj.setInterestName(interestName);
		obj.setDescription(rs.getString("description"));

		return null;
	}
}
