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
	private Character gender;
	private String bio;
	private String avatar;
	private List<Hobby> hobbies = new ArrayList<>();
	@Override
	public boolean isEmpty() {
		return this.userId == null;
	}
}
