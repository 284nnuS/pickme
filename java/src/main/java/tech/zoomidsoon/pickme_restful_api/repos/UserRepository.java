package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.mappers.UserRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.User;
import tech.zoomidsoon.pickme_restful_api.repos.HobbyRepository.FindByNameList;
import tech.zoomidsoon.pickme_restful_api.utils.Utils;

public class UserRepository implements Repository<User> {
	private static final Repository<User> singleton = new UserRepository();

	private UserRepository() {
	}

	public static Repository<User> getInstance() {
		return singleton;
	}

	@Override
	public Result<User, JsonAPIResponse.Error> create(Connection conn, User user) throws Exception {
		try {
			if (user.getHobbies() != null && !user.getHobbies().isEmpty()) {
				FindByNameList fbnl = new FindByNameList(user.getHobbies());
				if (HobbyRepository.getInstance().read(conn, fbnl).size() != user.getHobbies().size()) {
					return new Result<>(null, new JsonAPIResponse.Error(500, "Some hobbies are not available", ""));
				}
			}

			conn.setAutoCommit(false);

			// Insert user into database
			try (PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO tblUser (role, email, name, gender, avatar, bio, cautionTimes) VALUES (?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS)) {
				stmt.setString(1, user.getRole());
				stmt.setString(2, user.getEmail());
				stmt.setString(3, user.getName());
				stmt.setString(4, user.getGender());
				stmt.setString(5, user.getAvatar());
				stmt.setString(6, user.getBio());
				stmt.setInt(7, user.getCautionTimes());

				if (stmt.executeUpdate() != 1) {
					conn.rollback();
					return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));
				}

				// Get id of recently created user
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					rs.next();
					user.setUserId(rs.getInt(1));
				}
			}

			// Create User-Hobby relation
			if (user.getHobbies().size() > 0) {
				String query = String.format("INSERT INTO tblUserHobby (userId, hobbyName) VALUES %s",
						user.getHobbies().stream().map(el -> "(?,?)").collect(Collectors.joining(", ")));
				try (PreparedStatement stmt = conn.prepareStatement(query)) {
					int index = 1;

					for (String hobbyName : user.getHobbies()) {
						stmt.setInt(index++, user.getUserId());
						stmt.setString(index++, hobbyName);
					}

					if (stmt.executeUpdate() != user.getHobbies().size()) {
						conn.rollback();
						return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));
					}
				}
			}
			conn.commit();

			return new Result<>(user, null);

		} catch (Exception e) {
			conn.rollback();
			throw e;
		}
	}

	@Override
	public List<User> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(UserRepository.class)) {
			throw new IllegalArgumentException("This criteria is not supported");
		}

		try (ResultSet rs = criteria.query(conn)) {
			return UserRowMapper.getInstance().processResultSet(rs, User.class);
		}
	}

	@Override
	public Result<User, JsonAPIResponse.Error> update(Connection conn, User user) throws Exception {
		// Cannot update if userId is missing
		if (user.isEmpty())
			return null;

		try {
			if (user.getHobbies() != null && !user.getHobbies().isEmpty()) {
				FindByNameList fbnl = new FindByNameList(user.getHobbies());
				if (HobbyRepository.getInstance().read(conn, fbnl).size() != user.getHobbies().size()) {
					return new Result<>(null, new JsonAPIResponse.Error(500, "Some hobbies are not available", ""));
				}
			}

			conn.setAutoCommit(false);

			FindById fid = new FindById(user.getUserId());
			List<User> list = UserRowMapper.getInstance().processResultSet(fid.query(conn), User.class);

			if (list.isEmpty())
				return new Result<>(null, new JsonAPIResponse.Error(404, "User does not exist", ""));

			User inDB = list.get(0);
			boolean hobbyIsChanged = !Utils.equalList(inDB.getHobbies(), user.getHobbies());
			boolean needDeleteBeforeUpdate = inDB.getHobbies().size() > 0 && hobbyIsChanged;
			User newUser = new User(inDB);

			Utils.copyNonNullFields(newUser, user, "email", "userId");

			// Check if having any changes
			if (newUser.equals(inDB))
				return new Result<>(newUser, null);

			// Remove old hobbies in database
			if (needDeleteBeforeUpdate)
				try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM tblUserHobby WHERE userId = ?")) {
					stmt.setInt(1, newUser.getUserId());

					if (stmt.executeUpdate() == 0) {
						conn.rollback();
						return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));
					}
				}

			// Update user in database
			try (PreparedStatement stmt = conn.prepareStatement(
					"UPDATE tblUser SET role = ?, name = ?, gender = ?, avatar = ?, bio = ?, cautionTimes = ? WHERE userId = ?")) {
				stmt.setString(1, newUser.getRole());
				stmt.setString(2, newUser.getName());
				stmt.setString(3, newUser.getGender());
				stmt.setString(4, newUser.getAvatar());
				stmt.setString(5, newUser.getBio());
				stmt.setInt(6, newUser.getCautionTimes());
				stmt.setInt(7, newUser.getUserId());

				if (stmt.executeUpdate() != 1) {
					conn.rollback();
					return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));
				}
			}

			// Create User-Hobby relation
			if (hobbyIsChanged && newUser.getHobbies().size() > 0) {
				String query = String.format("INSERT INTO tblUserHobby (userId, hobbyName) VALUES %s",
						newUser.getHobbies().stream().map(el -> "(?,?)").collect(Collectors.joining(", ")));
				try (PreparedStatement stmt = conn.prepareStatement(query)) {
					int index = 1;

					for (String hobbyName : newUser.getHobbies()) {
						stmt.setInt(index++, newUser.getUserId());
						stmt.setString(index++, hobbyName);
					}

					if (stmt.executeUpdate() != newUser.getHobbies().size()) {
						conn.rollback();
						return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));
					}
				}
			}
			conn.commit();

			return new Result<>(newUser, null);
		} catch (Exception e) {
			conn.rollback();
			throw e;
		}
	}

	@Override
	public Result<User, JsonAPIResponse.Error> delete(Connection conn, User user) throws Exception {
		try {
			conn.setAutoCommit(false);

			FindById fid = new FindById(user.getUserId());
			List<User> list = this.read(conn, fid);

			if (list.isEmpty())
				return new Result<>(null, new JsonAPIResponse.Error(400, "User does not exist", ""));

			user = list.get(0);

			try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM tblUser WHERE userId LIKE ?")) {
				stmt.setInt(1, user.getUserId());

				if (stmt.executeUpdate() != 1) {
					conn.rollback();
					return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));
				}
			}

			conn.commit();

			return new Result<>(user, null);

		} catch (Exception e) {
			conn.rollback();
			throw e;
		}
	}

	@Override
	public List<User> readAll(Connection conn) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement(
				"SELECT a.userId, a.name, a.email, a.role, a.gender, a.bio, a.avatar, a.cautionTimes, b.hobbyName\n"
						+ "FROM tblUser a \n"
						+ "LEFT OUTER JOIN tblUserHobby b ON a.userId = b.userId ",
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY)) {
			try (ResultSet rs = stmt.executeQuery()) {
				return UserRowMapper.getInstance().processResultSet(rs, User.class);
			}
		}
	}

	@AllArgsConstructor
	public static class FindById implements Criteria {
		private int userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT a.userId, a.name, a.email, a.role, a.gender, a.bio, a.avatar, a.cautionTimes, b.hobbyName\n"
							+ "FROM tblUser a \n"
							+ "LEFT OUTER JOIN tblUserHobby b ON a.userId = b.userId \n"
							+ "WHERE a.userId LIKE ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, userId);
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindByName implements Criteria {
		private String name;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT a.userId, a.name, a.email, a.role, a.gender, a.bio, a.avatar, a.cautionTimes, b.hobbyName\n"
							+ "FROM tblUser a \n"
							+ "LEFT OUTER JOIN tblUserHobby b ON a.userId = b.userId \n"
							+ "WHERE a.name LIKE '%?%'",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, name);
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindByEmail implements Criteria {
		private String email;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT a.userId, a.name, a.email, a.role, a.gender, a.bio, a.avatar, a.cautionTimes, b.hobbyName\n"
							+ "FROM tblUser a \n"
							+ "LEFT OUTER JOIN tblUserHobby b ON a.userId = b.userId \n"
							+ "WHERE a.email LIKE ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, email);
			return stmt.executeQuery();
		}
	}
}
