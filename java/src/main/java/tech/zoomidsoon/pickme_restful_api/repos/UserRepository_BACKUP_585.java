package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

<<<<<<< HEAD
=======
import lombok.*;

>>>>>>> origin/master
import tech.zoomidsoon.pickme_restful_api.mappers.UserRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.User;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;
import tech.zoomidsoon.pickme_restful_api.utils.Utils;

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
<<<<<<< HEAD
		// TODO Auto-generated method stub
=======
		try {
			try (Connection connection = DBContext.getConnection()) {
				FindById fid = new FindById(entity.getUserId());
				UserRowMapper urm = new UserRowMapper();
				List<User> list = urm.processResultSet(fid.query(connection), User.class);

				if (list == null || list.size() == 0)
					return null;

				User inDB = list.get(0);

				Utils.copyNonNullFields(inDB, entity);

				try (PreparedStatement stmt = connection.prepareStatement(
						"UPDATE tbluser\n"
								+ "SET name = ?, avatar= ?, bio =?, gender = ?\n"
								+ "WHERE userid = ?")) {
					stmt.setInt(5, inDB.getUserId());
					stmt.setString(1, inDB.getName());
					stmt.setString(2, inDB.getAvatar());
					stmt.setString(3, inDB.getBio());
					stmt.setString(4, Character.toString(inDB.getGender()));

					if (stmt.executeUpdate() > 0)
						return inDB;
				}
			}
		} catch (Exception e) {
		}
>>>>>>> origin/master
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

					if (users.size() > 0)
						return users.get(0);
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

	@AllArgsConstructor
	public static class FindById implements Criteria {
		private int userId;

<<<<<<< HEAD
		private String userId;

=======
>>>>>>> origin/master
		@Override
		public ResultSet query(Connection conn) {
			try {
				try (PreparedStatement stmt = conn.prepareStatement("select * from tbluser where userid like ?")) {
<<<<<<< HEAD
					stmt.setString(1, userId);
=======
					stmt.setInt(1, userId);
>>>>>>> origin/master
					return stmt.executeQuery();
				}
			} catch (SQLException e) {
			}
			return null;
		}
	}

	@AllArgsConstructor
	public static class FindByName implements Criteria {
<<<<<<< HEAD

		@Override
		public ResultSet query(Connection conn) {
			// TODO Auto-generated method stub
=======
		private String userName;

		@Override
		public ResultSet query(Connection conn) {
			try {
				try (PreparedStatement stmt = conn.prepareStatement("select * from tbluser where name like '%?%'")) {
					stmt.setString(1, userName);
					return stmt.executeQuery();
				}
			} catch (SQLException e) {
			}
>>>>>>> origin/master
			return null;
		}
	}

	@AllArgsConstructor
	public static class FindByEmail implements Criteria {
<<<<<<< HEAD

		@Override
		public ResultSet query(Connection conn) {
			// TODO Auto-generated method stub
=======
		private String email;

		@Override
		public ResultSet query(Connection conn) {
			try {
				try (PreparedStatement stmt = conn.prepareStatement("select * from tbluser where email like ?")) {
					stmt.setString(1, email);
					return stmt.executeQuery();
				}
			} catch (SQLException e) {
			}
>>>>>>> origin/master
			return null;
		}
	}
}
