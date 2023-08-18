// Copyright (c) 2014-2021 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
// Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.
package com.softwareag.c8y.push.commons.exceptions;

public class HubConnectionException extends ValidationException {

	private static final long serialVersionUID = 413135343201078249L;

	public HubConnectionException() {
		super("Connection to Azure notification hub failed.");
	}
	
	public HubConnectionException(String msg) {
		super(msg);
	}
}
