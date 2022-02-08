package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.mysql.cj.Query;

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
		try {
			try (Connection connection = DBContext.getConnection()) {

				try (PreparedStatement stmt = connection.prepareStatement("UPDATE tbluser\n"
				+"SET name = '?', avatar= '?', bio ='?', gender = '?'\n"
				+"WHERE userid = '?';")) {
				    stmt.setString(5, entity.getUserId());
					stmt.setString(1, entity.getUsername());
					stmt.setString(2, entity.getAvatar());
					stmt.setString(3, entity.getBio());
					stmt.setString(4, Character.toString(entity.getGender()));
					ResultSet rs = stmt.executeQuery();
					return (User) rs;
				}
			} catch (SQLException e) {

			}
		} catch (Exception e) {
		}
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
		try {
			try (Connection connection = DBContext.getConnection()) {

				try (PreparedStatement stmt = connection.prepareStatement("select * from tbluser")) {
					ResultSet rs = stmt.executeQuery();
					UserRowMapper urm = new UserRowMapper();
					return urm.processResultSet(rs, User.class);
				}
			} catch (SQLException e) {

			}
		} catch (Exception e) {
		}
		return null;
	}

	public static class FindById implements Criteria {

		private String userId;

		public FindById(String userId) {
			this.userId = userId;
		}

		@Override
		public ResultSet query(Connection conn) {
			try {
				try (PreparedStatement stmt = conn.prepareStatement("select * from tbluser where userid like ?")) {
					stmt.setString(1, userId);
					ResultSet rs = stmt.executeQuery();
					return rs;
				}
			} catch (SQLException e) {
			}
			return null;
		}

	}

	public static class FindByName implements Criteria {

		private String userName;

		public FindByName(String userName) {
			this.userName = userName;
		}

		@Override
		public ResultSet query(Connection conn) {
			try {
				try (PreparedStatement stmt = conn.prepareStatement("select * from tbluser where name like '%?%'")) {
					stmt.setString(1, userName);
					ResultSet rs = stmt.executeQuery();
					return rs;
				}
			} catch (SQLException e) {
			}
			return null;
		}

	}

	public static class FindByEmail implements Criteria {

		private String email;

		public FindByEmail(String email) {
			this.email = email;
		}

		@Override
		public ResultSet query(Connection conn) {
			try {
				try (PreparedStatement stmt = conn.prepareStatement("select * from tbluser where email like ?")) {
					stmt.setString(1, email);
					ResultSet rs = stmt.executeQuery();
					return rs;
				}
			} catch (SQLException e) {
			}
			return null;
		}
	}
}
