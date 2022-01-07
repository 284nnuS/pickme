package tech.zoomidsoon.pickme_backend;

import com.google.gson.Gson;

public class Hello {
	private static Gson gson = new Gson();
	private String message;

	public Hello(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public static Hello fromJson(String data) {
		return gson.fromJson(data, Hello.class);
	}
}