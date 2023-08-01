package com.cumulocity.alarmapp.util

import com.cumulocity.alarmapp.MyApplication
import com.cumulocity.client.api.*
import com.cumulocity.client.model.*
import com.cumulocity.client.supplementary.SeparatedQueryParameter
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Callback
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSession

class CumulocityAPI private constructor() {
    private lateinit var alarmsApi: AlarmsApi
    private lateinit var managedObjectsApi: ManagedObjectsApi
    private lateinit var externalIDsApi: ExternalIDsApi
    private lateinit var currentUserApi: CurrentUserApi
    private lateinit var registrationApi: RegistrationApi;

    private fun createHttpClientBuilder(): OkHttpClient.Builder {
        val client = createOkHttpClient()
        val interceptor = Interceptor { chain: Interceptor.Chain ->
            val authToken = Credentials.basic(
                LoginHolder.getInstance(MyApplication.getAppContext()).userID,
                LoginHolder.getInstance(MyApplication.getAppContext()).password
            )
            var request = chain.request()
            val headers = request.headers().newBuilder().add("Authorization", authToken).build()
            request = request.newBuilder().headers(headers).build()
            chain.proceed(request)
        }
        client.addInterceptor(interceptor)
        return client
    }

    private fun createOkHttpClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .readTimeout(1266666, TimeUnit.MILLISECONDS)
            .hostnameVerifier { hostname: String?, sslSession: SSLSession? ->
                HttpsURLConnection.getDefaultHostnameVerifier().verify(hostname, sslSession)
            }
    }

    fun getActiveAlarmCount(severity: SeparatedQueryParameter<String>, callback: Callback<Int?>) {
        alarmsApi.getNumberOfAlarms(
            severity = severity,
            status = SeparatedQueryParameter(arrayOf(Alarm.Status.ACTIVE.name))
        ).enqueue(callback)
    }

    fun getDevice(id: String, callback: Callback<ManagedObject?>) {
        managedObjectsApi.getManagedObject(id = id).enqueue(callback)
    }

    fun filterAlarmList(alarmModel: AlarmModel, callback: Callback<AlarmCollection?>) {
        alarmsApi.getAlarms(
            pageSize = 50,
            severity = SeparatedQueryParameter(alarmModel.severity.toTypedArray()),
            status = SeparatedQueryParameter(alarmModel.status.toTypedArray()),
            type = SeparatedQueryParameter(alarmModel.type),
            source = alarmModel.deviceID
        ).enqueue(callback)
    }

    fun filterAlarms(
        sourceID: String,
        status: SeparatedQueryParameter<String>,
        callback: Callback<AlarmCollection?>
    ) {
        alarmsApi.getAlarms(pageSize = 50, source = sourceID, status = status).enqueue(callback)
    }

    fun getExternalID(id: String, callback: Callback<ExternalIds?>) {
        externalIDsApi.getExternalIds(id).enqueue(callback)
    }

    fun filterDeviceName(query: String, callback: Callback<ManagedObjectCollection?>) {
        managedObjectsApi.getManagedObjects(query = query).enqueue(callback)
    }

    fun appendNameFilter(deviceName: String): String {
        return "\$filter=name eq $deviceName"
    }

    fun updateAlarm(alarm: Alarm, id: String, callback: Callback<Alarm?>) {
        alarmsApi.updateAlarm(body = alarm, id = id).enqueue(callback);
    }

    fun getUserInfo(callback: Callback<CurrentUser?>) {
        currentUserApi = CurrentUserApi.create(
            LoginHolder.getInstance(MyApplication.getAppContext()).tenant,
            createHttpClientBuilder()
        )
        currentUserApi.getCurrentUser().enqueue(callback);
    }

    fun subscribePushNotification(registration: Registration, callback: Callback<ResponseBody>) {
        registrationApi.subscribe(registration).enqueue(callback);
    }

    fun unSubscribePushNotification(deviceToken: String, callback: Callback<ResponseBody>) {
        registrationApi.unsubscribe(deviceToken = deviceToken).enqueue(callback);
    }

    fun getAlarm(alarmId: String, callback: Callback<Alarm?>) {
        alarmsApi.getAlarm(id = alarmId).enqueue(callback);
    }

    fun initializeAPIs() {
        val URL = LoginHolder.getInstance(MyApplication.getAppContext()).tenant;
        val httpClientBuilder = createHttpClientBuilder()
        alarmsApi = AlarmsApi.create(URL, httpClientBuilder)
        managedObjectsApi = ManagedObjectsApi.create(URL, httpClientBuilder)
        externalIDsApi = ExternalIDsApi.create(URL, httpClientBuilder)
        registrationApi = RegistrationApi.create(URL, httpClientBuilder)
    }

    companion object {
        var instance: CumulocityAPI? = null
            get() {
                if (field == null) {
                    field = CumulocityAPI()
                }
                return field
            }
            private set
    }
}