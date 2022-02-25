package tech.zoomidsoon.pickme_restful_api.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import tech.zoomidsoon.pickme_restful_api.models.Media;

public class MediaRowMapper extends RowMapper<Media> {
	private static final RowMapper<Media> singleton = new MediaRowMapper();

	private MediaRowMapper() {
	}

	public static RowMapper<Media> getInstance() {
		return singleton;
	}

	@Override
	public Boolean mapRow(ResultSet rs, Media obj) throws SQLException {
		String mediaName = rs.getString("mediaName");

		obj.setMediaName(mediaName);
		obj.setUserId(rs.getInt("userId"));
		obj.setMediaType(rs.getString("mediaType"));

		return null;
	}
}