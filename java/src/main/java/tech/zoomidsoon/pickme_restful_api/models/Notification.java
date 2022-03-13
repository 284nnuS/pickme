package tech.zoomidsoon.pickme_restful_api.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString

public class Notification extends Entity {
	private Integer notificationId;
	private Long time;
	private Integer sourceUID;
	private Integer targetUID;
	private String avatar;
	private String eventType;
	private Boolean seen;
	private String message;
	private String link;

	@Override
	public boolean isEmpty() {
		return this.notificationId == null;
	}

}