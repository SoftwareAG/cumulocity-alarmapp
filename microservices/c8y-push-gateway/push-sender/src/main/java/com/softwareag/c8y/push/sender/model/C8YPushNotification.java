package com.softwareag.c8y.push.sender.model;

import java.util.Map;

public class C8YPushNotification {

	private final Map<String, String> properties;
	private final PushTemplate pushTemplate;

	public C8YPushNotification(final Map<String, String> properties, final PushTemplate pushTemplate) {
		this.properties = properties;
		this.pushTemplate = pushTemplate;
	}

	public String createTemplate() {
		String modifiedTemplate = pushTemplate.getBody();
		for (final String key : properties.keySet()) {
			final String keyToChange = String.format("$(%s)", key);
			if (modifiedTemplate.contains(keyToChange)) {
				modifiedTemplate = modifiedTemplate.replace(keyToChange, properties.get(key));
			}
		}
		return modifiedTemplate;
	}
}
