package com.softwareag.c8y.push.sender.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.sdk.client.alarm.AlarmApi;
import com.softwareag.c8y.push.commons.exceptions.ValidationException;
import com.softwareag.c8y.push.commons.service.ConnectionHubService;
import com.softwareag.c8y.push.commons.service.PushSettingsService;
import com.softwareag.c8y.push.registration.model.DeviceRegistration.Platform;
import com.softwareag.c8y.push.registration.service.ITagManager;
import com.softwareag.c8y.push.sender.model.C8YPushNotification;
import com.softwareag.c8y.push.sender.model.NotificationRequest;
import com.softwareag.c8y.push.sender.model.NotificationRequest.Receiver;
import com.softwareag.c8y.push.sender.model.PushTemplate;
import com.windowsazure.messaging.Notification;
import com.windowsazure.messaging.NotificationHubClient;
import com.windowsazure.messaging.NotificationHubsException;

@Service
public class PushSenderService {

	@Autowired
	private ConnectionHubService connectionHubService;

	@Autowired
	private PushTemplateService templateService;

	@Autowired
	private PushSettingsService pushSettingsService;

	@Autowired
	private AlarmApi alarmApi;

	@SuppressWarnings("deprecation")
	public void sendNotification(final NotificationRequest notificationRequest) throws ValidationException {
		// validate
		validate(notificationRequest);
		// create templates
		final Map<String, String> properties = new HashMap<>();
		properties.put("title", notificationRequest.getMessage().getTitle());

		// find the alarm
		try {
			final String alarmId = notificationRequest.getMessage().getBody().getAlarmId();
			final AlarmRepresentation alarm = alarmApi.getAlarm(GId.asGId(alarmId));
			if (alarm != null) {
				properties.put("body", alarm.getText());
				properties.put("alarmId", alarm.getId().getValue());
				properties.put("sourceId", alarm.getSource().getId().getValue());
			} else {
				throw new ValidationException("Alarm with id " + alarmId + " could not be found.");
			}
		} catch (final Exception e) {
			throw new ValidationException(e.getMessage());
		}

		final PushTemplate appleTemplate = getTemplate(Platform.IOS);
		final PushTemplate androidTemplate = getTemplate(Platform.Android);

		final String appleBody = new C8YPushNotification(properties, appleTemplate).createTemplate();
		final String androidBody = new C8YPushNotification(properties, androidTemplate).createTemplate();
		final Notification appleNotification = Notification.createAppleNotification(appleBody);
		final Notification androidNotification = Notification.createGcmNotification(androidBody);

		// build tag list to filter receiving registrations
		final Set<String> tags = new HashSet<>();
		if (notificationRequest.getTags() != null) {
			notificationRequest.getTags().stream().forEach(e -> tags.add(e));
		}

		final Receiver receiver = notificationRequest.getReceiver();
		if (receiver != null) {
			final List<String> deviceTokens = receiver.getDeviceTokens();
			if (deviceTokens != null && !deviceTokens.isEmpty()) {
				sendDirectNotification(appleNotification, receiver.getDeviceTokens());
				sendDirectNotification(androidNotification, receiver.getDeviceTokens());
			} else if (receiver.getUserIds() != null) {
				receiver.getUserIds().stream().forEach(e -> {
					final String safeUserId = pushSettingsService.encodeSafeUserId(e);
					tags.add(ITagManager.Tag.UserId.getValue(safeUserId));
				});
				sendNotification(appleNotification, tags);
				sendNotification(androidNotification, tags);
			}
		} else {
			sendNotification(appleNotification, tags);
			sendNotification(androidNotification, tags);
		}
	}

	private void validate(final NotificationRequest request) throws ValidationException {
		if (request.getMessage() == null) {
			throw new ValidationException("Property 'message' must not be null.");
		}
		if (request.getMessage().getTitle() == null) {
			throw new ValidationException("Property 'title' must not be null.");
		}
		if (request.getMessage().getBody() == null) {
			throw new ValidationException("Property 'body' must not be null.");
		}
		if (request.getMessage().getBody().getAlarmId() == null) {
			throw new ValidationException("Property 'alarmId' must not be null.");
		}
	}

	private void sendNotification(final Notification notification, final Set<String> tags) throws ValidationException {
		try {
			final NotificationHubClient hub = connectionHubService.get();
			if ((tags == null) || tags.isEmpty()) {
				hub.sendNotification(notification);
			} else {
				hub.sendNotification(notification, tags);
			}
		} catch (final NotificationHubsException e) {
			throw new ValidationException(e.getMessage());
		}
	}

	private void sendDirectNotification(final Notification notification, final List<String> deviceList)
			throws ValidationException {
		try {
			final NotificationHubClient hub = connectionHubService.get();
			hub.sendDirectNotification(notification, deviceList);
		} catch (final NotificationHubsException e) {
			throw new ValidationException(e.getMessage());
		}
	}

	private PushTemplate getTemplate(final Platform platform) {
		return templateService.getTemplate(platform);
	}
}