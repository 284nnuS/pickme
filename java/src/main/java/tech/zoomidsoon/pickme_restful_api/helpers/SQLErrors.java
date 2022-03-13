package tech.zoomidsoon.pickme_restful_api.helpers;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.*;

@AllArgsConstructor
public enum SQLErrors {
	DUPLICATE_ENTRY(1062, "Duplicate entry"),
	DATA_TRUNCATED(1265, "Data truncated"),
	INCORRECT_DATA_TYPE(1366, "Invalid data type"),
	TRIGGER_EXCEPTION(1644, "SQL Trigger prevent insert"),
	CHECK_CONSTANT(3819, "Check constant");

	public final int errCode;
	public final String message;

	private static final Map<Integer, SQLErrors> toErr = Arrays.stream(values())
			.collect(Collectors.toMap(el -> el.errCode, el -> el));

	public static SQLErrors fromErrCode(int errCode) {
		return toErr.get(errCode);
	}
}
