package tech.zoomidsoon.pickme_restful_api.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class Interest extends Entity {
	private String interestName;
	private String description;

	@Override
	public boolean isEmpty() {
		return this.interestName == null;
	}
}
