// Copyright (c) 2014-2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
// Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
package com.softwareag.c8y.push.commons.model;

public class AzureConfiguration {

	private String hub;

	private String connection;

	public String getHub() {
		return hub;
	}

	public void setHub(final String hub) {
		this.hub = hub;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(final String connection) {
		this.connection = connection;
	}
}
