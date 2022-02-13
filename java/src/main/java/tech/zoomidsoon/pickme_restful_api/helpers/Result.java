package tech.zoomidsoon.pickme_restful_api.helpers;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class Result<T, E> {
	private T data;
	private E error;

	public boolean isOk() {
		return data != null;
	}
}
