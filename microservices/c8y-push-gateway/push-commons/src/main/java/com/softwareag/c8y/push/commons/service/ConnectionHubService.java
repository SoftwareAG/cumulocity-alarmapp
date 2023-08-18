// Copyright (c) 2014-2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
// Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
package com.softwareag.c8y.push.commons.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softwareag.c8y.push.commons.model.AzureConfiguration;
import com.windowsazure.messaging.NotificationHub;
import com.windowsazure.messaging.NotificationHubClient;

@Service
public class ConnectionHubService {

	@Autowired
	private PushSettingsService pushSettingsService;

	public NotificationHubClient get() {
		return new NotificationHub(pushSettingsService.getConnectionString(), pushSettingsService.getHubPath());
	}

	public NotificationHubClient get(final AzureConfiguration temporaryConfiguration) {
		return new NotificationHub(temporaryConfiguration.getConnection(), temporaryConfiguration.getHub());
	}
}
