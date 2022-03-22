package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse.Error;
import tech.zoomidsoon.pickme_restful_api.mappers.ReportRowMapper;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.models.Report;
import tech.zoomidsoon.pickme_restful_api.utils.Utils;
import lombok.*;

public class ReportRepository implements Repository<Report> {
	private static final Repository<Report> singleton = new ReportRepository();

	private ReportRepository() {
	}

	public static Repository<Report> getInstance() {
		return singleton;
	}

	@Override
	public Result<Report, Error> create(Connection conn, Report report) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO tblReport (reporter, reported, time, message, done) VALUES (?,?,?,?,?,?)",
				Statement.RETURN_GENERATED_KEYS)) {
			Timestamp now = Timestamp.from(Instant.now());
			report.setTime(now.getTime());
			stmt.setInt(1, report.getReporter());
			stmt.setInt(2, report.getReported());
			stmt.setTimestamp(3, now);
			stmt.setString(4, report.getMessage());
			stmt.setBoolean(5, report.getDone());

			if (stmt.executeUpdate() != 1)
				return new Result<>(null, JsonAPIResponse.SERVER_ERROR);

			// Get id of recently created report
			try (ResultSet rs = stmt.getGeneratedKeys()) {
				rs.next();
				report.setReportId(rs.getLong(1));
			}

			return new Result<>(report, null);
		}
	}

	@Override
	public List<Report> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(ReportRepository.class)) {
			throw new IllegalArgumentException("Criteria does not belong to the same nest host class");
		}

		try (ResultSet rs = criteria.query(conn)) {
			return ReportRowMapper.getInstance().processResultSet(rs, Report.class);
		}
	}

	@Override
	public Result<Report, Error> update(Connection conn, Report report) throws Exception {
		// Cannot update if reportId is missing
		if (report.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "reportId is required", ""));

		FindById fid = new FindById(report.getReportId());
		List<Report> list = this.read(conn, fid);

		if (list.isEmpty())
			return new Result<>(null, new JsonAPIResponse.Error(400, "Report does not exist", ""));
		Report inDB = list.get(0);
		if (report.getMessage() != null && !report.getMessage().equals(inDB.getMessage()))
			return new Result<>(null, new JsonAPIResponse.Error(400, "Not allow to change message of the report", ""));

		Utils.copyNonNullFields(inDB, report, "reportId", "reporter", " reported", "time");

		try (PreparedStatement stmt = conn.prepareStatement(
				"UPDATE tblReport SET done=? WHERE reportId = ?")) {
			stmt.setBoolean(1, report.getDone());
			stmt.setLong(2, report.getReportId());

			if (stmt.executeUpdate() > 0)
				return new Result<>(report, null);

			return new Result<>(null, new JsonAPIResponse.Error(400, "Report Id not found", ""));
		}
	}

	@Override
	public Result<Report, Error> delete(Connection conn, Report entity) throws Exception {

		return new Result<>(null, new JsonAPIResponse.Error(400, "Cannot delete report", ""));
	}

	@Override
	public List<Report> readAll(Connection conn) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblReport",
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY)) {
			try (ResultSet rs = stmt.executeQuery()) {
				return ReportRowMapper.getInstance().processResultSet(rs, Report.class);
			}

		}
	}

	@AllArgsConstructor
	public static class FindById implements Criteria {
		private Long reportId;

		@Override
		public ResultSet query(Connection conn) throws Exception {

			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblReport WHERE reportId = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setLong(1, reportId);
			return stmt.executeQuery();

		}
	}

	@AllArgsConstructor
	@Getter
	@Setter
	public static class FindByTimeAndReportedId implements Criteria {
		private long time;
		private int reported;
		private int num;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblReport WHERE ((reported = ?) AND time < ? ORDER BY time DESC LIMIT"
							+ num,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			Timestamp timestamp = new Timestamp(time);
			stmt.setInt(1, reported);
			stmt.setTimestamp(2, timestamp);
			return stmt.executeQuery();

		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	public static class FindByDone implements Criteria {
		private boolean done;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblReport WHERE done = ?  ORDER BY time DESC ",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setBoolean(1, done);
			return stmt.executeQuery();

		}
	}

	@NoArgsConstructor
	@Getter
	@Setter
	public static class FindByDoneAndTime implements Criteria {
		private boolean done;
		private long time;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblReport WHERE done = ? AND time < ?  ORDER BY time DESC ",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setBoolean(1, done);
			Timestamp timestamp = new Timestamp(time);
			stmt.setTimestamp(2, timestamp);
			return stmt.executeQuery();

		}
	}
}
