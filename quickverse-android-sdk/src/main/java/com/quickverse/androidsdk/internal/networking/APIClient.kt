package com.quickverse.androidsdk.internal.networking

import android.provider.Settings
import com.quickverse.androidsdk.internal.managers.LoggingManager
import com.quickverse.androidsdk.internal.managers.MissingKey
import com.quickverse.androidsdk.QuickVerse
import com.quickverse.androidsdk.internal.managers.ReportBody
import com.quickverse.androidsdk.internal.managers.UtilisedKey
import com.quickverse.androidsdk.internal.models.QuickVerseLocalization
import com.quickverse.androidsdk.internal.models.QuickVerseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*

interface QuickVerseAPI {
    @Headers(
        "Content-Type: application/json",
        "Platform: Android",
        "X-QUICKVERSE-DEVICEID: ${Settings.Secure.ANDROID_ID}",
        "X_QUICKVERSE_VERSION: 1.5.5"
    )

    @GET("localisation/{languageCode}")
    fun getLocalizations(
        @Header("Authorization") tokenString: String,
        @Path("languageCode") languageCode: String
    ): Call<QuickVerseResponse>

    @POST("report")
    fun report(
        @Header("Authorization") tokenString: String,
        @Body body: ReportBody
    ): Call<Unit>
}

class APIClient(private val apiKey: String, private val packageName: String) {

    private val service: QuickVerseAPI
    init {
        val baseURL = "https://quickverse.io/sdk/api/"
        val retrofit = Retrofit
            .Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(QuickVerseAPI::class.java)
    }

    fun getLocalizationsFor(languageCode: String, completion: (List<QuickVerseLocalization>?, Boolean) -> Unit) {
        checkAuthCreds()

        if (QuickVerse.isDebugEnabled) {
            LoggingManager.log("ℹ️ Retrieving localizations for language code: $languageCode")
        }
        service.getLocalizations(tokenString = getToken64(), languageCode = languageCode)
            .enqueue(object :
                Callback<QuickVerseResponse> {
                override fun onResponse(
                    call: Call<QuickVerseResponse>,
                    response: Response<QuickVerseResponse>
                ) {
                    if (response.isSuccessful) {
                        completion(response.body()?.data?.localisations, true)
                    } else {
                        LoggingManager.log("ℹ️ Localization fetch unsuccessful")
                        completion(null, false)
                    }
                }
                override fun onFailure(call: Call<QuickVerseResponse>, t: Throwable) {
                    LoggingManager.log("ℹ️ Localization fetch unsuccessful")
                    completion(null, false)
                }
            })
    }

    fun report(missingKeys: List<MissingKey>, utilisedKeys: List<UtilisedKey>, completion: (Boolean) -> Unit) {
        checkAuthCreds()
        val body = ReportBody(missing_keys = missingKeys, utilised_keys = utilisedKeys)
        service.report(tokenString = getToken64(), body = body)
            .enqueue(object: Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    completion(response.isSuccessful)
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    completion(false)
                }
            })
    }

    private fun getToken64(): String {
        val base64Token = Base64.getEncoder().encodeToString("$packageName:$apiKey".toByteArray())
        return "Bearer $base64Token"
    }
    private fun checkAuthCreds() {
        if (apiKey.isEmpty()) {
            throw Exception("🚨 API Key not provided. Please configure the SDK on your app startup with your API key from https://quickverse.io.")
        }
        if (packageName.isEmpty()) {
            throw Exception("🚨 Package name not provided. Please configure the SDK on your app startup with your API key from https://quickverse.io.")
        }
    }
}