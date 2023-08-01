// Copyright (c) 2014-2023 Software AG, Darmstadt, Germany and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or its affiliates and/or their licensors.
// Use, reproduction, transfer, publication or disclosure is prohibited except as specifically provided for in your License Agreement with Software AG.

package com.cumulocity.client.api

import com.cumulocity.client.model.Registration
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

/**
 * Endpoint that is used to register physical devices at the tenant.
 */
interface RegistrationApi {

    companion object Factory {
        fun create(baseUrl: String): RegistrationApi {
            return create(baseUrl, null)
        }

        fun create(baseUrl: String, clientBuilder: OkHttpClient.Builder?): RegistrationApi {
            val retrofitBuilder = retrofit().baseUrl(baseUrl)
            if (clientBuilder != null) {
                retrofitBuilder.client(clientBuilder.build())
            }
            return retrofitBuilder.build().create(RegistrationApi::class.java)
        }

        fun retrofit(): Retrofit.Builder {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
        }
    }

    /**
     * Registers a device at the tenant.
     *
     * "Creates a Azure registration within the configured notification hub. Needs to be performed once the device token is obtained from APNS or Firebase.Use this resource to also update an existing registration. E.g. to modify the tag list."
     *
     * ##### Response Codes
     *
     * The following table gives an overview of the possible response codes and their meanings:
     *
     * * HTTP 201 Successfully registered device.
     * * HTTP 400 Could not create Azure registration.
     * * HTTP 401 Authentication information is missing or invalid.
     *
     * @param body
     */
    @Headers(*["Content-Type:application/json", "Accept:application/json"])
    @POST("/service/pushgateway/registrations")
    fun subscribe(
        @Body body: Registration
    ): Call<ResponseBody>

    /**
     * Removes a registration given by it's device token.
     *
     * ##### Response Codes
     *
     * The following table gives an overview of the possible response codes and their meanings:
     *
     * * HTTP 201 The registration has been removed.
     * * HTTP 400 The registration could not been removed.
     * * HTTP 401 Authentication information is missing or invalid.
     *
     * @param deviceToken
     * A registered device token.
     */
    @Headers("Accept:application/json")
    @DELETE("/service/pushgateway/registrations/{deviceToken}")
    fun unsubscribe(
        @Path("deviceToken") deviceToken: String
    ): Call<ResponseBody>
}
