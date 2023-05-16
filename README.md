[![Version](https://img.shields.io/maven-central/v/io.quickverse.androidsdk/quickverse)](https://central.sonatype.com/artifact/io.quickverse.androidsdk/quickverse/)
[![Twitter](https://img.shields.io/twitter/follow/quickverse_io?style=social)](https://twitter.com/quickverse.io)

# QuickVerse Android SDK

QuickVerse makes mobile & web app localization a breeze. Migrate your web and mobile localizations to QuickVerse Cloud, and start managing them anytime, anywhere, collaboratively.

1. [Installation](#installation)
2. [Usage](#usage)
    1. [Import the SDK](#1-import-the-sdk)
    2. [Configure the SDK](#2-configure-the-sdk-on-app-launch)
    3. [Retrieve your QuickVerse localizations](#3-retrieve-your-quickverseio-localizations)
    4. [Access your localizations](#4-access-your-localized-strings)
3. [Logging & Troubleshooting](#Logging-&-Troubleshooting)
5. [FAQs](#faqs)
6. [Support](#support)

## Installation

Add the following dependency to your _module-level_ `build.gradle` file:

```
dependencies {
    implementation 'io.quickverse.androidsdk:quickverse:1.5.2'
}
```

## Usage

QuickVerse is a very lightweight integration, requiring just a few lines of code.

### 1. Import the SDK

```kotlin
import com.quickverse.androidsdk.manager.QuickVerse
```

### 2. Configure the SDK on app launch

You'll need your APIKey, retrievable from your QuickVerse account [here](https://quickverse.io/project/default/applications).
```kotlin
QuickVerse.configure(quickVerseAPIKey = "{your-api-key}", appPackageName = "{your-package-name}")
QuickVerse.isDebugEnabled = true // Optionally get detailed console logs
```

### 3. Retrieve your QuickVerse.io localizations

In most cases, you'll want to download the localizations during your launch sequence - before any copy is shown to the user.

```kotlin
QuickVerse.getLocalizations() { success ->
    // Continue into app or handle failure         
}
```
_Note_: Keep an eye on the console. If you enable `isDebugEnabled`, the QuickVerse SDK will print out all available keys from your [quickverse.io](https://quickverse.io/project/default/localisations) account.

### 4. Access your localized strings

```kotlin
text = QuickVerse.stringFor("Onboarding.Demo.Body")
```

Optionally provide a default value, should the key not exist in the local store.
```kotlin
text = QuickVerse.stringFor("Onboarding.Demo.Body", "Welcome to QuickVerse")
```

**_Recommended_**: Although you _can_ access the keys "inline", as showed above, we strongly recommend you store your keys in a single file for easy maintenance, e.g:
```kotlin
object QVKey {
    const val onboardingDemoTitle = "Onboarding.Demo.Title"
}
```

You can then access your localized strings without hardcoding keys:
```kotlin
text = QuickVerse.stringFor(QVKey.onboardingDemoTitle)
```

## Logging & Troubleshooting

All QuickVerse console logs start with "QuickVerse: " for easy filtering. We recommend setting `QuickVerse.isDebugEnabled = true` during setup, and any time you're adding new keys.

### Informational Logs:
- "Retrieving localizations for language code" - informs you which language localizations will be fetched for. Useful for testing.

### Troubleshooting Logs:
- "API Key not configured" - have you called `QuickVerse.configure(apiKey: "{your-api-key}")` on app launch, before you try to fetch localizations?
- "API Key incorrect" - have you added your application to [quickverse.io](https://quickverse.io/project/default/applications), and are you using the correct APIKey for the current Bundle ID? QuickVerse APIkeys are specific to bundle IDs.
- "No response received" / "Localizations empty" - have you added at least one localization to your [quickverse.io](https://quickverse.io/project/default/localisations) account?

Missing logs? Make sure you're setting `QuickVerse.isDebugEnabled = true` when you configure the SDK.

## FAQs

1. How big is your SDK? The final binary size of the QuickVerse iOS SDK is very small, just 0.01MB, or 10kb!
2. How does your SDK handle limited connectivity? Our SDK requests all localizations on launch and caches them, so if there's limited connectivity later in the session, the localizations will still work.
3. What kind of data does the SDK collect and how is it used? The only data our SDK transmits off the device is: language keys for translations, SDK version, and device type.
4. Can we request changes? Absolutely! Please share your requests with us at team@quickverse.io and we'll review and get back to you.

## Support

Got a query or need support? We're always on hand to help with your integration & beyond. Just ping us at team@quickverse.io and we'll get back to you within your QuickVerse plan's SLA.
