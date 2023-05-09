[![Version](https://img.shields.io/cocoapods/v/quickverse?style=flat)](https://cocoapods.org/pods/quickverse-ios-sdk)
[![Twitter](https://img.shields.io/twitter/follow/quickverse_io?style=social)](https://twitter.com/quickverse.io)

# QuickVerse Android SDK

QuickVerse makes mobile & web app localization a breeze. Migrate your web and mobile localizations to QuickVerse Cloud, and start managing them anytime, anywhere, collaboratively.

1. [Installation](#installation)
2. [Usage](#usage)
3. [Logging & Troubleshooting](#Logging-&-Troubleshooting)
5. [FAQs](#faqs)
6. [Support](#support)

## Installation

Add the following dependency to your module-level `build.gradle` file:

```
dependencies {
    implementation 'com.quickverse.androidsdk:1.3.2'
}
```

## Usage

QuickVerse is a very lightweight integration, requiring just a few lines of code.

1. In files where you wish to use QuickVerse localizations, import the QuickVerse SDK you just installed.
```Kotlin
import com.quickverse.android_sdk.manager.QuickVerse
```
2. Configure the QuickVerse SDK on launch. To do this, you need your APIKey, retrievable from your QuickVerse account [here](https://quickverse.io/project/default/applications).
```Kotlin
QuickVerse.configure(quickVerseAPIKey = "{your-api-key}", appPackageName = "{your-package-name}")
QuickVerse.isDebugModeEnabled = true // Optionally get detailed console logs
```

3. Download the localizations, typically during your launch sequence.
```Kotlin
QuickVerse.getLocalizations() { success ->
    // Continue into app            
}
```
_Note_: Keep an eye on the console. If you enable `isDebugEnabled`, the QuickVerse SDK will print out all available keys from your [quickverse.io](https://quickverse.io/project/default/localisations) account.

4. Access your localized strings - from anywhere in your app.
```Kotlin
text = QuickVerse.stringFor("Onboarding.Demo.Body")
```

Optionally provide a default value, should the key not exist in the local store.
```Kotlin
text = QuickVerse.stringFor("Onboarding.Demo.Body", "Welcome to QuickVerse")
```

**_Recommended_**: Although you _can_ access the keys "inline", as showed above, we strongly recommend you store your keys in a single file for easy maintenance, e.g:
```Kotlin
object QuickVerseKey {
    const val onboardingDemoTitle = "Onboarding.Demo.Title"
}
```

You can then access your localized strings without hardcoding keys:
```Kotlin
text = QuickVerse.stringFor(QuickVerseKey.onboardingDemoTitle)
```

## Logging & Troubleshooting

All QuickVerse console logs start with "QuickVerse: " for easy filtering. We recommend setting `QuickVerse.shared.isDebugEnabled = true` during setup, and any time you're adding new keys.

### Informational Logs:
- "START AVAILABLE LOCALIZATION KEYS" - logs an auto-generated struct of available keys for you to copy into your application.
- "Retrieving localizations for language code" - informs you which language localizations will be fetched for. Useful for testing.

### Troubleshooting Logs:
- "API Key not configured" - have you called `QuickVerse.shared.configure(apiKey: "{your-api-key}")` on app launch, before you try to fetch localizations?
- "API Key incorrect" - have you added your application to [quickverse.io](https://quickverse.io/project/default/applications), and are you using the correct APIKey for the current Bundle ID? QuickVerse APIkeys are specific to bundle IDs.
- "No response received" / "Localizations empty" - have you added at least one localization to your [quickverse.io](https://quickverse.io/project/default/localisations) account?

Missing logs? Make sure you're setting `QuickVerse.isDebugEnabled = true` when you configure the SDK.

## FAQs

1. How big is your SDK? The final binary size of the QuickVerse iOS SDK is very small, just 0.2MB.
2. How does your SDK handle limited connectivity? Our SDK requests all localizations on launch and caches them, so if there's limited connectivity later in the session, the localisations will still work.
3. What kind of data does the SDK collect and how is it used? The only data our SDK transmits off the device is: language keys for translations, SDK version, and device type.
4. Can we request changes? Absolutely! Please share your requests with us at team@quickverse.io and we'll review and get back to you.

## Support

Got a query or need support? We're always on hand to help with your integration & beyond. Just ping us at team@quickverse.io and we'll get back to you within the SLAs timeline associated with your QuickVerse plan.
