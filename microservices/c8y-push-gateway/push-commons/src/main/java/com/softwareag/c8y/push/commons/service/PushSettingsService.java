package com.softwareag.c8y.push.commons.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cumulocity.model.option.OptionPK;
import com.cumulocity.rest.representation.tenant.OptionRepresentation;
import com.cumulocity.sdk.client.RestConnector;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.option.TenantOptionApi;
import com.softwareag.c8y.push.commons.exceptions.HubConnectionException;
import com.softwareag.c8y.push.commons.exceptions.ValidationException;
import com.softwareag.c8y.push.commons.model.AzureConfiguration;
import com.windowsazure.messaging.NotificationHubsException;

@Service
public class PushSettingsService {

	private enum OptionKey {
		HUB, CONNECTION
	}

	private static final String CATEGORY = "pushgateway.azure";

	@Value("${defaultsettings.connection}")
	private String defaultConnection;

	@Value("${defaultsettings.hub}")
	private String defaultHub;

	@Autowired
	private TenantOptionApi tenantOptions;

	@Autowired
	private RestConnector restConnector;

	@Autowired
	private ConnectionHubService connectionHubService;

	public AzureConfiguration getConfiguration() throws ValidationException {
		final AzureConfiguration configuration = new AzureConfiguration();
		configuration.setHub(getHubPath());
		configuration.setConnection(getConnectionString());
		return configuration;
	}

	public void updateConfiguration(final AzureConfiguration azureConfiguration) throws ValidationException {
		validate(azureConfiguration);
		verifyAzureConnection(azureConfiguration);
		updateOption(OptionKey.HUB, azureConfiguration.getHub());
		updateOption(OptionKey.CONNECTION, azureConfiguration.getConnection());
	}

	private void validate(final AzureConfiguration azureConfiguration) throws ValidationException {
		if (azureConfiguration.getHub() == null) {
			throw new ValidationException("Property 'hub' must not be null.");
		}
		if (azureConfiguration.getConnection() == null) {
			throw new ValidationException("Property 'connection' must not be null.");
		}
	}

	private void verifyAzureConnection(final AzureConfiguration azureConfiguration) throws HubConnectionException {
		try {
			connectionHubService.get(azureConfiguration).createRegistrationId();
		} catch (final NotificationHubsException e) {
			throw new HubConnectionException();
		}
	}

	private OptionRepresentation getOptionRepresentationByKey(final OptionKey key) {
		try {
			final OptionPK opk = new OptionPK(CATEGORY, key.name().toLowerCase());
			return tenantOptions.getOption(opk);
		} catch (final SDKException e) {
			return null;
		}
	}

	private Optional<String> getOptionByKey(final OptionKey key) {
		final OptionRepresentation option = getOptionRepresentationByKey(key);
		if (option == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(option.getValue());
	}

	private OptionRepresentation updateOption(final OptionKey key, final String value) {
		final OptionRepresentation optionRepresentation = new OptionRepresentation();
		optionRepresentation.setKey(key.name().toLowerCase());
		optionRepresentation.setCategory(CATEGORY);
		optionRepresentation.setValue(value);
		return tenantOptions.save(optionRepresentation);
	}

	public String currentTenant() {
		return restConnector.getPlatformParameters().getTenantId();
	}

	public String encodeSafeUserId(final String userId) {
		return userId.replace("@", "_at_");
	}

	public String decodeSafeUserId(final String userId) {
		return userId.replace("_at_", "@");
	}

	public String getConnectionString() {
		return getOptionByKey(OptionKey.CONNECTION).orElse(defaultConnection);
	}

	public String getHubPath() {
		return getOptionByKey(OptionKey.HUB).orElse(defaultHub);
	}
}
