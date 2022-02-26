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
	private Long time;
	private Integer sender;
	private Integer receiver;
	private String content;
	private String react;

	@Override
	public boolean isEmpty() {
		return this.messageId == null;
	}
}