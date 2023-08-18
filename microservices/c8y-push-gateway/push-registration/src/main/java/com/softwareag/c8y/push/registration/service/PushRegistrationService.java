// Copyright (c) 2014-2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
// Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
package com.softwareag.c8y.push.registration.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cumulocity.rest.representation.user.UserRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.user.UserApi;
import com.softwareag.c8y.push.commons.exceptions.HubConnectionException;
import com.softwareag.c8y.push.commons.exceptions.ValidationException;
import com.softwareag.c8y.push.commons.service.ConnectionHubService;
import com.softwareag.c8y.push.commons.service.PushSettingsService;
import com.softwareag.c8y.push.registration.model.DeviceRegistration;
import com.softwareag.c8y.push.registration.model.DeviceRegistration.Device;
import com.softwareag.c8y.push.registration.service.ITagManager.Tag;
import com.windowsazure.messaging.Installation;
import com.windowsazure.messaging.NotificationHubClient;
import com.windowsazure.messaging.NotificationHubsException;
import com.windowsazure.messaging.NotificationPlatform;
import com.windowsazure.messaging.Registration;

@Service
public class PushRegistrationService {

	private static final Logger LOG = LoggerFactory.getLogger(PushRegistrationService.class);

	@Autowired
	private ConnectionHubService connectionHubService;

	@Autowired
	private PushSettingsService pushSettingsService;

	@Autowired
	private UserApi userApi;

	public void registerDevice(final DeviceRegistration registration)
			throws HubConnectionException, ValidationException {
		// verify payload
		validate(registration);

		// verify user id is existing
		final UserRepresentation user = getUser(registration.getUserId());

		// create registration on Azure
		final NotificationHubClient hub = connectionHubService.get();
		final String deviceToken = registration.getDevice().getDeviceToken();
		final String safeUserId = pushSettingsService.encodeSafeUserId(registration.getUserId());
		final String installationId = safeUserId + DigestUtils.sha256Hex(deviceToken);
		final NotificationPlatform notificationPlatform = registration.getDevice().getPlatform()
				.getNotificationPlatform();
		final Installation installation = new Installation(installationId, notificationPlatform, deviceToken);
		registration.getTags().ifPresent(e -> e.forEach(tag -> installation.addTag(tag)));
		final ITagManager tagManager = ITagManager.BUILDER.create(installation);
		tagManager.addTag(Tag.BundleId, registration.getBundleId());
		tagManager.addTag(Tag.UserId, safeUserId);
		try {
			hub.createOrUpdateInstallation(installation);
		} catch (final NotificationHubsException e) {
			throw new ValidationException(e.getMessage());
		}
	}

	private void validate(final DeviceRegistration registration) throws ValidationException {
		if (registration.getUserId() == null) {
			throw new ValidationException("Property 'userId' must not be null.");
		}
		if (registration.getBundleId() == null) {
			throw new ValidationException("Property 'bundleId' must not be null.");
		}
		if (registration.getDevice() == null) {
			throw new ValidationException("Property 'device' must not be null.");
		}
		if (registration.getDevice().getDeviceToken() == null) {
			throw new ValidationException("Property 'deviceToken' must not be null.");
		}
		if (registration.getDevice().getPlatform() == null) {
			throw new ValidationException("Property 'platform' must not be null.");
		}
	}

	public List<DeviceRegistration> getRegistrations(final Optional<String> tag) throws HubConnectionException {
		final NotificationHubClient hub = connectionHubService.get();
		try {
			List<Registration> registrations = null;
			if (tag.isPresent()) {
				registrations = hub.getRegistrationsByTag(tag.get()).getRegistrations();
			} else {
				registrations = hub.getRegistrations().getRegistrations();
			}
			return registrations.stream().map(e -> convert(e)).collect(Collectors.toList());
		} catch (final NotificationHubsException e) {
			throw new HubConnectionException();
		}
	}

	public void removeRegistrations() throws HubConnectionException {
		final NotificationHubClient hub = connectionHubService.get();
		try {
			final List<Registration> registrations = getRegistrations();
			for (final Registration registration : registrations) {
				hub.deleteRegistration(registration);
			}
		} catch (final NotificationHubsException e) {
			throw new HubConnectionException();
		}
	}

	public List<DeviceRegistration> getRegistrationsByUserId(final String userId)
			throws HubConnectionException, ValidationException {
		// verify C8y user is existing
		getUser(userId);

		final String safeUserId = pushSettingsService.encodeSafeUserId(userId);
		return getRegistrations(Optional.of(Tag.UserId.getValue(safeUserId)));
	}

	public void removeRegistration(final String deviceToken) throws HubConnectionException {
		final NotificationHubClient hub = connectionHubService.get();
		try {
			final List<Registration> registrations = hub.getRegistrationsByChannel(deviceToken).getRegistrations();
			for (final Registration registration : registrations) {
				hub.deleteRegistration(registration);
			}
			LOG.info("Successfully deregistered PushRegistration service.");
		} catch (final NotificationHubsException e) {
			throw new HubConnectionException(
					String.format("Failed to remove device registration due to %s ", e.getMessage()));
		}
	}

	private DeviceRegistration convert(final Registration registration) {
		final DeviceRegistration deviceRegistration = new DeviceRegistration();
		deviceRegistration.setTags(registration.getTags().stream().collect(Collectors.toList()));

		final ITagManager tagManager = ITagManager.BUILDER.create(registration);
		deviceRegistration.setBundleId(tagManager.getTag(Tag.BundleId));
		final String safeUserId = tagManager.getTag(Tag.UserId);
		if (safeUserId != null) {
			deviceRegistration.setUserId(pushSettingsService.decodeSafeUserId(safeUserId));
		}

		final Device device = new Device();
		deviceRegistration.setDevice(device);
		device.setDeviceToken(tagManager.getDeviceToken());
		device.setPlatform(tagManager.getPlatform());

		return deviceRegistration;
	}

	private List<Registration> getRegistrations() throws NotificationHubsException {
		final NotificationHubClient hub = connectionHubService.get();
		return hub.getRegistrations().getRegistrations();
	}

	private UserRepresentation getUser(final String userName) throws ValidationException {
		try {
			final UserRepresentation representation = userApi.getUser(pushSettingsService.currentTenant(), userName);
			return representation;
		} catch (final SDKException e) {
			throw new ValidationException("No Cumulocity IoT user found.");
		}
	}
}
