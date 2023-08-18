package com.softwareag.c8y.push.registration.rest;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.softwareag.c8y.push.commons.exceptions.HubConnectionException;
import com.softwareag.c8y.push.commons.exceptions.ValidationException;
import com.softwareag.c8y.push.commons.model.Error;
import com.softwareag.c8y.push.registration.model.DeviceRegistration;
import com.softwareag.c8y.push.registration.service.PushRegistrationService;

@RestController
public class PushRegistrationsRestController {

	private static final Logger LOG = LoggerFactory.getLogger(PushRegistrationsRestController.class);

	@Autowired
	private PushRegistrationService pushRegistrationService;

	@Autowired
	public PushRegistrationsRestController() {
	}

	/**
	 * Registers a device at the tenant.
	 * Creates a Azure registration within the configured notification hub. Needs to be performed once the device token is obtained from APNS or Firebase.
	 * @return
	 * @return
	 */
	@PostMapping(path = "/registrations", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registerDevice(final @RequestBody DeviceRegistration registration) {
		LOG.info("Received device registration.");
		// 201 "Successfully registered device."
		// 400 "Could not create Azure registration."
		// -> "Connection to Azure notification hub failed."
		// -> "No Cumulocity IoT user found."
		// -> "Device token has been already registered."
		// -> "Subscription is not valid."
		// 401 "Authentication information is missing or invalid."
		ResponseEntity<?> response = ResponseEntity.noContent().build();
		try {
			pushRegistrationService.registerDevice(registration);
			LOG.info(String.format("Installed device registration for token %s.", registration.getDevice().getDeviceToken()));
		} catch (final ValidationException e) {
			response = ResponseEntity.badRequest().body(new Error(e.getMessage()));
			LOG.info(String.format("Failed device registration: %s", e.getMessage()));
		}
		return response;
	}

	/**
	 * Receive a list of all registrations.
	 * Registrations can be optionally filtered by a tag.
	 * @return
	 * @return
	 */
	@GetMapping(path = "/registrations", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getRegistrations(final @QueryParam(value = "tag") String tag) {
		// 200 "The request has succeeded and registrations are sent in the response."
		// 400 "Could not retrieve registrations."
		// -> "Connection to Azure notification hub failed."
		// 401 "Authentication information is missing or invalid."
		ResponseEntity<?> response = null;
		try {
			final List<DeviceRegistration> registrations = pushRegistrationService.getRegistrations(Optional.ofNullable(tag));
			response = ResponseEntity.ok(registrations);
		} catch (final HubConnectionException e) {
			response = ResponseEntity.badRequest().body(new Error(e.getMessage()));
		}
		return response;
	}

	/**
	 * Get all registrations for a Cumulocity IoT user id.
	 * @return
	 * @return
	 */
	@GetMapping(path = "/registrations/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getRegistrationsByUserId(final @PathVariable(name = "userId") String userId) {
		// 200 "The request has succeeded and registrations are sent in the response."
		// 401 "Could not retrieve registrations."
		// -> "Could not find the Cumulocity IoT user id."
		// -> "Connection to Azure notification hub failed."
		// 401 "Authentication information is missing or invalid."
		ResponseEntity<?> response = null;
		try {
			final List<DeviceRegistration> registrations = pushRegistrationService.getRegistrationsByUserId(userId);
			response = ResponseEntity.ok(registrations);
		} catch (final ValidationException e) {
			response = ResponseEntity.badRequest().body(new Error(e.getMessage()));
		}
		return response;
	}

	/**
	 * Removes all registrations from the Azure notification hub.
	 * @return
	 * @return
	 */
	@DeleteMapping(path = "/registrations", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> removeRegistrations() {
		// 201 "Successfully deleted all registrations."
		// 400 "Could not create Azure registration."
		// -> "Connection to Azure notification hub failed."
		// 401 "Authentication information is missing or invalid."
		ResponseEntity<?> response = ResponseEntity.noContent().build();
		try {
			pushRegistrationService.removeRegistrations();
		} catch (final HubConnectionException e) {
			response = ResponseEntity.badRequest().body(new Error(e.getMessage()));
		}
		return response;
	}

	/**
	 * Removes a registration given a device token.
	 * @return
	 * @return
	 */
	@DeleteMapping(path = "/registrations/{deviceToken}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> removeRegistration(final @PathVariable(name = "deviceToken") String deviceToken) {
		// 201 "The registration has been removed."
		// 400 "The registration could not been removed."
		// -> "Connection to Azure notification hub failed."
		// -> "The device token is not yet registered."
		// 401 "Authentication information is missing or invalid."
		ResponseEntity<?> response = ResponseEntity.noContent().build();
		try {
			pushRegistrationService.removeRegistration(deviceToken);
		} catch (final HubConnectionException e) {
			response = ResponseEntity.badRequest().body(new Error(e.getMessage()));
		}
		return response;
	}
}
