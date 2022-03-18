package tech.zoomidsoon.pickme_restful_api.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class User extends Entity {
	private Integer userId;
	private String email;
	private String role;
	private Integer cautionTimes;

	@Override
	public boolean isEmpty() {
		return this.userId == null;
	}
}
