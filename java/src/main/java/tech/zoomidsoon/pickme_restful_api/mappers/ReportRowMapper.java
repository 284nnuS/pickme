package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import tech.zoomidsoon.pickme_restful_api.models.Report;

public class ReportRowMapper extends RowMapper<Report> {
	private static final RowMapper<Report> singleton = new ReportRowMapper();

	private ReportRowMapper() {
	}

	public static RowMapper<Report> getInstance() {
		return singleton;
	}

	@Override
	public Boolean mapRow(ResultSet rs, Report obj) throws SQLException {
		Long reportId = rs.getLong("reportId");
		Timestamp time = rs.getTimestamp("time");
		obj.setReportId(reportId);
		obj.setTime(time.getTime());
		obj.setReporter(rs.getInt("reporter"));
		obj.setReported(rs.getInt("reported"));
		obj.setMessage(rs.getString("message"));
		obj.setDone(rs.getBoolean("done"));
		return true;
	}

}
