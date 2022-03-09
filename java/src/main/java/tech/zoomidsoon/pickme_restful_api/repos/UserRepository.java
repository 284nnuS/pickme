package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.mappers.UserRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.Media;
import tech.zoomidsoon.pickme_restful_api.models.User;
import tech.zoomidsoon.pickme_restful_api.repos.InterestRepository.FindByNameList;
import tech.zoomidsoon.pickme_restful_api.utils.ListUtils;
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
			if (user.getInterests() != null && !user.getInterests().isEmpty()) {
				FindByNameList findByNameList = new FindByNameList(user.getInterests());
				if (InterestRepository.getInstance().read(conn, findByNameList).size() != user.getInterests().size()) {
					return new Result<>(null, new JsonAPIResponse.Error(500, "Some interest are not available", ""));
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
					return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
				}

				// Get id of recently created user
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					rs.next();
					user.setUserId(rs.getInt(1));
				}
			}

			user.getMedias().forEach(media -> media.setUserId(user.getUserId()));

			// Create User-Interest relation
			if (user.getInterests().size() > 0) {
				String query = String.format("INSERT INTO tblUserInterest (userId, interestName) VALUES %s",
						user.getInterests().stream().map(el -> "(?,?)").collect(Collectors.joining(", ")));
				try (PreparedStatement stmt = conn.prepareStatement(query)) {
					int index = 1;

					for (String interestName : user.getInterests()) {
						stmt.setInt(index++, user.getUserId());
						stmt.setString(index++, interestName);
					}

					if (stmt.executeUpdate() != user.getInterests().size()) {
						conn.rollback();
						return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
					}
				}
			}

			// Create User-Media relation
			if (user.getMedias().size() > 0) {
				String query = String.format("INSERT INTO tblMedia (mediaName, userId, mediaType) VALUES %s",
						user.getMedias().stream().map(el -> "(?,?,?)").collect(Collectors.joining(", ")));
				try (PreparedStatement stmt = conn.prepareStatement(query)) {
					int index = 1;

					for (Media media : user.getMedias()) {
						stmt.setString(index++, media.getMediaName());
						stmt.setInt(index++, media.getUserId());
						stmt.setString(index++, media.getMediaType());
						try {
							media.write();
						} catch (IllegalArgumentException e) {
							return new Result<>(null, new JsonAPIResponse.Error(400, e.getMessage(), ""));
						}
					}

					if (stmt.executeUpdate() != user.getMedias().size()) {
						conn.rollback();
						return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
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
			if (user.getInterests() != null && !user.getInterests().isEmpty()) {
				FindByNameList findByNameList = new FindByNameList(user.getInterests());
				if (InterestRepository.getInstance().read(conn, findByNameList).size() != user.getInterests().size()) {
					return new Result<>(null, new JsonAPIResponse.Error(500, "Some interest are not available", ""));
				}
			}

			conn.setAutoCommit(false);

			FindById fid = new FindById(user.getUserId());
			List<User> list = UserRowMapper.getInstance().processResultSet(fid.query(conn), User.class);

			if (list.isEmpty())
				return new Result<>(null, new JsonAPIResponse.Error(404, "User does not exist", ""));

			user.getMedias().forEach(media -> media.setUserId(user.getUserId()));

			User inDB = list.get(0);

			List<String> addedInterests = new ArrayList<>();
			List<String> removedInterests = new ArrayList<>();
			List<String> mergedInterests = new ArrayList<>();
			List<Media> addedMedias = new ArrayList<>();
			List<Media> removedMedias = new ArrayList<>();
			List<Media> mergedMedias = new ArrayList<>();

			ListUtils.diffList(inDB.getInterests(), user.getInterests(), addedInterests, removedInterests,
					mergedInterests);
			ListUtils.diffList(inDB.getMedias(), user.getMedias(), addedMedias, removedMedias, mergedMedias);

			Utils.copyNonNullFields(inDB, user, "email", "userId", "medias", "interest");

			User newUser = inDB;
			newUser.setInterests(mergedInterests);
			newUser.setMedias(mergedMedias);

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
					return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
				}
			}

			// Remove User-Interest relation
			if (removedInterests.size() > 0) {
				String query = String.format("DELETE FROM tblUserInterest WHERE (userId, interestName) IN (%s)",
						removedInterests.stream().map(el -> "(?,?)").collect(Collectors.joining(", ")));
				try (PreparedStatement stmt = conn.prepareStatement(query)) {
					int index = 1;

					for (String interestName : removedInterests) {
						stmt.setInt(index++, newUser.getUserId());
						stmt.setString(index++, interestName);
					}

					if (stmt.executeUpdate() != removedInterests.size()) {
						conn.rollback();
						return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
					}
				}
			}

			// Add User-Interest relation
			if (addedInterests.size() > 0) {
				String query = String.format("INSERT INTO tblUserInterest (userId, interestName) VALUES %s",
						addedInterests.stream().map(el -> "(?,?)").collect(Collectors.joining(", ")));
				try (PreparedStatement stmt = conn.prepareStatement(query)) {
					int index = 1;

					for (String interestName : addedInterests) {
						stmt.setInt(index++, newUser.getUserId());
						stmt.setString(index++, interestName);
					}

					if (stmt.executeUpdate() != addedInterests.size()) {
						conn.rollback();
						return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
					}
				}
			}

			// Remove User-Interest relation
			if (removedMedias.size() > 0) {
				String query = String.format("DELETE FROM tblMedia WHERE (mediaName) IN (%s)",
						removedMedias.stream().map(el -> "(?)").collect(Collectors.joining(", ")));

				try (PreparedStatement stmt = conn.prepareStatement(query)) {
					int index = 1;

					for (Media media : removedMedias) {
						stmt.setString(index++, media.getMediaName());
						media.delete();
					}

					if (stmt.executeUpdate() != removedMedias.size()) {
						conn.rollback();
						return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
					}
				}
			}

			// Add User-Media relation
			if (addedMedias.size() > 0) {
				String query = String.format("INSERT INTO tblMedia (mediaName, userId, mediaType) VALUES %s",
						addedMedias.stream().map(el -> "(?,?,?)").collect(Collectors.joining(", ")));
				try (PreparedStatement stmt = conn.prepareStatement(query)) {
					int index = 1;

					for (Media media : addedMedias) {
						stmt.setString(index++, media.getMediaName());
						stmt.setInt(index++, media.getUserId());
						stmt.setString(index++, media.getMediaType());
						try {
							media.write();
						} catch (IllegalArgumentException e) {
							return new Result<>(null, new JsonAPIResponse.Error(400, e.getMessage(), ""));
						}
					}

					if (stmt.executeUpdate() != addedMedias.size()) {
						conn.rollback();
						return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
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
				"SELECT a.userId, a.name, a.email, a.role, a.gender, a.bio, a.avatar, a.cautionTimes, b.interestName, c.mediaName, c.MediaType \n"
						+ "FROM tblUser a \n"
						+ "LEFT OUTER JOIN tblUserInterest b ON a.userId = b.userId \n"
						+ "LEFT OUTER JOIN tblMedia c ON a.userId = c.userId ",
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
					"SELECT a.userId, a.name, a.email, a.role, a.gender, a.bio, a.avatar, a.cautionTimes, b.interestName, c.mediaName, c.MediaType \n"
							+ "FROM tblUser a \n"
							+ "LEFT OUTER JOIN tblUserInterest b ON a.userId = b.userId \n"
							+ "LEFT OUTER JOIN tblMedia c ON a.userId = c.userId \n"
							+ "WHERE a.userId = ?",
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
					"SELECT a.userId, a.name, a.email, a.role, a.gender, a.bio, a.avatar, a.cautionTimes, b.interestName, c.mediaName, c.MediaType \n"
							+ "FROM tblUser a \n"
							+ "LEFT OUTER JOIN tblUserInterest b ON a.userId = b.userId \n"
							+ "LEFT OUTER JOIN tblMedia c ON a.userId = c.userId \n"
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
					"SELECT a.userId, a.name, a.email, a.role, a.gender, a.bio, a.avatar, a.cautionTimes, b.interestName, c.mediaName, c.MediaType \n"
							+ "FROM tblUser a \n"
							+ "LEFT OUTER JOIN tblUserInterest b ON a.userId = b.userId \n"
							+ "LEFT OUTER JOIN tblMedia c ON a.userId = c.userId \n"
							+ "WHERE a.email = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, email);
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindByUserIdWithMatchstatus implements Criteria {
		private Integer userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT a.userId, a.name, a.email, a.role, a.gender, a.bio, a.avatar, a.cautionTimes, b.interestName, c.mediaName, c.MediaType \n"
							+ "FROM tblUser a \n"
							+ "LEFT OUTER JOIN tblUserInterest b ON a.userId = b.userId \n"
							+ "LEFT OUTER JOIN tblMedia c ON a.userId = c.userId \n"
							+ "WHERE a.userId NOT IN (SELECT userIdTwo FROM tblMatchStatus WHERE userIdOne = ?) and a.userid not like ? ORDER BY RAND() LIMIT 10",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, userId);
			stmt.setInt(2, userId);
			return stmt.executeQuery();
		}
	}
}
