package com.wjaronski.bricklistapp

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.preference.RingtonePreference
import android.text.TextUtils
import android.view.MenuItem
import android.R.id.edit
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import com.wjaronski.bricklistapp.util.LanguageHelper
import java.util.*
import java.util.prefs.PreferenceChangeEvent


/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {
    lateinit var settingsActivity: SettingsActivity
    lateinit var languagePref_ID: String
    lateinit var context: Context
//    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
        context = applicationContext
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || GeneralPreferenceFragment::class.java.name == fragmentName
//                || DataSyncPreferenceFragment::class.java.name == fragmentName
//                || NotificationPreferenceFragment::class.java.name == fragmentName
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("default_url_preference"))
//            bindPreferenceSummaryToValue(findPreference(KEY_PREF_LANGUAGE))
            bindPreferenceSummaryToValue2(findPreference("show_archived"))

//            findPreference(KEY_PREF_LANGUAGE).onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
//
//            }

//            findPreference(KEY_PREF_LANGUAGE).setOnPreferenceChangeListener { preference, newValue ->
//
//                Locale.ENGLISH
//                when(newValue.toString()){
//                    "lan_pl_PL"->{changeLanguage("pl")}
//                    "lan_en_US"->{changeLanguage("en")}
//                    else->{}
//                }
//                true
//            }

//            bindPreferenceSummaryToValue(findPreference("language_preference"))
//            bindPreferenceSummaryToValue(findPreference("example_list"))
        }




        fun changeLanguage(lang: String){
            Log.e("LANG", ": $lang")
            LanguageHelper.changeLocale(this.resources, lang)
//            val locale = Locale(lang)
//            Locale.setDefault(locale)
//            val config = Configuration()
//            config.setLocale(locale)
//            restartActivity()
        }

        private fun restartActivity() {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class NotificationPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_notification)
            setHasOptionsMenu(true)

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"))
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }*/

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class DataSyncPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_data_sync)
            setHasOptionsMenu(true)

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"))
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }*/

    companion object {

        val KEY_PREF_LANGUAGE = "language_preference"



        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            Log.e("PREFERENCE", stringValue)
            if (preference is ListPreference) {

//                if(preference.getV is Boolean)
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                if(stringValue.equals("lan_pl_PL") or stringValue.equals("lan_en_US")){

                    Log.e("PREFERENCE","PREF" + stringValue+" "+ index)
                    val str = stringValue.replace("lan_", "")
                    LanguageHelper.changeLocale(MainActivity.res,str)
//                   LanguageHelper.changeLocale(this.resources, lang)
//                    val locale = Locale(str.toLowerCase())
//                    Locale.setDefault(locale)
//                    val config = Configuration()
//                    config.setLocale(locale)
//                    restartActivity()
////                     .getResources().updateConfiguration(config,


////                            getBaseContext().getResources().getDisplayMetrics());
//
////                    restartAc
                }
//                else if(stringValue.equals())

                // Set the summary to reflect the new value.
                preference.setSummary(
                        if (index >= 0)
                            listPreference.entries[index]
                        else
                            null)

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
//                if(stringValue.equals("false")){
//
//                }
//                if(stringValue)
//                if(stringValue.equals("lan_PL") or stringValue.equals("lan_EN")){
//                    Log.e("PREFERENCE","PREF" + stringValue)
//                    val str = stringValue.replace("lan_", "")
//                    val locale = Locale(str.toLowerCase())
//                    Locale.setDefault(locale)
//                    val config = Configuration()
////                    config.locale = locale
////                    config.setLocale(locale)
//
////                    getBaseContext().getResources().updateConfiguration(config,
////                            getBaseContext().getResources().getDisplayMetrics())
////                    settings.edit().putString("locale", "ar").commit()
////                    this.finish()
////                    val intent = Intent(this, MainActivity::class.java)
//////                    val refresh = Intent(this, MainActivity::class.java)
////                    startActivity(refresh)
//                }
            }
            true
        }



        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.

         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }
        private fun bindPreferenceSummaryToValue2(preference: Preference) {
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getBoolean(preference.key, false))
        }
    }
}
