package tech.zoomidsoon.pickme_restful_api.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Conversation extends Entity {
	private Long conversationId;
	private Integer otherId;
	private String otherName;
	private String otherAvatar;
	private boolean isSender;
	private Long latestTime;
	private String latestMessage;

	@Override
	public boolean isEmpty() {
		return conversationId == null;
	}
}
