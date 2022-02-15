package tech.zoomidsoon.pickme_restful_api.models;

import java.util.ArrayList;
import java.util.List;

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
	private String gender;
	private String bio;
	private String avatar;
	private Integer cautionTimes;
	private List<String> hobbies;

	public User(User other) {
		this.userId = other.userId;
		this.email = other.email;
		this.name = other.name;
		this.gender = other.gender;
		this.avatar = other.avatar;
		this.bio = other.bio;
		this.cautionTimes = other.cautionTimes;
		this.role = other.role;
		this.hobbies = new ArrayList<>(other.hobbies);
	}

	@Override
	public boolean isEmpty() {
		return this.userId == null;
	}
}
