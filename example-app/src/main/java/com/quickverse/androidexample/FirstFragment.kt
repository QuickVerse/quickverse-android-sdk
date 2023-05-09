package com.quickverse.androidexample

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickverse.sdk.manager.QuickVerse
import com.quickverse.androidexample.databinding.FragmentFirstBinding
import java.util.*

object QuickVerseKey {
    const val onboardingDemoTitle = "Onboarding.Demo.Title"
}

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val demoLanguages = arrayOf("es", "it", "fr", "de", "en")
    private var demoLanguageIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        /**
         * 1. Create an account at https://quickverse.io, and add this demo application.
         *      The package name of this demo app (if you don't change it) is: com.quickverse.androidexample.
         * 2. Update the "configure" call below with your quickverse API Key.
         */
        QuickVerse.configure(quickVerseAPIKey = "", appPackageName = "com.quickverse.androidexample")
        QuickVerse.isDebugModeEnabled = true
        getDeviceLanguageLocalizations()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.systemLanguageButton.setOnClickListener {
            getDeviceLanguageLocalizations()
        }
        binding.changeSystemLanguageButton.setOnClickListener {
            changeDeviceLanguage()
        }
        binding.cycleDemoLanguagesButton.setOnClickListener {
            getDemoLanguageLocalizations()
        }
    }

    private fun getDeviceLanguageLocalizations() {
        // This request would normally be in your launch sequence (before you need to display any copy), and only once per session
        QuickVerse.getLocalizations() { success ->
            if (success) {
                binding.resultSourceTextview.text = "Using: System Language (${Locale.getDefault().language})"

                // Strongly Recommended - Use a centrally-declared keys file
                binding.onboardingTitleTextview.text = QuickVerse.stringFor(QuickVerseKey.onboardingDemoTitle)
                // Alternatively, keys can be hardcoded "inline"
                binding.onboardingBodyTextview.text = QuickVerse.stringFor("Onboarding.Demo.Body")
            } else {
                // Handle error
                println("Error")
            }
        }
    }

    private fun getDemoLanguageLocalizations() {
        val demoLanguageCode = demoLanguages[demoLanguageIndex]
        if (demoLanguageIndex == demoLanguages.count() - 1) {
            demoLanguageIndex = 0
        } else {
            demoLanguageIndex++
        }
        // This request would normally be in your launch sequence (before you need to display any copy), and only once per session
        QuickVerse.getSpecificLocalizations(languageCode = demoLanguageCode) { success ->
            if (success) {
                binding.resultSourceTextview.text = "Using: Demo Language ($demoLanguageCode)"

                // Strongly Recommended - Use a centrally-declared keys file
                binding.onboardingTitleTextview.text = QuickVerse.stringFor(QuickVerseKey.onboardingDemoTitle)
                // Alternatively, keys can be hardcoded "inline"
                binding.onboardingBodyTextview.text = QuickVerse.stringFor("Onboarding.Demo.Body")
            } else {
                // Handle error
            }
        }
    }

    private fun changeDeviceLanguage() {
        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}