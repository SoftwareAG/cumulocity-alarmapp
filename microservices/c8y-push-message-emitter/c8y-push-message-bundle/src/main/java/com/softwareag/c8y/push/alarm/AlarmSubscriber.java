package com.softwareag.c8y.push.alarm;

import com.cumulocity.sdk.client.PlatformParameters;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.notification.SubscribeOperationListener;
import com.cumulocity.sdk.client.notification.Subscriber;
import com.cumulocity.sdk.client.notification.SubscriberBuilder;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import com.cumulocity.sdk.client.notification.SubscriptionNameResolver;

public class AlarmSubscriber implements Subscriber<String, AlarmNotification> {

	private static final String REALTIME_NOTIFICATIONS_URL = "cep/realtime";

	private final Subscriber<String, AlarmNotification> subscriber;

	private static final String CHANNEL_PREFIX = "/alarms/";

	public AlarmSubscriber(final PlatformParameters parameters) {
		subscriber = createSubscriber(parameters);
	}

	private Subscriber<String, AlarmNotification> createSubscriber(final PlatformParameters parameters) {
		return SubscriberBuilder.<String, AlarmNotification>anSubscriber().withParameters(parameters)
				.withEndpoint(REALTIME_NOTIFICATIONS_URL).withSubscriptionNameResolver(new Identity())
				.withDataType(AlarmNotification.class).build();
	}

	@Override
	public Subscription<String> subscribe(final String channelID,
			final SubscriptionListener<String, AlarmNotification> handler) throws SDKException {
		return subscriber.subscribe(CHANNEL_PREFIX + channelID, handler);
	}

	@Override
	public void disconnect() {
		subscriber.disconnect();
	}

	@Override
	public Subscription<String> subscribe(final String arg0, final SubscribeOperationListener arg1,
			final SubscriptionListener<String, AlarmNotification> arg2, final boolean arg3) throws SDKException {
		return null;
	}

	private static final class Identity implements SubscriptionNameResolver<String> {

		@Override
		public String apply(final String id) {
			return id;
		}
	}
}