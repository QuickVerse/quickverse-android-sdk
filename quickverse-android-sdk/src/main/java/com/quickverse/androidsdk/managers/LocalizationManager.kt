package com.quickverse.androidsdk.managers
import com.quickverse.androidsdk.models.QuickVerseLocalization
import com.quickverse.networking.APIClient

class LocalizationManager(private val apiClient: APIClient) {

    var localizations = emptyList<QuickVerseLocalization>()

    fun getLocalizationsFor(languageCode: String, completion: (Boolean) -> Unit) {
        if (QuickVerse.isDebugEnabled) {
            LoggingManager.log("ℹ️ Retrieving localizations for language code: $languageCode")
        }
        apiClient.getLocalizationsFor(languageCode) { localizations, success ->
            localizations?.let { unwrappedLocalizations ->
                this.localizations = unwrappedLocalizations
            }
            completion(success)
        }
    }

    fun valueFor(key: String): String? {
        localizations.firstOrNull { it.key == key }?.target_text?.let { unwrappedLocalization ->
            return unwrappedLocalization
        }
        if (localizations.isEmpty()) {
            LoggingManager.log("🚨 WARN: No localizations have been received. Have you added at least one localization to your quickverse account? If yes, did your fetchLocalizations( request succeed?")
        } else {
            LoggingManager.log("🚨 WARN: Value not found for referenced key: $key. Please check this key exists in your quickverse.io account.")
        }
        return null
    }
}