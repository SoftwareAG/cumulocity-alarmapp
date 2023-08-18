// Copyright (c) 2014-2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
// Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
package com.softwareag.c8y.push.registration.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import com.softwareag.c8y.push.registration.model.DeviceRegistration;
import com.softwareag.c8y.push.registration.model.DeviceRegistration.Platform;
import com.windowsazure.messaging.AppleRegistration;
import com.windowsazure.messaging.GcmRegistration;
import com.windowsazure.messaging.Installation;
import com.windowsazure.messaging.NotificationPlatform;
import com.windowsazure.messaging.Registration;

@SuppressWarnings("deprecation")
public interface ITagManager {

	String getDeviceToken();
	String getTag(final Tag tag);
	void addTag(final Tag tag, final String value);
	void remove(final Tag tag);
	void removeCustomTags();
	DeviceRegistration.Platform getPlatform();
	Builder BUILDER = new Builder();

	public enum Tag {
		UserId("userId:"), BundleId("bundleId:"), Id("$InstallationId:");

		private final String id;

		Tag(final String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public String getValue(final String value) {
			return getId() + value;
		}

		public String getValue(final Collection<String> tags) {
			final Optional<String> value = tags.stream().filter(e -> e.startsWith(getId())).findFirst();
			if (value.isPresent()) {
				return value.get().replaceAll(getId(), "");
			}
			return null;
		}

		public boolean isTag(final String value) {
			return value.startsWith(getId());
		}

		public static boolean isAnyTag(final String value) {
			return Arrays.stream(Tag.values()).anyMatch(e -> value.startsWith(e.getId()));
		}
	}

	class Builder {

		ITagManager create(final Registration registration) {
			return new RegistrationTagManager(registration);
		}

		ITagManager create(final Installation installation) {
			return new InstallationTagManager(installation);
		}
	}

	class RegistrationTagManager implements ITagManager {

		private final Registration registration;

		RegistrationTagManager(final Registration registration) {
			this.registration = registration;
		}

		@Override
		public String getDeviceToken() {
			if (registration instanceof AppleRegistration) {
				return ((AppleRegistration) registration).getDeviceToken();
			} else if (registration instanceof GcmRegistration) {
				return ((GcmRegistration) registration).getGcmRegistrationId();
			}
			return null;
		}

		@Override
		public String getTag(final Tag tag) {
			return tag.getValue(registration.getTags());
		}

		@Override
		public void addTag(final Tag tag, final String value) {
			registration.getTags().add(tag.getValue(value));
		}

		@Override
		public Platform getPlatform() {
			return registration instanceof AppleRegistration ? Platform.IOS : Platform.Android;
		}

		@Override
		public void removeCustomTags() {
			registration.getTags().removeIf(e -> !Tag.isAnyTag(e));
		}

		@Override
		public void remove(final Tag tag) {
			registration.getTags().removeIf(e -> tag.isTag(e));
		}
	}

	class InstallationTagManager implements ITagManager {

		private final Installation installation;

		InstallationTagManager(final Installation installation) {
			this.installation = installation;
		}

		@Override
		public String getDeviceToken() {
			return null;
		}

		@Override
		public String getTag(final Tag tag) {
			return tag.getValue(installation.getTags());
		}

		@Override
		public void addTag(final Tag tag, final String value) {
			installation.addTag(tag.getValue(value));
		}

		@Override
		public Platform getPlatform() {
			return installation.getPlatform() == NotificationPlatform.Apns ? Platform.IOS : Platform.Android;
		}

		@Override
		public void removeCustomTags() {
			installation.getTags().removeIf(e -> !Tag.isAnyTag(e));
		}

		@Override
		public void remove(final Tag tag) {
			installation.getTags().removeIf(e -> tag.isTag(e));
		}
	}
}
