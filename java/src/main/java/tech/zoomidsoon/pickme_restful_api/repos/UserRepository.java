package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.mappers.UserRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.User;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

public class UserRepository implements Repository<User> {

	@Override
	public User create(User entity) {
		try {
			try (Connection connection = DBContext.getConnection()) {
				try (PreparedStatement stmt = connection.prepareStatement(
						"insert into tbluse (userid,role,email,name,gender,avatar,bio) values (?,?,?,?,?,?,?)")) {

					stmt.setInt(1, entity.getUserId());
					stmt.setString(2, entity.getRole());
					stmt.setString(3, entity.getEmail());
					stmt.setString(4, entity.getName());
					stmt.setString(5, Character.toString(entity.getGender()));
					stmt.setString(6, entity.getAvatar());
					stmt.setString(7, entity.getBio());

					if (stmt.executeUpdate() > 0)
						return entity;

					return null;
				}
			}
		} catch (Exception e) {
		}
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
		try {
			try (Connection connection = DBContext.getConnection()) {
				try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM tbluse WHERE userid like ?")) {
					stmt.setInt(1, entity.getUserId());
					ResultSet rs = stmt.executeQuery();
					UserRowMapper urm = new UserRowMapper();
					List<User> users = urm.processResultSet(rs, User.class);

					return users.size() > 0 ? users.get(0) : null;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public List<User> readAll() {
		try {
			try (Connection connection = DBContext.getConnection()) {
				try (PreparedStatement stmt = connection.prepareStatement("select * from tbluser")) {
					ResultSet rs = stmt.executeQuery();
					UserRowMapper urm = new UserRowMapper();
					return urm.processResultSet(rs, User.class);
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	@NoArgsConstructor
	public static class FindById implements Criteria {
		private String userId;

		@Override
		public ResultSet query(Connection conn) {
			try {
				try (PreparedStatement stmt = conn.prepareStatement("select * from tbluser where userid like ?")) {
					stmt.setString(1, userId);
					return stmt.executeQuery();
				}
			} catch (SQLException e) {
			}
			return null;
		}
	}

	@NoArgsConstructor
	public static class FindByName implements Criteria {
		@Override
		public ResultSet query(Connection conn) {
			return null;
		}
	}

	@NoArgsConstructor
	public static class FindByEmail implements Criteria {
		@Override
		public ResultSet query(Connection conn) {
			return null;
		}
	}
}
