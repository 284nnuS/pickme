package tech.zoomidsoon.pickme_restful_api.utils;

public class Env {
	public static String get(String name, String defaultValue) {
		try {
			String value = System.getenv(name);
			if (value.isBlank())
				return defaultValue;
			return value;
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
