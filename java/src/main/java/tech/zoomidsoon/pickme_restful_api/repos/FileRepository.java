package tech.zoomidsoon.pickme_restful_api.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse.Error;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.mappers.FileRowMapper;
import tech.zoomidsoon.pickme_restful_api.models.File;

public class FileRepository implements Repository<File> {
	private static final Repository<File> singleton = new FileRepository();

	private FileRepository() {
	}

	public static Repository<File> getInstance() {
		return singleton;
	}

	@Override
	public Result<File, JsonAPIResponse.Error> create(Connection conn, File file) throws Exception {
		try (PreparedStatement stmt = conn.prepareStatement(
				"INSERT INTO tblFile (fileName, userId, bucketName, mimeType) VALUES (?,?,?,?)")) {
			file.setFileName(UUID.randomUUID().toString());

			stmt.setString(1, file.getFileName());
			stmt.setInt(2, file.getUserId());
			stmt.setString(3, file.getBucketName());
			stmt.setString(4, file.getMimeType());

			if (stmt.executeUpdate() != 1)
				return new Result<>(null, JsonAPIResponse.SERVER_ERROR);

			return new Result<>(file, null);
		}
	}

	@Override
	public List<File> read(Connection conn, Criteria criteria) throws Exception {
		if (!criteria.getClass().getNestHost().isAssignableFrom(FileRepository.class)) {
			throw new IllegalArgumentException("Criteria does not belong to the same nest host class");
		}

		try (ResultSet rs = criteria.query(conn)) {
			return FileRowMapper.getInstance().processResultSet(rs, File.class);
		}
	}

	@Override
	public Result<File, JsonAPIResponse.Error> update(Connection conn, File file) throws Exception {
		throw new UnsupportedOperationException("File Repository does not support updating");
	}

	@Override
	public Result<File, Error> delete(Connection conn, File file) throws Exception {
		try {
			Find find = new Find(file.getUserId(), file.getBucketName(), file.getFileName());
			List<File> list = FileRowMapper.getInstance().processResultSet(find.query(conn), File.class);

			if (list.isEmpty())
				return new Result<>(null, new JsonAPIResponse.Error(400, "This file does not exist", ""));

			file = list.get(0);

			try (PreparedStatement stmt = conn
					.prepareStatement("DELETE FROM tblFile WHERE fileName = ? AND userId = ? AND bucketName = ?")) {
				stmt.setString(1, file.getFileName());
				stmt.setInt(2, file.getUserId());
				stmt.setString(3, file.getBucketName());

				if (stmt.executeUpdate() > 0)
					return new Result<>(file, null);
			}
		} catch (Exception e) {
		}

		return new Result<>(null, JsonAPIResponse.SERVER_ERROR);
	}

	@Override
	public List<File> readAll(Connection conn) throws Exception {
		throw new UnsupportedOperationException("File Repository does not support readAll");
	}

	@AllArgsConstructor
	public static class Find implements Criteria {
		private int userId;
		private String bucketName;
		private String fileName;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblFile WHERE fileName = ? AND userId = ? AND bucketName = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, fileName);
			stmt.setInt(2, userId);
			stmt.setString(3, bucketName);
			return stmt.executeQuery();
		}
	}

	@AllArgsConstructor
	public static class FindByBucketName implements Criteria {
		private int userId;
		private String bucketName;

		@Override
		public ResultSet query(Connection conn) throws Exception {
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM tblFile WHERE userId = ? AND bucketName = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, userId);
			stmt.setString(2, bucketName);
			return stmt.executeQuery();
		}
	}
}
