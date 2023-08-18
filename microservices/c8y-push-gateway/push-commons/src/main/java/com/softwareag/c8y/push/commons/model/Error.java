// Copyright (c) 2014-2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
// Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
package com.softwareag.c8y.push.commons.model;

public class Error {

	private final String message;

	public Error(final String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
