package tech.zoomidsoon.pickme_restful_api.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString

public class Notification extends Entity{
    private Integer notificationId;
    private Integer sourceUID;
    private Integer targetUID;
    private String eventType;
    private Integer seen;
    private String message;
    @Override
	public boolean isEmpty() {
		return this.notificationId == null;
	}
    
}