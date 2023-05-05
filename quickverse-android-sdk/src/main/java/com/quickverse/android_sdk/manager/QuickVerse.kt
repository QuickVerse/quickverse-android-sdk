package com.quickverse.android_sdk.manager

import com.quickverse.android_sdk.logging.QuickVerseLogger
import com.quickverse.android_sdk.models.QuickVerseLocalization
import com.quickverse.android_sdk.models.QuickVerseResponse
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
        "X_QUICKVERSE_VERSION: 1.0.0"
    )
    @GET("localisation/{languageCode}")
    fun getLocalizations(
        @Header("Authorization") tokenString: String,
        @Path("languageCode") languageCode: String
    ): Call<QuickVerseResponse>
}

/**
 * 1. Configure this object with both an API Key and your app's package name.
 * 2. Optionally set `isDebugModeEnabled` to true for detailed console logs.
 * 3. Call `getLocalizations(` at a time that works for your app. In most cases you'd want to do this during your launch sequence, before any copy is displayed.
 * 4. Call `stringFor(` to retrieve the localized string for the user's device language setting
 * 5. This object will hold the localized values for the duration of the session, and can be retrieved from anywhere in your app.
 */

object QuickVerse {

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

    private var localizations = emptyList<QuickVerseLocalization>()
    var isDebugModeEnabled: Boolean = false

    private var apiKey = String()
    private var packageName = String()
    /**
     * Must be called before you can use the SDK.
     * We strongly recommend you call this on app initialisation.
     */
    fun configure(quickVerseAPIKey: String, appPackageName: String) {
        apiKey = quickVerseAPIKey
        packageName = appPackageName
    }

    /**
     * Use these methods to fetch the localizations you have created on quickverse.io.
     * You will typically want to call this on launch, before you display any copy.
     */
    // Option 1 (Recommended): fetches your quickverse localizations for the user's device language setting.
    // Unless you have a very specific use case, this is the method you'll want you use.
    fun getLocalizations(completion: (Boolean) -> Unit) {
        val languageCode = Locale.getDefault().language
        getLocalizationsFor(languageCode, completion)
    }
    // Option 2: fetches your quickverse localizations for a specific language code you provide.
    // This must be a two-letter ISO 639-1 code: https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
    fun getSpecificLocalizations(languageCode: String, completion: (Boolean) -> Unit) {
        getLocalizationsFor(languageCode, completion)
    }

    /**
     * Use these methods to retrieve values for the localizations you fetched using one of the "get" methods above.
     * You can call these from anywhere in your app, e.g. Quickverse.stringFor(key: "Onboarding.Welcome.Title")
     */
    // Option 1: Returns the value for a specific key, or null if one does not exist
    fun stringFor(key: String): String? {
        return valueFor(key)
    }
    // Option 2: Returns the value for a specific key, falling back to a default value
    fun stringFor(key: String, defaultValue: String): String {
        return valueFor(key) ?: defaultValue
    }

    /**
     * Internal functionality
     */
    private fun getLocalizationsFor(languageCode: String, completion: (Boolean) -> Unit) {
        if (apiKey.isEmpty()) {
            throw Exception("🚨 API Key not provided. Please configure the SDK on your app startup (usually AppDelegate) with your API key from https://quickverse.io.")
        }
        if (packageName.isEmpty()) {
            throw Exception("🚨 Package name not provided. Please configure the SDK on your app startup (usually AppDelegate) with your API key from https://quickverse.io.")
        }
        val base64Token = Base64.getEncoder().encodeToString("$packageName:$apiKey".toByteArray())
        val token64 = "Bearer $base64Token"
        if (isDebugModeEnabled) {
            QuickVerseLogger.logStatement("ℹ️ Retrieving localizations for language code: $languageCode")
        }
        service.getLocalizations(tokenString = token64, languageCode = languageCode).enqueue(object : Callback<QuickVerseResponse> {
            override fun onResponse(call: Call<QuickVerseResponse>, response: Response<QuickVerseResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.localisations?.let { unwrappedLocalizations ->
                        if (isDebugModeEnabled) {
                            if (unwrappedLocalizations.isEmpty()) {
                                QuickVerseLogger.logStatement("🚨 WARN: Localizations empty. Please add at least one localization entry to your account on quickverse.io.")
                            } else {
                                QuickVerseLogger.logStatement("ℹ️ Retrieved ${unwrappedLocalizations.count()} localizations for language code: $languageCode")
                            }
                        }
                        localizations = unwrappedLocalizations
                    }
                    if (localizations.isEmpty() && isDebugModeEnabled) {
                        QuickVerseLogger.logStatement("🚨 WARN: Localizations empty. Please add at least one localization entry to your account on quickverse.io.")
                    }
                    completion(true)
                } else {
                    completion(false)
                }
            }
            override fun onFailure(call: Call<QuickVerseResponse>, t: Throwable) {
                completion(false)
            }
        })
    }
    private fun valueFor(key: String): String? {
        localizations.firstOrNull() { it.key == key }?.target_text?.let { unwrappedLocalization ->
            return unwrappedLocalization
        }
        if (localizations.isEmpty()) {
            QuickVerseLogger.logStatement("🚨 WARN: No localizations have been received. Have you added at least one localization to your quickverse account? If yes, did your fetchLocaliZations request succeed?")
        } else {
            QuickVerseLogger.logStatement("🚨 WARN: Value not found for referenced key: $key. Please check this key exists in your quickverse.io account.")
        }
        return null
    }
}