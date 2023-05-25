package com.quickverse.androidsdk.internal.managers
import com.quickverse.androidsdk.QVSubstitution
import com.quickverse.androidsdk.QuickVerse
import com.quickverse.androidsdk.internal.models.QuickVerseLocalization
import com.quickverse.androidsdk.internal.networking.APIClient

class LocalizationManager(private val apiClient: APIClient) {

    var localizations = emptyList<QuickVerseLocalization>()
    var successfulFetch = false

    fun getLocalizationsFor(languageCode: String, completion: (Boolean) -> Unit) {
        if (QuickVerse.isDebugEnabled) {
            LoggingManager.log("ℹ️ Retrieving localizations for language code: $languageCode")
        }
        apiClient.getLocalizationsFor(languageCode) { localizations, success ->
            localizations?.let { unwrappedLocalizations ->
                this.localizations = unwrappedLocalizations
            }
            if (success) {
                successfulFetch = true
            }
            completion(success)
        }
    }

    fun valueFor(key: String, substitutions: List<QVSubstitution>? = null): String? {
        var target = localizations.firstOrNull { it.key == key }?.target_text
        substitutions?.let { subs ->
            subs.forEach { it
                target = target?.replace(it.replace, it.with)
            }
        }
        return target
    }
}