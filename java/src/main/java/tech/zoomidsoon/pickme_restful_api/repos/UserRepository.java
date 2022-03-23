package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.mappers.UserRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.User;
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
			// Insert user into database
			try (PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO tblUser (email, role) VALUES (?,?)",
					Statement.RETURN_GENERATED_KEYS)) {
				stmt.setString(1, user.getEmail());
				stmt.setString(2, user.getRole());

				if (stmt.executeUpdate() != 1) {
					conn.rollback();
					return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
				}

				// Get id of recently created user
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					rs.next();
					user.setUserId(rs.getInt(1));
					user.setDisabled(false);
				}
			}

			return new Result<>(user, null);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<User> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(UserRepository.class)) {
			throw new IllegalArgumentException("This criteria is not supported");
		}

		try (ResultSet rs = criteria.query(conn)) {
			List<User> result = UserRowMapper.getInstance().processResultSet(rs, User.class);
			return result;
		}
	}

	@Override
	public Result<User, JsonAPIResponse.Error> update(Connection conn, User user) throws Exception {
		// Cannot update if userId is missing
		if (user.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "userId is required", ""));

		try {
			FindById fid = new FindById(user.getUserId());
			List<User> list = UserRowMapper.getInstance().processResultSet(fid.query(conn), User.class);

			if (list.isEmpty())
				return new Result<>(null, new JsonAPIResponse.Error(404, "User does not exist", ""));

			User inDB = list.get(0);

			Utils.copyNonNullFields(inDB, user, "email", "userId");

			User newUser = inDB;

			try (PreparedStatement stmt = conn.prepareStatement(
					"UPDATE tblUser SET role = ?, cautionTimes = ?, disabled = ? WHERE userId = ?")) {
				stmt.setString(1, newUser.getRole());
				stmt.setInt(2, newUser.getCautionTimes());
				stmt.setBoolean(3, newUser.getDisabled());
				stmt.setInt(4, newUser.getUserId());

				if (stmt.executeUpdate() != 1)
					return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
			}

			return new Result<>(newUser, null);
		} catch (Exception e) {
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

			try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM tblUser WHERE userId = ?")) {
				stmt.setInt(1, user.getUserId());

				if (stmt.executeUpdate() != 1) {
					conn.rollback();
					return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
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
				"SELECT * FROM tblUser",
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY)) {
			try (ResultSet rs = stmt.executeQuery()) {
				List<User> result = UserRowMapper.getInstance().processResultSet(rs, User.class);
				return result;
			}
		}
	}

	@AllArgsConstructor
	public static class FindById implements Criteria {
		private int userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblUser WHERE userId = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, userId);
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindByEmail implements Criteria {
		private String email;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblUser WHERE email = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, email);
			return stmt.executeQuery();
		}
	}
}
