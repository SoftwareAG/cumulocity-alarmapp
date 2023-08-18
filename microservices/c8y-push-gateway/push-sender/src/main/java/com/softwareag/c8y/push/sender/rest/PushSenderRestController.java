package com.softwareag.c8y.push.sender.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.softwareag.c8y.push.commons.exceptions.ValidationException;
import com.softwareag.c8y.push.commons.model.Error;
import com.softwareag.c8y.push.sender.model.NotificationRequest;
import com.softwareag.c8y.push.sender.service.PushSenderService;

@RestController
public class PushSenderRestController {

	private static final Logger LOG = LoggerFactory.getLogger(PushSenderRestController.class);

	@Autowired
	private PushSenderService pushSenderService;

	/**
	 * Asks the Azure notification hub to trigger a push notification. Sends a push
	 * notification to the specified receivers. If no receiver is specified, the
	 * push notification will be send to each registration. Additional tags can be
	 * set to filter registrations.
	 *
	 * @param request
	 * @return
	 */
	@PostMapping(path = "/notification", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> sendNotification(@RequestBody final NotificationRequest notificationRequest) {
		LOG.info("Received push notification request.");
		// '201': "Successfully sent notification."
		// '400': "Could not send notification."
		// -> "Connection to Azure notification hub failed."
		// -> "There are missing properties in the request."
		ResponseEntity<?> response = ResponseEntity.noContent().build();
		try {
			pushSenderService.sendNotification(notificationRequest);
			LOG.info(String.format("Successfully sent push notification for alarm #%s.",
					notificationRequest.getMessage().getBody().getAlarmId()));
		} catch (final ValidationException e) {
			response = ResponseEntity.badRequest().body(new Error(e.getMessage()));
		}
		return response;
	}
}
