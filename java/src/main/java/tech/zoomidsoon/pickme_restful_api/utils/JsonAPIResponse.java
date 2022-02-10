package tech.zoomidsoon.pickme_restful_api.utils;

import java.sql.SQLException;
import java.util.Arrays;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class JsonAPIResponse {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Object data;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Error error;

	public static Response ok(Object data) {
		JsonAPIResponse response = new JsonAPIResponse();
		response.data = data;
		return Response.ok(response).build();
	}

	public static Response error(int code, String message, String details) {
		JsonAPIResponse response = new JsonAPIResponse();
		response.error = new Error(code, message, details);
		return Response.status(code).entity(response).build();
	}

	public static Response sqlErrors(SQLException e, SQLErrors... handlers) {
		SQLErrors err = SQLErrors.fromErrCode(e.getErrorCode());
		if (err == null) {
			System.out.println(e);
			return null;
		}

		if (Arrays.asList(handlers).contains(err)) {
			JsonAPIResponse response = new JsonAPIResponse();
			response.error = new Error(400, err.message, e.getMessage());
			return Response.status(400).entity(response).build();
		}

		System.out.println(e);
		return null;
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
