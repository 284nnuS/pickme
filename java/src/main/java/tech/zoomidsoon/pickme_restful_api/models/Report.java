package tech.zoomidsoon.pickme_restful_api.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class Report extends Entity {

	private Long reportId;
	private Integer reporter;
	private Integer reported;
	private Long time;
	private String tag;
	private String additionalInfo;
	private String resolved;

	@Override
	public boolean isEmpty() {

		if (reportId == null)
			return true;
		return false;
	}

}
