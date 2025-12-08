package com.surtiapp.surtimovil.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DS_NAME = "app_prefs_ds"
val Context.dataStore by preferencesDataStore(DS_NAME)

class DataStoreManager(private val context: Context) {

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_ROLE = stringPreferencesKey("user_role")
    }

    /* ============= ONBOARDING ============= */
    val onboardingDoneFlow: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[Keys.ONBOARDING_DONE] ?: false }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_DONE] = done
        }
    }

    /* ============= TOKEN ============= */
    val authTokenFlow: Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[Keys.AUTH_TOKEN] }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.AUTH_TOKEN] = token
        }
    }

    /* ============= ROLE ============= */
    val userRoleFlow: Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[Keys.USER_ROLE] }

    suspend fun saveUserRole(role: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ROLE] = role
        }
    }
}
