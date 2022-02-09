package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

<<<<<<<<< Temporary merge branch 1
import com.mysql.cj.Query;

=========
>>>>>>>>> Temporary merge branch 2
import tech.zoomidsoon.pickme_restful_api.mappers.UserRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.User;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

public class UserRepository implements Repository<User> {

	@Override
	public User create(User entity) {
<<<<<<<<< Temporary merge branch 1
		// TODO Auto-generated method stub
=========
		try {
			try (Connection connection = DBContext.getConnection()) {
				try (PreparedStatement stmt = connection.prepareStatement(
						"insert into tbluse (userid,role,email,name,gender,avatar,bio) values (?,?,?,?,?,?,?)")) {

					stmt.setString(1, entity.getUserId());
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
>>>>>>>>> Temporary merge branch 2
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
<<<<<<<<< Temporary merge branch 1
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
=========
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User delete(User entity) {
		try {
			try (Connection connection = DBContext.getConnection()) {
				try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM tbluse WHERE userid like ?")) {
					stmt.setString(1, entity.getUserId());
					ResultSet rs = stmt.executeQuery();
					UserRowMapper urm = new UserRowMapper();
					List<User> users = urm.processResultSet(rs, User.class);

					return users.size() > 0 ? users.get(0) : null;
				}
			}
		} catch (Exception e) {
		}
>>>>>>>>> Temporary merge branch 2
		return null;
	}

	@Override
	public List<User> readAll() {
		try {
			try (Connection connection = DBContext.getConnection()) {
<<<<<<<<< Temporary merge branch 1

=========
>>>>>>>>> Temporary merge branch 2
				try (PreparedStatement stmt = connection.prepareStatement("select * from tbluser")) {
					ResultSet rs = stmt.executeQuery();
					UserRowMapper urm = new UserRowMapper();
					return urm.processResultSet(rs, User.class);
				}
<<<<<<<<< Temporary merge branch 1
			} catch (SQLException e) {

=========
>>>>>>>>> Temporary merge branch 2
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static class FindById implements Criteria {

		private String userId;

<<<<<<<<< Temporary merge branch 1
		public FindById(String userId) {
			this.userId = userId;
		}

=========
>>>>>>>>> Temporary merge branch 2
		@Override
		public ResultSet query(Connection conn) {
			try {
				try (PreparedStatement stmt = conn.prepareStatement("select * from tbluser where userid like ?")) {
					stmt.setString(1, userId);
<<<<<<<<< Temporary merge branch 1
					ResultSet rs = stmt.executeQuery();
					return rs;
=========
					return stmt.executeQuery();
>>>>>>>>> Temporary merge branch 2
				}
			} catch (SQLException e) {
			}
			return null;
		}
<<<<<<<<< Temporary merge branch 1

=========
>>>>>>>>> Temporary merge branch 2
	}

	public static class FindByName implements Criteria {

<<<<<<<<< Temporary merge branch 1
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
=========
		@Override
		public ResultSet query(Connection conn) {
			// TODO Auto-generated method stub
>>>>>>>>> Temporary merge branch 2
			return null;
		}

	}

	public static class FindByEmail implements Criteria {

<<<<<<<<< Temporary merge branch 1
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
=========
		@Override
		public ResultSet query(Connection conn) {
			// TODO Auto-generated method stub
>>>>>>>>> Temporary merge branch 2
			return null;
		}
	}
}
