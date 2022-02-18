package tech.zoomidsoon.pickme_restful_api.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tech.zoomidsoon.pickme_restful_api.helpers.JsonAPIResponse;
import tech.zoomidsoon.pickme_restful_api.models.Media;
import tech.zoomidsoon.pickme_restful_api.repos.MediaRepository;
import tech.zoomidsoon.pickme_restful_api.utils.DBContext;

@Path("/media")
public class MediaController {
	@GET
	@Path("/{userId}/{mediaName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("userId") int userId, @PathParam("mediaName") String mediaName) {
		try {
			try (Connection conn = DBContext.getConnection()) {
				MediaRepository.FindByMediaNameAndUserId findByMediaNameAndUserId = new MediaRepository.FindByMediaNameAndUserId(
						mediaName, userId);
				List<Media> medias = MediaRepository.getInstance().read(conn, findByMediaNameAndUserId);

				if (medias.isEmpty())
					return JsonAPIResponse.handleError(404, "Media does not exist", "");

				Media media = medias.get(0);
				Media.Payload payload = new Media.Payload(media.getMediaType(), "<FILE CONTENT IN BASE64>");

				return JsonAPIResponse.ok(payload);
			} catch (SQLException e) {
				Response response = JsonAPIResponse.handleSQLError(e);
				if (response != null)
					return response;
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

		return JsonAPIResponse.handleError(JsonAPIResponse.SERVER_ERROR);
	}
}
