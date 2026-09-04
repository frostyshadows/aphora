package com.sherryyuan.aphora.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.sherryyuan.aphora.BuildConfig
import com.sherryyuan.aphora.PREFS_NAME
import androidx.core.content.edit

private const val PREF_VERSION_CODE_KEY = "version_code"
private const val NOT_FOUND = -1

/**
 * Code referenced from https://stackoverflow.com/questions/7217578/check-if-application-is-on-its-first-run
 */
fun isFirstInstall(context: Context): Boolean {
    val sharedPrefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    return sharedPrefs.getInt(PREF_VERSION_CODE_KEY, NOT_FOUND) == NOT_FOUND
}

/**
 * Should be called only after first-install setup (e.g. seeding default data) has finished.
 */
fun markFirstInstallComplete(context: Context) {
    val sharedPrefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    sharedPrefs.edit {
        putInt(PREF_VERSION_CODE_KEY, BuildConfig.VERSION_CODE)
    }
}
