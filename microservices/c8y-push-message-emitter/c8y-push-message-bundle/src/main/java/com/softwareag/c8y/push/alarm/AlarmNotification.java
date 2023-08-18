package com.softwareag.c8y.push.alarm;

public class AlarmNotification {

	private Object data;

	private String realtimeAction;

	public Object getData() {
		return data;
	}

	public void setData(final Object data) {
		this.data = data;
	}

	public String getRealtimeAction() {
		return realtimeAction;
	}

	public void setRealtimeAction(final String realtimeAction) {
		this.realtimeAction = realtimeAction;
	}
}