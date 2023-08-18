package com.softwareag.c8y.push.commons.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.softwareag.c8y.push.commons.exceptions.HubConnectionException;
import com.softwareag.c8y.push.commons.exceptions.ValidationException;
import com.softwareag.c8y.push.commons.model.AzureConfiguration;
import com.softwareag.c8y.push.commons.model.Error;
import com.softwareag.c8y.push.commons.service.PushSettingsService;

@RestController
public class PushSettingsRestController {

	@Autowired
	private PushSettingsService pushSettingsService;

	/**
	 * Get the current Azure notification hub authorization keys.
	 * @return
	 */
	@GetMapping(path = "/configuration", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getConfiguration() {
		// 200: "The request has succeeded and the configuration is sent in the response."
		ResponseEntity<?> response = null;
		try {
			final AzureConfiguration configuration = pushSettingsService.getConfiguration();
			response = ResponseEntity.ok(configuration);
		} catch (final ValidationException e) {
			response = ResponseEntity.badRequest().body(new Error(e.getMessage()));
		}
		return response;
	}

	/**
	 * Updates the Azure notification hub authorization keys.
	 * @return
	 */
	@PutMapping(path = "/configuration", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateConfiguration(@RequestBody final AzureConfiguration configuration) {
		// 201: "The configuration has been updated."
		// 400: "Could not update configuration."
		// -> "There are missing properties in the request body."
		ResponseEntity<?> response = ResponseEntity.noContent().build();
		try {
			pushSettingsService.updateConfiguration(configuration);
		} catch (final HubConnectionException e) {
			response = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new Error(e.getMessage()));
		} catch (final ValidationException e) {
			response = ResponseEntity.badRequest().body(new Error(e.getMessage()));
		}
		return response;
	}
}
