package tech.zoomidsoon.pickme_restful_api.helpers;

import java.sql.SQLException;
import java.util.Arrays;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.*;
import tech.zoomidsoon.pickme_restful_api.mixin.MediaMixin;
import tech.zoomidsoon.pickme_restful_api.models.Media;

@Getter
@Setter
@NoArgsConstructor
public class JsonAPIResponse {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Object data;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Error error;

	public static JsonAPIResponse.Error SERVER_ERROR = new JsonAPIResponse.Error(500, "Something went wrong", "");
	private static ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.addMixIn(Media.class, MediaMixin.class);
	}

	public static Response ok(Object data) throws JsonProcessingException {
		JsonAPIResponse response = new JsonAPIResponse();
		response.data = data;
		byte[] output = mapper.writeValueAsBytes(response);
		return Response.ok(output).build();
	}

	public static Response handleError(int code, String message, String details) {
		Error error = new Error(code, message, details);
		return handleError(error);
	}

	public static Response handleError(Error error) {
		JsonAPIResponse response = new JsonAPIResponse();
		response.error = error;
		return Response.status(error.code).entity(response).build();
	}

	/**
	 * Handle SQL Errors that is included in parameter
	 * 
	 * @return return null it's unexpected error
	 */
	public static Response handleSQLError(SQLException e, SQLErrors... errors) {
		SQLErrors err = SQLErrors.fromErrCode(e.getErrorCode());
		if (err == null) {
			System.out.println(e.getErrorCode() + "\n" + e);
			e.printStackTrace();
			return null;
		}

		if (Arrays.asList(errors).contains(err)) {
			JsonAPIResponse response = new JsonAPIResponse();
			response.error = new Error(400, err.message, e.getMessage());
			return Response.status(400).entity(response).build();
		}

		System.out.println(e.getErrorCode() + "\n" + e);
		e.printStackTrace();
		return null;
	}

	public static <T> Response handleResult(Result<T, Error> result) throws JsonProcessingException {
		if (result.isOk())
			return ok(result.getData());

		Error error = result.getError();
		return Response.status(error.getCode()).entity(error).build();
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Error {
		private int code;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String message;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String details;
	}
}
