package tech.zoomidsoon.pickme_restful_api.models;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Media extends Entity {
	private String mediaName;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer userId;
	private String mediaType;

	@Override
	public boolean isEmpty() {
		return this.mediaName == null;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	public static class Payload {
		private String mediaType;
		private String payload;
	}
}
