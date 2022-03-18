package tech.zoomidsoon.pickme_restful_api.models;

import java.util.List;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class UserProfile extends Entity {
	private Integer userId;
	private String name;
	private String gender;
	private String address;
	private String avatar;
	private String bio;
	private List<String> interests;
	private String phone;
	private String statusEmoji;
	private String statusText;
	private Long birthday;
	private List<MatchedUser> matches;
	private int likes;

	@Override
	public boolean isEmpty() {
		return userId == null;
	}
}
