package tech.zoomidsoon.pickme_restful_api.utils;

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

	public static Response error(int code, String message) {
		JsonAPIResponse response = new JsonAPIResponse();
		response.error = new Error(code, message);
		return Response.status(code).entity(response).build();
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Error {
		private int code;
		private String message;
	}
}
