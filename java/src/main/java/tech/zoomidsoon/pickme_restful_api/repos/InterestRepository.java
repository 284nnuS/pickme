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
import tech.zoomidsoon.pickme_restful_api.mappers.InterestRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.Interest;
import tech.zoomidsoon.pickme_restful_api.utils.Utils;

public class InterestRepository implements Repository<Interest> {
	private static final Repository<Interest> singleton = new InterestRepository();

	private InterestRepository() {
	}

	public static Repository<Interest> getInstance() {
		return singleton;
	}

	@Override
	public Result<Interest, JsonAPIResponse.Error> create(Connection conn, Interest interest) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO tblInterest (interestName, description) VALUES (?,?)",
				Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, interest.getInterestName());
			stmt.setString(2, interest.getDescription());

			if (stmt.executeUpdate() == 0)
				return new Result<>(null, JsonAPIResponse.SERVER_ERROR);

			return new Result<>(interest, null);
		}
	}

	@Override
	public List<Interest> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(InterestRepository.class)) {
			throw new IllegalArgumentException("Criteria does not belong to the same nest host class");
		}

		try (ResultSet rs = criteria.query(conn)) {
			return InterestRowMapper.getInstance().processResultSet(rs, Interest.class);
		}
	}

	@Override
	public Result<Interest, JsonAPIResponse.Error> update(Connection conn, Interest interest) throws Exception {
		// Cannot update if interestName is missing
		if (interest.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "interestName is required", ""));

		FindByName fin = new FindByName(interest.getInterestName());
		List<Interest> list = InterestRowMapper.getInstance().processResultSet(fin.query(conn), Interest.class);

		if (list.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "This interest does not exist", ""));

		Interest inDB = list.get(0);

		Utils.copyNonNullFields(inDB, interest);

		try (PreparedStatement stmt = conn.prepareStatement(
				"UPDATE tblInterest SET description = ? WHERE interestName = ?")) {
			stmt.setString(1, inDB.getDescription());
			stmt.setString(2, inDB.getInterestName());

			if (stmt.executeUpdate() > 0)
				return new Result<>(inDB, null);
		}

		return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
	}

	@Override
	public Result<Interest, JsonAPIResponse.Error> delete(Connection conn, Interest interest) throws Exception {
		try {
			FindByName fin = new FindByName(interest.getInterestName());
			List<Interest> list = InterestRowMapper.getInstance().processResultSet(fin.query(conn), Interest.class);

			if (list.isEmpty())
				return new Result<>(null, new JsonAPIResponse.Error(400, "This interest does not exist", ""));

			interest = list.get(0);

			try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM tblInterest WHERE interestName = ?")) {
				stmt.setString(1, interest.getInterestName());

				if (stmt.executeUpdate() > 0)
					return new Result<>(interest, null);
			}
		} catch (Exception e) {
		}

		return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
	}

	@Override
	public List<Interest> readAll(Connection conn) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblInterest",
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY)) {
			try (ResultSet rs = stmt.executeQuery()) {
				return InterestRowMapper.getInstance().processResultSet(rs, Interest.class);
			}
		}
	}

	@AllArgsConstructor
	public static class FindByName implements Criteria {
		private String name;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblInterest WHERE interestName = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, name);
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindByNameList implements Criteria {
		private List<String> names;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			String query = String.format("SELECT * FROM tblInterest WHERE interestName IN (%s)",
					names.stream().map(el -> "?").collect(Collectors.joining(", ")));

			PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			for (int i = 0; i < names.size(); i++)
				stmt.setString(i + 1, names.get(i));

			return stmt.executeQuery();
		}
	}
}
