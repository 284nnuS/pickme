package tech.zoomidsoon.pickme_restful_api.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class MatchStatus extends Entity {
	private Integer userIdOne;
	private Integer userIdTwo;
	private Boolean like;

	@Override
	public boolean isEmpty() {
		return userIdOne == null || userIdTwo == null;
	}
}
