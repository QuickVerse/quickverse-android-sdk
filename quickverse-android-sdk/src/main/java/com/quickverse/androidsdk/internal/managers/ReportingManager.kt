package com.quickverse.androidsdk.internal.managers
import com.quickverse.androidsdk.internal.networking.APIClient

data class UtilisedKey(val key: String, val usage_count: Int)
data class MissingKey(val key: String, val default_value: String)
data class ReportBody(val missing_keys: List<MissingKey>, val utilised_keys: List<UtilisedKey>)

class ReportingManager(private val apiClient: APIClient) {
    private var utilisedKeys = mutableListOf<UtilisedKey>()
    private var missingKeys = mutableListOf<MissingKey>()

    private val keyLimit = 4
    private fun needsTransmission(): Boolean {
        if (missingKeys.isNotEmpty()) {
            return true
        } else {
            val utilisedCount = utilisedKeys.sumOf { utilisedKey -> utilisedKey.usage_count }
            return (missingKeys.count() + utilisedCount) >= keyLimit
        }
    }
    private var requestInFlight: Boolean = false

    fun logUtilisedKey(key: String) {
        val existingCount = utilisedKeys.firstOrNull { it.key == key }?.usage_count ?: 0
        val newCount = existingCount + 1
        utilisedKeys.removeAll { it.key == key }
        utilisedKeys.add(UtilisedKey(key = key, usage_count = newCount))
        uploadKeyDataIfNecessary()
    }
    fun logMissingKey(key: String, defaultValue: String?) {
        missingKeys.removeAll { it.key == key }
        missingKeys.add(MissingKey(key = key, default_value = defaultValue ?: ""))
        uploadKeyDataIfNecessary()
    }

    private fun uploadKeyDataIfNecessary() {
        if (!needsTransmission() || requestInFlight) { return }

        requestInFlight = true

        apiClient.report(missingKeys = missingKeys, utilisedKeys = utilisedKeys) { success ->
            if (success) {
                missingKeys.clear()
                utilisedKeys.clear()
            } else {
                // Request failed, do not clear keys, retry on next
            }
            requestInFlight = false
        }
    }
}