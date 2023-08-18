// Copyright (c) 2014-2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
// Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
package com.softwareag.c8y.push.sender.model;

import java.util.List;

public class NotificationRequest {

	private Receiver receiver;
	private Message message;
	private List<String> tags;

	public Receiver getReceiver() {
		return receiver;
	}

	public void setReceiver(final Receiver receiver) {
		this.receiver = receiver;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(final Message message) {
		this.message = message;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(final List<String> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "NotificationRequest [receiver=" + receiver + ", message=" + message + ", tags=" + tags + "]";
	}

	public static class Receiver {

		private List<String> userIds;
		private List<String> deviceTokens;

		public List<String> getUserIds() {
			return userIds;
		}

		public void setUserIds(final List<String> userIds) {
			this.userIds = userIds;
		}

		public List<String> getDeviceTokens() {
			return deviceTokens;
		}

		public void setDeviceTokens(final List<String> deviceTokens) {
			this.deviceTokens = deviceTokens;
		}
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
