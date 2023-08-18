// Copyright (c) 2014-2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
// Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
package com.softwareag.c8y.push.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class NotificationRequest {

	private Message message;

	@JsonFormat(shape = JsonFormat.Shape.ARRAY)
	private final List<String> tags = new ArrayList<>();

	public static NotificationRequest build(final String title, final String alarmId) {
		final NotificationRequest request = new NotificationRequest();
		final Message message = new Message();
		request.setMessage(message);
		message.setTitle(title);
		final Body body = new Body();
		body.setAlarmId(alarmId);
		message.setBody(body);
		return request;
	}

	public void appendTag(final String tag) {
		tags.add(tag);
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(final Message message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "NotificationRequest [message=" + message + ", tags=" + tags + "]";
	}

	public static class Message {

		private String title;
		private Body body;

		public String getTitle() {
			return title;
		}

		public void setTitle(final String title) {
			this.title = title;
		}

		public Body getBody() {
			return body;
		}

		public void setBody(final Body body) {
			this.body = body;
		}

		@Override
		public String toString() {
			return "Message [title=" + title + ", body=" + body + "]";
		}
	}

	public static class Body {

		private String alarmId;

		public String getAlarmId() {
			return alarmId;
		}

		public void setAlarmId(final String alarmId) {
			this.alarmId = alarmId;
		}

		@Override
		public String toString() {
			return "Body [alarmId=" + alarmId + "]";
		}
	}
}
