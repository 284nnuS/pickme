package tech.zoomidsoon.pickme_restful_api.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class MessageItem extends Entity {
	private Integer userId;
	private Long messageId;
	private Long time;
	private String name;
	private String avatar;
	private Boolean isSender;
	private String content;

	@Override
	public boolean isEmpty() {
		return userId == null;
	}
}
