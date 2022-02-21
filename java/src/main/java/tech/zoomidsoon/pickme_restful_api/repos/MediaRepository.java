package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse.Error;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.mappers.MediaRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.Media;

public class MediaRepository implements Repository<Media> {
	private static final Repository<Media> singleton = new MediaRepository();

	private MediaRepository() {
	}

	public static Repository<Media> getInstance() {
		return singleton;
	}

	@Override
	public Result<Media, JsonAPIResponse.Error> create(Connection conn, Media media) throws Exception {
		throw new UnsupportedOperationException("Media Repository does not support creating");
	}

	@Override
	public List<Media> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(MediaRepository.class)) {
			throw new IllegalArgumentException("Criteria does not belong to the same nest host class");
		}

		try (ResultSet rs = criteria.query(conn)) {
			return MediaRowMapper.getInstance().processResultSet(rs, Media.class);
		}
	}

	@Override
	public Result<Media, JsonAPIResponse.Error> update(Connection conn, Media media) throws Exception {
		throw new UnsupportedOperationException("Media Repository does not support updating");
	}

	@Override
	public Result<Media, Error> delete(Connection conn, Media entity) throws Exception {
		throw new UnsupportedOperationException("Media Repository does not support deleting");
	}

	@Override
	public List<Media> readAll(Connection conn) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblMedia", ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY)) {
			try (ResultSet rs = stmt.executeQuery()) {
				return MediaRowMapper.getInstance().processResultSet(rs, Media.class);
			}
		}
	}

	@AllArgsConstructor
	public static class FindByMediaNameAndUserId implements Criteria {
		private String mediaName;
		private int userId;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblMedia WHERE mediaName = ? and userId = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, mediaName);
			stmt.setInt(2, userId);
			return stmt.executeQuery();
		}
	}
}
