package tech.zoomidsoon.pickme_restful_api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Entity {
	@JsonIgnore
	public abstract boolean isEmpty();
}
