package com.softwareag.c8y.push.sender.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.softwareag.c8y.push.registration.model.DeviceRegistration.Platform;
import com.softwareag.c8y.push.sender.model.PushTemplate;

@Service
public class PushTemplateService {

	private static final String DEFAULT_TEMPLATE_NAME = "default";

	@Value("${default.template.ios}")
	private String defaultIOSTemplate;

	@Value("${default.template.android}")
	private String defaultAndroidTemplate;

	private PushTemplate createTemplate(final String body, final String environment, final String name) {
		final PushTemplate pushTemplate = new PushTemplate();
		pushTemplate.setBody(body);
		pushTemplate.setEnvironment(environment);
		pushTemplate.setName(name);
		return pushTemplate;
	}

	public PushTemplate getTemplate(final Platform platform) {
		if (platform == Platform.IOS) {
			return createTemplate(defaultIOSTemplate, platform.name(), DEFAULT_TEMPLATE_NAME);
		} else {
			return createTemplate(defaultAndroidTemplate, platform.name(), DEFAULT_TEMPLATE_NAME);
		}
	}
}
