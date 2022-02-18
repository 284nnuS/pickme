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
import tech.zoomidsoon.pickme_restful_api.mappers.MediaRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.Media;
import tech.zoomidsoon.pickme_restful_api.utils.Utils;

public class MediaRepository implements Repository<Media> {
    private static final Repository<Media> singleton = new MediaRepository();

	private MediaRepository() {
	}

	public static Repository<Media> getInstance() {
		return singleton;
	}
    @Override
	public Result<Media, JsonAPIResponse.Error> create(Connection conn, Media media) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO tblMedia (mediaName,userId,mediaType) VALUES (?,?,?)",
				Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, media.getMediaName());
            stmt.setInt(2,media.getUserId());        
			stmt.setString(3, media.getMediaType());

			if (stmt.executeUpdate() == 0)
				return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));

			return new Result<>(media, null);
		}
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
        //Media doesn't need update
		return null;
	}

    @Override
	public Result<Media, JsonAPIResponse.Error> delete(Connection conn, Media media) throws Exception {
		try {
			FindByName fin = new FindByName(media.getMediaName());
			List<Media> list = MediaRowMapper.getInstance().processResultSet(fin.query(conn), Media.class);

			if (list.isEmpty())
				return new Result<>(null, new JsonAPIResponse.Error(400, "This media does not exist", ""));

            media = list.get(0);

			try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM tblMedia WHERE name LIKE ?")) {
				stmt.setString(1, media.getMediaName());

				if (stmt.executeUpdate() > 0)
					return new Result<>(media, null);
			}
		} catch (Exception e) {
		}

		return new Result<>(null, new JsonAPIResponse.Error(500, "Something went wrong", ""));
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
	public static class FindByName implements Criteria {
		private String name;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tblMedia WHERE mediaName = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, name);
			return stmt.executeQuery();
		}
	}

}
