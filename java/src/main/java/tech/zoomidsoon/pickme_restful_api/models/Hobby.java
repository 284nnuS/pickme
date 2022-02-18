package tech.zoomidsoon.pickme_restful_api.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class Hobby extends Entity {
	private String hobbyName;
	private String description;

	@Override
	public boolean isEmpty() {
		return this.hobbyName == null;
	}
}
