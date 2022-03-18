package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import tech.zoomidsoon.pickme_restful_api.models.File;

public class FileRowMapper extends RowMapper<File> {
	private static final RowMapper<File> singleton = new FileRowMapper();

	private FileRowMapper() {
	}

	public static RowMapper<File> getInstance() {
		return singleton;
	}

	@Override
	public Boolean mapRow(ResultSet rs, File obj) throws SQLException {
		obj.setFileName(rs.getString("fileName"));
		obj.setUserId(rs.getInt("userId"));
		obj.setBucketName(rs.getString("bucketName"));
		obj.setMimeType(rs.getString("mimeType"));
		return null;
	}
}