// Copyright (c) 2014-2023 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
// Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.

package com.cumulocity.client.model
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Registration(var userId: String?, var bundleId: String?, var device: Device?) {
	constructor() : this(userId = null, bundleId = null, device = null)

	/**
	 * Additional information for this subscription.
	 */
	var tags: Array<String>? = null

	/**
	 * Device information.
	 */
	data class Device(var deviceName: String?, var deviceToken: String?, var platform: Platform?) {
		constructor() : this(deviceName = null, deviceToken = null, platform = null)
	
		enum class Platform(val value: String) {
			@SerializedName(value = "IOS")
			IOS("IOS"),
			@SerializedName(value = "Android")
			ANDROID("Android")
		}
	
	
		override fun toString(): String {
			return Gson().toJson(this).toString()
		}
	}

	override fun toString(): String {
		return Gson().toJson(this).toString()
	}
}
