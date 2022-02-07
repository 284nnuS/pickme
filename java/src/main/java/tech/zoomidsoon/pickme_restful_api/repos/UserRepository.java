package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import tech.zoomidsoon.pickme_restful_api.mappers.UserRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.User;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

public class UserRepository implements Repository<User> {

	@Override
	public User create(User entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> read(Criteria criteria) {
		try {
			try (Connection connection = DBContext.getConnection()) {
				ResultSet result = criteria.query(connection);
				UserRowMapper urm = new UserRowMapper();
				return urm.processResultSet(result, User.class);
			}
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public User update(User entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User delete(User entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> readAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public static class FindById implements Criteria {

		@Override
		public ResultSet query(Connection conn) {
			try {
				try (PreparedStatement stmt = conn.prepareStatement("select * from tbluser")) {
					ResultSet rs = stmt.executeQuery();
					return rs;
				}
			} catch (SQLException e) {
			}
			return null;
		}

	}
}
