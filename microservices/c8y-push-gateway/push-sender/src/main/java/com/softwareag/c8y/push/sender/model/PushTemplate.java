package com.softwareag.c8y.push.sender.model;

public class PushTemplate {

	private String name;
	private String body;
	private String environment;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getBody() {
		return body;
	}

	public void setBody(final String body) {
		this.body = body;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(final String environment) {
		this.environment = environment;
	}

	@Override
	public String toString() {
		return "PushTemplate [name=" + name + ", body=" + body + ", environment=" + environment + "]";
	}
}
