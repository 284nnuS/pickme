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
import tech.zoomidsoon.pickme_restful_api.mappers.HobbyRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.Hobby;
import tech.zoomidsoon.pickme_restful_api.utils.Utils;

public class HobbyRepository implements Repository<Hobby> {
	private static final Repository<Hobby> singleton = new HobbyRepository();

	private HobbyRepository() {
	}

	public static Repository<Hobby> getInstance() {
		return singleton;
	}

	@Override
	public Result<Hobby, JsonAPIResponse.Error> create(Connection conn, Hobby hobby) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO tblHobby (hobbyName, description) VALUES (?,?)",
				Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, hobby.getHobbyName());
			stmt.setString(2, hobby.getDescription());

			if (stmt.executeUpdate() == 0)
				return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));

			return new Result<>(hobby, null);
		}
	}

	@Override
	public List<Hobby> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(HobbyRepository.class)) {
			throw new IllegalArgumentException("Criteria does not belong to the same nest host class");
		}

		try (ResultSet rs = criteria.query(conn)) {
			return HobbyRowMapper.getInstance().processResultSet(rs, Hobby.class);
		}
	}

	@Override
	public Result<Hobby, JsonAPIResponse.Error> update(Connection conn, Hobby hobby) throws Exception {
		if (hobby.isEmpty())
			return null;

		FindByName fin = new FindByName(hobby.getHobbyName());
		List<Hobby> list = HobbyRowMapper.getInstance().processResultSet(fin.query(conn), Hobby.class);

		if (list.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "This hobby does not exist", ""));

		Hobby inDB = list.get(0);

		Utils.copyNonNullFields(inDB, hobby);

		try (PreparedStatement stmt = conn.prepareStatement(
				"UPDATE tblUser SET description = ?")) {
			stmt.setString(2, inDB.getDescription());
			stmt.setString(1, inDB.getHobbyName());

			if (stmt.executeUpdate() > 0)
				return new Result<>(inDB, null);
		}

		return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));
	}

	@Override
	public Result<Hobby, JsonAPIResponse.Error> delete(Connection conn, Hobby hobby) throws Exception {
		try {
			FindByName fin = new FindByName(hobby.getHobbyName());
			List<Hobby> list = HobbyRowMapper.getInstance().processResultSet(fin.query(conn), Hobby.class);

			if (list.isEmpty())
				return new Result<>(null, new JsonAPIResponse.Error(400, "This hobby does not exist", ""));

			hobby = list.get(0);

			try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM tblHobby WHERE name LIKE ?")) {
				stmt.setString(1, hobby.getHobbyName());

				if (stmt.executeUpdate() > 0)
					return new Result<>(hobby, null);
			}
		} catch (Exception e) {
		}

		return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));
	}

	@Override
	public List<Hobby> readAll(Connection conn) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblHobby", ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY)) {
			try (ResultSet rs = stmt.executeQuery()) {
				return HobbyRowMapper.getInstance().processResultSet(rs, Hobby.class);
			}
		}
	}

	@AllArgsConstructor
	public static class FindByName implements Criteria {
		private String name;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblHobby WHERE hobbyName = ?",
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
			String query = String.format("SELECT * FROM tblHobby WHERE hobbyName IN (%s)",
					names.stream().map(el -> "?").collect(Collectors.joining(", ")));

			PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			for (int i = 0; i < names.size(); i++)
				stmt.setString(i + 1, names.get(i));

			return stmt.executeQuery();
		}
	}
}
