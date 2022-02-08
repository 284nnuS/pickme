package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import tech.zoomidsoon.pickme_restful_api.models.User;

public class UserRowMapper extends RowMapper<User> {

	@Override
	boolean mapRow(ResultSet rs, User obj, boolean first, boolean isNew) throws SQLException {
		String username = rs.getString("username");
		if (!isNew && username != obj.getUsername())
			return true;
		return false;
	}
}
