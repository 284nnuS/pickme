package tech.zoomidsoon.pickme_restful_api.helpers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DefaultExceptionHandler implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception arg0) {
		return JsonAPIResponse.handleError(400, arg0.getMessage(), "");
	}
}
