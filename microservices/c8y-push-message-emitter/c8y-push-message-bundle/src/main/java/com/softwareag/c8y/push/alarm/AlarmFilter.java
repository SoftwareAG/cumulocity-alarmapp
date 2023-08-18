// Copyright (c) 2014-2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
// Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
package com.softwareag.c8y.push.alarm;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cumulocity.rest.representation.alarm.AlarmRepresentation;

@Service
public class AlarmFilter implements Predicate<AlarmRepresentation> {

	@Value("${filter.general.type}")
	private Optional<String> typeFilter;

	@Value("${filter.general.severity}")
	private Optional<String> severityFilter;

	@Value("${filter.general.status}")
	private Optional<String> statusFilter;

	@Override
	public boolean test(final AlarmRepresentation alarm) {
		boolean acceptedType = true;
		if (typeFilter.isPresent() && !StringUtils.isEmpty(typeFilter.get())) {
			acceptedType = Arrays.stream(typeFilter.get().split(","))
					.anyMatch(type -> alarm.getType().equalsIgnoreCase(type));
		}
		boolean acceptedSeverity = true;
		if (severityFilter.isPresent() && !StringUtils.isEmpty(typeFilter.get())) {
			acceptedSeverity = Arrays.stream(severityFilter.get().split(","))
					.anyMatch(type -> alarm.getSeverity().equalsIgnoreCase(type));
		}
		boolean acceptedStatus = true;
		if (statusFilter.isPresent() && !StringUtils.isEmpty(typeFilter.get())) {
			acceptedStatus = Arrays.stream(statusFilter.get().split(","))
					.anyMatch(type -> alarm.getStatus().equalsIgnoreCase(type));
		}
		return acceptedType && acceptedSeverity && acceptedStatus;
	}

	@Override
	public String toString() {
		return "[typeFilter=" + typeFilter + ", severityFilter=" + severityFilter + ", statusFilter=" + statusFilter
				+ "]";
	}

}
