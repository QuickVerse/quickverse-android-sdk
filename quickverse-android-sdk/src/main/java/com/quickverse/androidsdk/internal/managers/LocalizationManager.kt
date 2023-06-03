package com.quickverse.androidsdk.internal.managers
import android.content.res.Resources
import com.quickverse.androidsdk.QVSubstitution
import com.quickverse.androidsdk.internal.models.QuickVerseLocalization
import com.quickverse.androidsdk.internal.networking.APIClient

class LocalizationManager(private val apiClient: APIClient) {

    var localizations = emptyList<QuickVerseLocalization>()
    var successfulFetch = false

    fun getLocalizationsFor(languageCode: String, completion: (Boolean) -> Unit) {
        var languageCodes = Resources.getSystem().configuration.locales.toLanguageTags()
        languageCodes = "$languageCode,".plus(languageCodes)

        apiClient.getLocalizationsFor(languageCodesJoined = languageCodes) { localizations, success ->
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