package tech.zoomidsoon.pickme_backend.models;

public class Hello {
	private String message;

	public Hello() {
	}

	public Hello(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}