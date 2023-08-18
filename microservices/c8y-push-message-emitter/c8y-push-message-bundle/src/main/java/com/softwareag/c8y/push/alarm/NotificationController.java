package com.softwareag.c8y.push.alarm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.svenson.JSON;
import org.svenson.JSONParser;

import com.cumulocity.microservice.context.ContextService;
import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.repository.application.ApplicationApi;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.model.JSONBase;
import com.cumulocity.rest.representation.alarm.AlarmRepresentation;
import com.cumulocity.rest.representation.audit.AuditRecordRepresentation;
import com.cumulocity.sdk.client.RestConnector;
import com.cumulocity.sdk.client.audit.AuditRecordApi;
import com.cumulocity.sdk.client.audit.AuditRecordCollection;
import com.cumulocity.sdk.client.audit.AuditRecordFilter;
import com.cumulocity.sdk.client.notification.Subscription;
import com.cumulocity.sdk.client.notification.SubscriptionListener;
import com.softwareag.c8y.push.model.NotificationRequest;
import com.softwareag.c8y.push.rest.PushRestTemplate;

@Controller
public class NotificationController {

	private static final Logger LOG = LoggerFactory.getLogger(NotificationController.class);

	private static final JSONParser jsonParser = JSONBase.getJSONParser();

	private static final JSON json = JSON.defaultJSON();

	private MicroserviceCredentials microserviceCredentials;

	@Autowired
	private RestConnector restConnector;

	@Autowired
	private ContextService<MicroserviceCredentials> contextService;

	@Autowired
	private AuditRecordApi auditApi;

	@Autowired
	private MicroserviceSubscriptionsService subscriptions;

	@Autowired
	private PushRestTemplate restTemplate;

	@Autowired
	private AlarmFilter alarmFilter;

	@Value("${C8Y.baseURL}")
	private String baseUrl;

	@Value("${push.application.name}")
	private String pushAppName;

	@Autowired
	private ApplicationApi applicationApi;

	private String contextPath;

	@EventListener
	private void onMicroserviceSubscriptionAddedEvent(final MicroserviceSubscriptionAddedEvent event) {
		microserviceCredentials = contextService.getContext();
		// execute business logic in the context of a particular tenant
		subscriptions.runForTenant(event.getCredentials().getTenant(), () -> {
			final AlarmSubscriber subscriber = new AlarmSubscriber(restConnector.getPlatformParameters());
			final SubscriptionListener<String, AlarmNotification> listener = new AlarmSubscriptionListener();
			subscriber.subscribe("*", listener);
		});
		contextPath = applicationApi.getByName(pushAppName).get().getContextPath();
	}

	private class AlarmSubscriptionListener implements SubscriptionListener<String, AlarmNotification> {

		@Override
		public void onNotification(final Subscription<String> subscription, final AlarmNotification notification) {
			final AlarmRepresentation alarm = jsonParser.parse(AlarmRepresentation.class,
					json.forValue(notification.getData()));
			if (alarmFilter.test(alarm)) {
				LOG.info(String.format("Received alarm #%s update. Preparing push notification.",
						alarm.getId().getValue()));
				contextService.runWithinContext(microserviceCredentials, () -> {
					// Note: there is a limitation on azure: Only 6 tag references can be used in one tag expression.
					// So, we cannot chain deviceId/severity/status/type and have to send multiple requests
					final String title = computeNotificationTitle(alarm, notification);
					sendRequest(filterBasedOnSeverityAndStatus(title, alarm));
					sendRequest(filterBasedOnType(title, alarm));
					sendRequest(filterBasedOnDeviceId(title, alarm));
					sendRequest(filterBasedOnDeviceIdAndType(title, alarm));
				});
			} else {
				LOG.info(String.format("Ignoring alarm #%s as it does not match filter %s.", alarm.getId().getValue(),
						alarmFilter));
			}
		}

		private NotificationRequest filterBasedOnSeverityAndStatus(final String title,
				final AlarmRepresentation alarm) {
			final NotificationRequest request = NotificationRequest.build(title, alarm.getId().getValue());
			request.appendTag(String.format(
					"deviceId:all && type:all && (severity:all || severity:%s) && (status:all || status:%s)",
					alarm.getSeverity().toLowerCase(), alarm.getStatus().toLowerCase()));
			return request;
		}

		private NotificationRequest filterBasedOnDeviceId(final String title, final AlarmRepresentation alarm) {
			final NotificationRequest request = NotificationRequest.build(title, alarm.getId().getValue());
			request.appendTag(String.format(
					"deviceId:%s && type:all && (severity:all || severity:%s) && (status:all || status:%s)",
					alarm.getSource().getId().getValue(), alarm.getSeverity().toLowerCase(),
					alarm.getStatus().toLowerCase()));
			return request;
		}

		private NotificationRequest filterBasedOnType(final String title, final AlarmRepresentation alarm) {
			final NotificationRequest request = NotificationRequest.build(title, alarm.getId().getValue());
			request.appendTag(String.format(
					"deviceId:all && type:%s && (severity:all || severity:%s) && (status:all || status:%s)",
					alarm.getType(), alarm.getSeverity().toLowerCase(), alarm.getStatus().toLowerCase()));
			return request;
		}

		private NotificationRequest filterBasedOnDeviceIdAndType(final String title, final AlarmRepresentation alarm) {
			final NotificationRequest request = NotificationRequest.build(title, alarm.getId().getValue());
			request.appendTag(String.format(
					"deviceId:%s && type:%s && (severity:all || severity:%s) && (status:all || status:%s)",
					alarm.getSource().getId().getValue(), alarm.getType(), alarm.getSeverity().toLowerCase(),
					alarm.getStatus().toLowerCase()));
			return request;
		}

		private void sendRequest(final NotificationRequest request) {
			try {
				final String uri = String.format("%s/service/%s/notification", baseUrl, contextPath);
				restTemplate.postForEntity(uri, request, Object.class);
			} catch (final Exception e) {
				LOG.error("Exception occurred while triggering push notification.", e);
			}
		}

		private String computeNotificationTitle(final AlarmRepresentation alarm, final AlarmNotification notification) {
			String title = null;
			if ("CREATE".equalsIgnoreCase(notification.getRealtimeAction())) {
				title = String.format("New %s alarm!", alarm.getSeverity().toLowerCase());
			} else {
				title = findLatestModification(alarm);
			}
			return title;
		}

		private String findLatestModification(final AlarmRepresentation alarm) {
			final StringBuilder sb = new StringBuilder();
			final AuditRecordFilter filter = new AuditRecordFilter().bySource(alarm.getId().getValue()).byType("Alarm");
			final AuditRecordCollection audits = auditApi.getAuditRecordsByFilter(filter);
			final List<AuditRecordRepresentation> allAudits = audits.get(2000).getAuditRecords();
			if (!allAudits.isEmpty()) {
				// get reverse order as "revert" won't work here.
				allAudits.sort((left, right) -> {
					return left.getCreationDateTime().compareTo(right.getCreationDateTime()) * -1;
				});
				final AuditRecordRepresentation audit = allAudits.get(0);
				final boolean hasSeverityChange = audit.getChanges().stream()
						.anyMatch(e -> "severity".equalsIgnoreCase(e.getAttribute()));
				if (hasSeverityChange) {
					sb.append(String.format("Changed severity to %s.", alarm.getSeverity().toLowerCase()));
				}
				final boolean hasStatusChange = audit.getChanges().stream()
						.anyMatch(e -> "status".equalsIgnoreCase(e.getAttribute()));
				if (hasStatusChange) {
					sb.append(String.format("Changed status to %s.", alarm.getStatus().toLowerCase()));
				}
			}
			return sb.toString();
		}

		@Override
		public void onError(final Subscription<String> arg0, final Throwable arg1) {
			LOG.error("An error occurred for the measurement subscription", arg1);
		}
	}
}
