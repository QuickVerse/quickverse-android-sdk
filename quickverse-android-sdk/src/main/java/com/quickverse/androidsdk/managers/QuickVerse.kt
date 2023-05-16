package com.quickverse.androidsdk.managers

import com.quickverse.networking.APIClient
import java.util.*

/**
 * 1. Configure this object with both an API Key and your app's package name.
 * 2. Optionally set `isDebugEnabled` to true for detailed console logs.
 * 3. Call `getLocalizations(` at a time that works for your app. In most cases you'd want to do this during your launch sequence, before any copy is displayed.
 * 4. Call `stringFor(` to retrieve the localized string for the user's device language setting
 * 5. This object will hold the localized values for the duration of the session, and can be retrieved from anywhere in your app.
 */

object QuickVerse {
    private lateinit var reportingManager: ReportingManager
    private lateinit var localizationManager: LocalizationManager

    var isDebugEnabled: Boolean = false

    /**
     * Must be called before you can use the SDK.
     * We strongly recommend you call this on app initialisation.
     */
    fun configure(quickVerseAPIKey: String, appPackageName: String) {
        val apiClient = APIClient(apiKey = quickVerseAPIKey, packageName = appPackageName)
        reportingManager = ReportingManager(apiClient = apiClient)
        localizationManager = LocalizationManager(apiClient = apiClient)
    }

    /**
     * Use these methods to fetch the localizations you have created on quickverse.io.
     * You will typically want to call this on launch, before you display any copy.
     */
    // Option 1 (Recommended): fetches your quickverse localizations for the user's device language setting.
    fun getLocalizations(completion: (Boolean) -> Unit) {
        val languageCode = Locale.getDefault().language
        getLocalizationsFor(languageCode, completion)
    }
    // Option 2: fetches your quickverse localizations for a specific, two-letter ISO 639-1 language code.
    fun getSpecificLocalizations(languageCode: String, completion: (Boolean) -> Unit) {
        getLocalizationsFor(languageCode, completion)
    }

    /**
     * Use these methods to retrieve values for the localizations you fetched using one of the "get" methods above.
     * You can call these from anywhere in your app, e.g. Quickverse.stringFor(key: "Onboarding.Welcome.Title")
     */
    // Option 1: Returns the value for a specific key, or null if one does not exist
    fun stringFor(key: String): String? {
        val value = localizationManager.valueFor(key)
        logRequestedKey(key = key, defaultValue = null, wasPresent = value != null)
        return value
    }
    // Option 2: Returns the value for a specific key, falling back to a default value
    fun stringFor(key: String, defaultValue: String): String {
        val value = localizationManager.valueFor(key)
        logRequestedKey(key = key, defaultValue = defaultValue, wasPresent = value != null)
        return value ?: defaultValue
    }

    /**
     * Internal functionality
     */
    private fun getLocalizationsFor(languageCode: String, completion: (Boolean) -> Unit) {
        if (isDebugEnabled) {
            LoggingManager.log("ℹ️ Retrieving localizations for language code: $languageCode")
        }
        localizationManager.getLocalizationsFor(languageCode) { success ->
            if (success) {
                if (isDebugEnabled) {
                    if (localizationManager.localizations.isEmpty()) {
                        LoggingManager.log("🚨 WARN: Localizations empty. Please add at least one localization entry to your account on quickverse.io.")
                    } else {
                        LoggingManager.log("ℹ️ Retrieved ${localizationManager.localizations.count()} localizations for language code: $languageCode")
                    }
                }
            } else {
                LoggingManager.log("🚨 WARN: Localization fetch unsuccessful")
            }
            completion(success)
        }
    }

    private fun logRequestedKey(key: String, defaultValue: String?, wasPresent: Boolean) {
        if (wasPresent) {
            reportingManager.logUtilisedKey(key)
        } else {
            if (localizationManager.localizations.isEmpty()) {
                LoggingManager.log("🚨 WARN: No localizations have been received. Have you added at least one localization to your quickverse account? If yes, did your fetchLocaliZations request succeed?")
            } else {
                LoggingManager.log("🚨 WARN: Value not found for referenced key: $key. Please check this key exists in your quickverse.io account.")
            }
            reportingManager.logMissingKey(key, defaultValue = defaultValue)
        }
    }
}