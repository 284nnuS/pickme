package tech.zoomidsoon.pickme_restful_api.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Message extends Entity {
	private Long messageId;
	private Long conversationId;
	private Long time;
	private String content;
	private String react;
	private Integer sender;

	@Override
	public boolean isEmpty() {
		return this.messageId == null;
	}
}