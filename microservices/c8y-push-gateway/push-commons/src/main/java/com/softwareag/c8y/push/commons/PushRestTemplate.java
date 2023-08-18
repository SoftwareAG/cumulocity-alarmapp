package com.softwareag.c8y.push.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;

@Component
public class PushRestTemplate extends RestTemplate {

	private static final Logger log = LoggerFactory.getLogger(PushRestTemplate.class);

	@Value("${C8Y.bootstrap.tenant}")
	private String bootstrapTenant;

	private static SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
		final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(60000);
		factory.setReadTimeout(60000);
		return factory;
	}

	private MicroserviceCredentials serviceCredentials;

	@Autowired
	public PushRestTemplate() {
		super(getClientHttpRequestFactory());
	}

	@EventListener
	public void handleEvent(final Object event) {
		if (serviceCredentials == null) {
			if (event instanceof MicroserviceSubscriptionAddedEvent) {
				final MicroserviceSubscriptionAddedEvent subscriptionAddedEvent = (MicroserviceSubscriptionAddedEvent) event;
				if (subscriptionAddedEvent.getCredentials().getTenant().equals(bootstrapTenant)) {
					serviceCredentials = subscriptionAddedEvent.getCredentials();
					log.info(String.format("received MicroserviceSubscriptionAddedEvent on tenant %s ",
							serviceCredentials.getTenant()));
					getInterceptors().add(new BasicAuthenticationInterceptor(
							serviceCredentials.getTenant() + '/' + serviceCredentials.getUsername(),
							serviceCredentials.getPassword()));
				}
			}
		}
	}
}
