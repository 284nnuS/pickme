package tech.zoomidsoon.pickme_restful_api.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class MediaMixin {
	@JsonIgnore
	abstract Integer getUserId();

	@JsonIgnore
	abstract String getPayload();
}
