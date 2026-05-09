package com.prunance.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Single DataStore instance per app
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * User profile and app-level preferences persisted via DataStore.
 */
class UserPreferences(private val context: Context) {

    companion object {
        val KEY_NAME = stringPreferencesKey("user_name")
        val KEY_MONTHLY_INCOME = doublePreferencesKey("monthly_income")
        val KEY_PAYDAY = intPreferencesKey("payday")
        val KEY_CURRENCY = stringPreferencesKey("currency")
        val KEY_LOW_BALANCE_THRESHOLD = doublePreferencesKey("low_balance_threshold")
        val KEY_PRIVACY_MODE = booleanPreferencesKey("privacy_mode")
        val KEY_PRIVACY_LOCK = stringPreferencesKey("privacy_lock")
        val KEY_HAS_SEEN_TOUR = booleanPreferencesKey("has_seen_tour")
        val KEY_HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    }

    // ── Read flows ────────────────────────────────────────────────────

    val userName: Flow<String> = context.dataStore.data.map { it[KEY_NAME] ?: "" }
    val monthlyIncome: Flow<Double> = context.dataStore.data.map { it[KEY_MONTHLY_INCOME] ?: 0.0 }
    val payday: Flow<Int> = context.dataStore.data.map { it[KEY_PAYDAY] ?: 25 }
    val currency: Flow<String> = context.dataStore.data.map { it[KEY_CURRENCY] ?: "NGN" }
    val lowBalanceThreshold: Flow<Double> = context.dataStore.data.map { it[KEY_LOW_BALANCE_THRESHOLD] ?: 5000.0 }
    val privacyMode: Flow<Boolean> = context.dataStore.data.map { it[KEY_PRIVACY_MODE] ?: false }
    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data.map { it[KEY_HAS_COMPLETED_ONBOARDING] ?: false }

    // ── Write operations ──────────────────────────────────────────────

    suspend fun saveProfile(
        name: String,
        monthlyIncome: Double,
        payday: Int,
        currency: String,
        lowBalanceThreshold: Double
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NAME] = name
            prefs[KEY_MONTHLY_INCOME] = monthlyIncome
            prefs[KEY_PAYDAY] = payday
            prefs[KEY_CURRENCY] = currency
            prefs[KEY_LOW_BALANCE_THRESHOLD] = lowBalanceThreshold
            prefs[KEY_HAS_COMPLETED_ONBOARDING] = true
        }
    }

    suspend fun setPrivacyMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_PRIVACY_MODE] = enabled }
    }

    suspend fun setPrivacyLock(pin: String?) {
        context.dataStore.edit {
            if (pin != null) it[KEY_PRIVACY_LOCK] = pin
            else it.remove(KEY_PRIVACY_LOCK)
        }
    }
}
