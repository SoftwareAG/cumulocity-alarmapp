package com.softwareag.c8y.push.registration.model;

import java.util.List;
import java.util.Optional;

import com.windowsazure.messaging.NotificationPlatform;

public class DeviceRegistration {

	private String userId;
	private String bundleId;
	private List<String> tags;
	private Device device;

	public String getUserId() {
		return userId;
	}

	public String getBundleId() {
		return bundleId;
	}

	public Optional<List<String>> getTags() {
		return Optional.ofNullable(tags);
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public void setBundleId(final String bundleId) {
		this.bundleId = bundleId;
	}

	public void setTags(final List<String> tags) {
		this.tags = tags;
	}

	public void setDevice(final Device device) {
		this.device = device;
	}

	public Device getDevice() {
		return device;
	}

	public static class Device {

		private String deviceToken;
		private Platform platform;

		public String getDeviceToken() {
			return deviceToken;
		}

		public Platform getPlatform() {
			return platform;
		}

		public void setDeviceToken(final String deviceToken) {
			this.deviceToken = deviceToken;
		}

		public void setPlatform(final Platform platform) {
			this.platform = platform;
		}
	}

	public enum Platform {
		IOS(NotificationPlatform.Apns), Android(NotificationPlatform.Gcm);

		private final NotificationPlatform notificationPlatform;

		Platform(final NotificationPlatform notificationPlatform) {
			this.notificationPlatform = notificationPlatform;
		}

		public NotificationPlatform getNotificationPlatform() {
			return notificationPlatform;
		}
	}
}
