package tech.zoomidsoon.pickme_restful_api.models;

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
	private List<String> interests;
	private List<Media> medias;

	@Override
	public boolean isEmpty() {
		return this.userId == null;
	}
}
