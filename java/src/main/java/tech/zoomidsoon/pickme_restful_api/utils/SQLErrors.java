package tech.zoomidsoon.pickme_restful_api.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.*;

@AllArgsConstructor
public enum SQLErrors {
	DUPLICATE_ENTRY(1062, "Duplicate entry"),
	INCORRECT_DATA_TYPE(1366, "Invalid data type"),
	CHECK_CONSTANT(3819, "Check constant");

	public final int errCode;
	public final String message;

	private static final Map<Integer, SQLErrors> toErr = Arrays.stream(values())
			.collect(Collectors.toMap(el -> el.errCode, el -> el));

	public static SQLErrors fromErrCode(int errCode) {
		return toErr.get(errCode);
	}
}
