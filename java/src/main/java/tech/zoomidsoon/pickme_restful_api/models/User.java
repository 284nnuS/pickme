package tech.zoomidsoon.pickme_restful_api.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class User extends Entity {
	private int userId = -1;
	private String name;
	private String email;
	private String role;
	private char gender;
	private String bio;
	private String avatar;

	@Override
	public boolean isEmpty() {
		return this.userId == -1;
	}
}
