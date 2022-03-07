package tech.zoomidsoon.pickme_restful_api.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class UserBasicInfoMixin {
	@JsonIgnore
	abstract Integer getRole();

	@JsonIgnore
	abstract Integer getCautionTimes();
}