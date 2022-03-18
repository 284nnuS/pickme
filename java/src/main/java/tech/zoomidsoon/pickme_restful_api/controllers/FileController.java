package tech.zoomidsoon.pickme_restful_api.controllers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import lombok.*;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.helpers.Result;
import tech.zoomidsoon.pickme_restful_api.helpers.SQLErrors;
import tech.zoomidsoon.pickme_restful_api.models.File;
import tech.zoomidsoon.pickme_restful_api.repos.FileRepository;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@SuppressWarnings({ "unchecked" })
@Path("/file")
public class FileController {
	@POST
	@Path("/{userId}/{bucketName}/{mimeType}")
	public Response uploadFile(@PathParam("userId") int userId, @PathParam("bucketName") String bucketName,
			@PathParam("mimeType") String mimeType, Payload payload) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				File file = new File();
				file.setUserId(userId);
				file.setBucketName(bucketName);
				file.setMimeType(mimeType);

				Result<File, JsonAPIResponse.Error> result = FileRepository.getInstance().create(conn, file);

				if (result.isOk())
					file.write(payload.getPayload());

				return JsonAPIResponse.handleResult(result);
			} catch (SQLException e) {
				Response response = JsonAPIResponse.handleSQLError(e, SQLErrors.DATA_TRUNCATED,
						SQLErrors.INCORRECT_DATA_TYPE);
				if (response != null)
					return response;
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
	}

	@GET
	@Path("/{userId}/{bucketName}")
	public Response findByBucketName(@PathParam("userId") int userId, @PathParam("bucketName") String bucketName) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				FileRepository.FindByBucketName find = new FileRepository.FindByBucketName(
						userId, bucketName);
				List<File> files = FileRepository.getInstance().read(conn, find);

				return JsonAPIResponse.ok(files);
			} catch (SQLException e) {
				Response response = JsonAPIResponse.handleSQLError(e, SQLErrors.DATA_TRUNCATED,
						SQLErrors.INCORRECT_DATA_TYPE);
				if (response != null)
					return response;
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
	}

	@GET
	@Path("/{userId}/{bucketName}/{fileName}")
	public Response downloadFile(@PathParam("userId") int userId, @PathParam("bucketName") String bucketName,
			@PathParam("fileName") String fileName) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				FileRepository.Find find = new FileRepository.Find(
						userId, bucketName, fileName);
				List<File> files = FileRepository.getInstance().read(conn, find);

				if (files.isEmpty())
					return JsonAPIResponse.handleError(404, "File is not found", "");

				File file = files.get(0);
				FileInputStream stream = file.read();

				StreamingOutput output = new StreamingOutput() {
					@Override
					public void write(OutputStream arg0) throws IOException, WebApplicationException {
						byte[] buffer = new byte[1024 * 8];
						int size;

						while ((size = stream.read(buffer)) > 0) {
							arg0.write(buffer, 0, size);
							arg0.flush();
						}
						stream.close();
					}
				};

				return Response.ok(output, file.getMimeType()).build();
			} catch (SQLException e) {
				Response response = JsonAPIResponse.handleSQLError(e, SQLErrors.DATA_TRUNCATED,
						SQLErrors.INCORRECT_DATA_TYPE);
				if (response != null)
					return response;
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
	}

	@DELETE
	@Path("/{userId}/{bucketName}/{fileName}")
	public Response deleteFile(@PathParam("userId") int userId, @PathParam("bucketName") String bucketName,
			@PathParam("fileName") String fileName) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				File file = new File();
				file.setFileName(fileName);
				file.setUserId(userId);
				file.setBucketName(bucketName);

				Result<File, JsonAPIResponse.Error> result = FileRepository.getInstance().delete(conn, file);

				if (result.isOk())
					result.getData().delete();

				return JsonAPIResponse.handleResult(result);
			} catch (SQLException e) {
				Response response = JsonAPIResponse.handleSQLError(e, SQLErrors.DATA_TRUNCATED,
						SQLErrors.INCORRECT_DATA_TYPE);
				if (response != null)
					return response;
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
	}

	@NoArgsConstructor
	@Getter
	@Setter
	static class Payload {
		private String payload;
	}
}