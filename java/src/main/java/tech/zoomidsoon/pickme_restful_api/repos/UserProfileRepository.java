package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse.Error;
import tech.zoomidsoon.pickme_restful_api.mappers.UserProfileRowMapper;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.models.UserProfile;
import tech.zoomidsoon.pickme_restful_api.repos.InterestRepository.FindByNameList;
import tech.zoomidsoon.pickme_restful_api.utils.ListUtils;
import tech.zoomidsoon.pickme_restful_api.utils.Utils;

public class UserProfileRepository implements Repository<UserProfile> {

	private static final Repository<UserProfile> singleton = new UserProfileRepository();

	public UserProfileRepository() {
	}

	public static Repository<UserProfile> getInstance() {
		return singleton;
	}

	@Override
	public Result<UserProfile, Error> create(Connection conn, UserProfile profile) throws Exception {
		// Cannot create if userId is missing
		if (profile.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "userId is required", ""));

		try {
			if (profile.getInterests() != null && !profile.getInterests().isEmpty()) {
				FindByNameList findByNameList = new FindByNameList(profile.getInterests());
				if (InterestRepository.getInstance().read(conn, findByNameList).size() != profile.getInterests().size()) {
					return new Result<>(null, new JsonAPIResponse.Error(500, "Some interest are not available", ""));
				}
			}

			conn.setAutoCommit(false);

			// Insert user into database
			try (PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO tblUserProfile (userId, name, gender, avatar, bio, birthday, address, statusEmoji, statusText, phone) VALUES (?,?,?,?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS)) {
				stmt.setInt(1, profile.getUserId());
				stmt.setString(2, profile.getName());
				stmt.setString(3, profile.getGender());
				stmt.setString(4, profile.getAvatar());
				stmt.setString(5, profile.getBio());
				stmt.setDate(6, new Date(profile.getBirthday()));
				stmt.setString(7, profile.getAddress());
				stmt.setString(8, profile.getStatusEmoji());
				stmt.setString(9, profile.getStatusText());
				stmt.setString(10, profile.getPhone());

				if (stmt.executeUpdate() != 1) {
					conn.rollback();
					return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
				}
			}

			// Create UserProfile-Interest relation
			if (profile.getInterests().size() > 0) {
				String query = String.format("INSERT INTO tblUserInterest (userId, interestName) VALUES %s",
						profile.getInterests().stream().map(el -> "(?,?)").collect(Collectors.joining(", ")));
				try (PreparedStatement stmt = conn.prepareStatement(query)) {
					int index = 1;

					for (String interestName : profile.getInterests()) {
						stmt.setInt(index++, profile.getUserId());
						stmt.setString(index++, interestName);
					}

					if (stmt.executeUpdate() != profile.getInterests().size()) {
						conn.rollback();
						return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
					}
				}
			}

			conn.commit();

			return new Result<>(profile, null);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<UserProfile> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(UserProfileRepository.class)) {
			throw new IllegalArgumentException("This criteria is not supported");
		}

		try (ResultSet rs = criteria.query(conn)) {
			List<UserProfile> result = UserProfileRowMapper.getInstance().processResultSet(rs, UserProfile.class);
			return result;
		}
	}

	@Override
	public Result<UserProfile, Error> update(Connection conn, UserProfile profile) throws Exception {
		// Cannot update if userId is missing
		if (profile.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "userId is required", ""));

		try {
			if (profile.getInterests() != null && !profile.getInterests().isEmpty()) {
				FindByNameList findByNameList = new FindByNameList(profile.getInterests());
				if (InterestRepository.getInstance().read(conn, findByNameList).size() != profile.getInterests().size()) {
					return new Result<>(null, new JsonAPIResponse.Error(500, "Some interest are not available", ""));
				}
			}

			conn.setAutoCommit(false);

			FindById fid = new FindById(profile.getUserId());
			List<UserProfile> list = UserProfileRowMapper.getInstance().processResultSet(fid.query(conn),
					UserProfile.class);

			if (list.isEmpty())
				return new Result<>(null, new JsonAPIResponse.Error(404, "User does not exist", ""));

			UserProfile inDB = list.get(0);

			List<String> addedInterests = new ArrayList<>();
			List<String> removedInterests = new ArrayList<>();
			List<String> mergedInterests = new ArrayList<>();

			if (profile.getInterests() != null)
				ListUtils.diffList(inDB.getInterests(), profile.getInterests(), addedInterests, removedInterests,
						mergedInterests);

			Utils.copyNonNullFields(inDB, profile, "userId", "medias", "interest");

			UserProfile newProfile = inDB;
			newProfile.setInterests(mergedInterests);

			// Update userProfile in database
			try (PreparedStatement stmt = conn.prepareStatement(
					"UPDATE tblUserProfile SET name = ?, gender = ?, avatar = ?, bio = ?, birthday = ?, address = ?, statusEmoji = ?, statusText = ?, phone = ? WHERE userId = ?")) {
				stmt.setInt(10, newProfile.getUserId());
				stmt.setString(1, newProfile.getName());
				stmt.setString(2, newProfile.getGender());
				stmt.setString(3, newProfile.getAvatar());
				stmt.setString(4, newProfile.getBio());
				stmt.setDate(5, new Date(newProfile.getBirthday()));
				stmt.setString(6, newProfile.getAddress());
				stmt.setString(7, newProfile.getStatusEmoji());
				stmt.setString(8, newProfile.getStatusText());
				stmt.setString(9, newProfile.getPhone());

				if (stmt.executeUpdate() != 1) {
					conn.rollback();
					return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
				}
			}

			// Remove UserProfile-Interest relation
			if (removedInterests.size() > 0) {
				String query = String.format("DELETE FROM tblUserInterest WHERE (userId, interestName) IN (%s)",
						removedInterests.stream().map(el -> "(?,?)").collect(Collectors.joining(", ")));
				try (PreparedStatement stmt = conn.prepareStatement(query)) {
					int index = 1;

					for (String interestName : removedInterests) {
						stmt.setInt(index++, newProfile.getUserId());
						stmt.setString(index++, interestName);
					}

					if (stmt.executeUpdate() != removedInterests.size()) {
						conn.rollback();
						return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
					}
				}
			}

			// Add UserProfile-Interest relation
			if (addedInterests.size() > 0) {
				String query = String.format("INSERT INTO tblUserInterest (userId, interestName) VALUES %s",
						addedInterests.stream().map(el -> "(?,?)").collect(Collectors.joining(", ")));
				try (PreparedStatement stmt = conn.prepareStatement(query)) {
					int index = 1;

					for (String interestName : addedInterests) {
						stmt.setInt(index++, newProfile.getUserId());
						stmt.setString(index++, interestName);
					}

					if (stmt.executeUpdate() != addedInterests.size()) {
						conn.rollback();
						return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
					}
				}
			}

			conn.commit();

			return new Result<>(newProfile, null);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public Result<UserProfile, Error> delete(Connection conn, UserProfile entity) throws Exception {
		throw new UnsupportedOperationException("MatchStatus Repository does not support deleting");
	}

	@Override
	public List<UserProfile> readAll(Connection conn) throws Exception {
		throw new UnsupportedOperationException("MatchStatus Repository does not support readAll");
	}

	@AllArgsConstructor
	public static class FindById implements Criteria {
		private int userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT tup.userId, tup.name, tup.gender, tup.avatar, tup.bio, tup.birthday, tup.address, \n"
							+ "tup.statusEmoji, tup.statusText, tup.phone, tui.interestName, \n"
							+ "re.matchedId, tup2.name as matchedName, tup2.avatar as matchedAvatar, \n"
							+ "COUNT(DISTINCT tupr.reactUID) as likes \n"
							+ "FROM tblUserProfile tup \n"
							+ "LEFT OUTER JOIN tblUserInterest tui ON tup.userId = tui.userId \n"
							+ "LEFT OUTER JOIN tblUserProfileReact tupr ON tup.userId = tupr.userId \n"
							+ "LEFT OUTER JOIN (SELECT tms1.userIdOne as userId, tms1.userIdTwo as matchedId FROM tblMatchStatus tms1 \n"
							+ "INNER JOIN tblMatchStatus tms2 \n"
							+ "ON tms1.userIdOne = tms2.userIdTwo AND tms1.userIdTwo = tms2.userIdOne AND tms1.`like` = 1 AND tms2.`like` = 1) re \n"
							+ "ON tup.userId = re.userId \n"
							+ "LEFT OUTER JOIN tblUserProfile tup2 \n"
							+ "ON re.matchedId = tup2.userId \n"
							+ "WHERE tup.userId = ? \n"
							+ "GROUP BY tup.userId, tup.name, tup.gender, tup.avatar, tup.bio, tup.birthday, tup.address, \n"
							+ "tup.statusEmoji, tup.statusText, tup.phone, tui.interestName, \n"
							+ "re.matchedId, tup2.name, tup2.avatar",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			stmt.setInt(1, userId);
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindMatchedUsersById implements Criteria {
		private int userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT tup.userId, tup.name, tup.gender, tup.avatar, tup.bio, tup.birthday, tup.address, \n"
							+ "tup.statusEmoji, tup.statusText, tup.phone, tui.interestName \n"
							+ "FROM tblUserProfile tup \n"
							+ "LEFT OUTER JOIN tblUserInterest tui ON tup.userId = tui.userId \n"
							+ "INNER JOIN (SELECT tms1.userIdOne as userId, tms1.userIdTwo as matchedId FROM tblMatchStatus tms1 \n"
							+ "INNER JOIN tblMatchStatus tms2 \n"
							+ "ON tms1.userIdOne = tms2.userIdTwo AND tms1.userIdTwo = tms2.userIdOne AND tms1.`like` = 1 AND tms2.`like` = 1) re \n"
							+ "ON tup.userId = re.matchedId \n"
							+ "WHERE re.userId = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, userId);
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindUnMatchedUsersById implements Criteria {
		private int userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT tup.userId, tup.name, tup.gender, tup.avatar, tup.bio, tup.birthday, tup.address, \n"
							+ "tup.statusEmoji, tup.statusText, tup.phone, tui.interestName \n"
							+ "FROM tblUserProfile tup \n"
							+ "LEFT OUTER JOIN tblUserInterest tui ON tup.userId = tui.userId \n"
							+ "WHERE tup.userId NOT IN (SELECT userIdTwo FROM tblMatchStatus WHERE userIdOne = ?) AND tup.userId != ? ORDER BY RAND() LIMIT 10",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, userId);
			stmt.setInt(2, userId);
			return stmt.executeQuery();
		}
	}
}
