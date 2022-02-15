package tech.zoomidsoon.pickme_restful_api.helpers;

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

	public static Response handleError(int code, String message, String details) {
		JsonAPIResponse response = new JsonAPIResponse();
		response.error = new Error(code, message, details);
		return Response.status(code).entity(response).build();
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

	public static <T> Response handleResult(Result<T, Error> result) {
		if (result.isOk())
			return Response.ok(result.getData()).build();

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
