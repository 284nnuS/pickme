package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.mappers.UserRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.User;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;
import tech.zoomidsoon.pickme_restful_api.utils.Utils;

public class UserRepository implements Repository<User> {
	private static final Repository<User> singleton = new UserRepository();

	private UserRepository() {
	}

	public static Repository<User> getInstance() {
		return singleton;
	}

	@Override
	public User create(User entity) throws Exception {
		try (Connection connection = DBContext.getConnection()) {
			try (PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO tbluser (role,email,name,gender,avatar,bio) VALUES (?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS)) {
				stmt.setString(1, entity.getRole());
				stmt.setString(2, entity.getEmail());
				stmt.setString(3, entity.getName());
				stmt.setString(4, Character.toString(entity.getGender()));
				stmt.setString(5, entity.getAvatar());
				stmt.setString(6, entity.getBio());

				if (stmt.executeUpdate() == 0)
					return null;

				try (ResultSet rs = stmt.getGeneratedKeys()) {
					rs.next();
					entity.setUserId(rs.getInt(1));
					return entity;
				}
			}
		}
	}

	@Override
	public List<User> read(Criteria criteria) throws Exception {
		try (Connection connection = DBContext.getConnection()) {
			try (ResultSet rs = criteria.query(connection)) {
				return UserRowMapper.getInstance().processResultSet(rs, User.class);
			}
		}
	}

	@Override
	public User update(User entity) throws Exception {
		try (Connection connection = DBContext.getConnection()) {
			if (entity.isEmpty())
				return null;

			FindById fid = new FindById(entity.getUserId());
			List<User> list = UserRowMapper.getInstance().processResultSet(fid.query(connection), User.class);

			if (list.size() == 0)
				return null;

			User inDB = list.get(0);

			Utils.copyNonNullFields(inDB, entity);

			try (PreparedStatement stmt = connection.prepareStatement(
					"UPDATE tbluser SET name = ?, avatar = ?, bio = ?, gender = ? WHERE userid = ?")) {
				stmt.setInt(5, inDB.getUserId());
				stmt.setString(1, inDB.getName());
				stmt.setString(2, inDB.getAvatar());
				stmt.setString(3, inDB.getBio());
				stmt.setString(4, Character.toString(inDB.getGender()));

				if (stmt.executeUpdate() > 0)
					return inDB;
			}
		}
		return null;
	}

	@Override
	public User delete(User entity) throws Exception {
		try {
			try (Connection connection = DBContext.getConnection()) {
				FindById fid = new FindById(entity.getUserId());
				List<User> list = UserRowMapper.getInstance().processResultSet(fid.query(connection), User.class);

				if (list.size() == 0)
					return null;

				User user = list.get(0);

				try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM tbluser WHERE userid like ?")) {
					stmt.setInt(1, entity.getUserId());

					if (stmt.executeUpdate() > 0)
						return user;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public List<User> readAll() throws Exception {
		try (Connection connection = DBContext.getConnection()) {
			try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM tbluser")) {
				try (ResultSet rs = stmt.executeQuery()) {
					return UserRowMapper.getInstance().processResultSet(rs, User.class);
				}
			}
		}
	}

	@AllArgsConstructor
	public static class FindById implements Criteria {
		private int userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tbluser WHERE userid LIKE ?");
			stmt.setInt(1, userId);
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindByName implements Criteria {
		private String userName;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tbluser WHERE name LIKE '%?%'");
			stmt.setString(1, userName);
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindByEmail implements Criteria {
		private String email;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tbluser WHERE email LIKE ?");
			stmt.setString(1, email);
			return stmt.executeQuery();
		}
	}
}
