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
	private String name;
	private String email;
	private String role;
	private Character gender;
	private String bio;
	private String avatar;

	@Override
	public boolean isEmpty() {
		return this.userId == null;
	}
}
