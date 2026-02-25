package com.tyzytyper
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "tyzytyper_settings")

object SettingsManager {
    val API_KEY = stringPreferencesKey("gemini_api_key")
    val GROK_API_KEY = stringPreferencesKey("grok_api_key")
    val GEMINI_MODEL = stringPreferencesKey("gemini_model")
    val GROK_MODEL = stringPreferencesKey("grok_model")
    val DEFAULT_PROVIDER = stringPreferencesKey("default_provider")
    
    val APP_THEME = intPreferencesKey("app_theme") // 0 = System, 1 = Light, 2 = Dark
    val CUSTOM_PROMPT = stringPreferencesKey("custom_prompt")
    val FLOATING_BUTTON_ENABLED = androidx.datastore.preferences.core.booleanPreferencesKey("floating_button_enabled")

    suspend fun saveApiKey(context: Context, key: String) { context.dataStore.edit { it[API_KEY] = key } }
    fun getApiKey(context: Context): Flow<String?> = context.dataStore.data.map { it[API_KEY] }

    suspend fun saveGrokApiKey(context: Context, key: String) { context.dataStore.edit { it[GROK_API_KEY] = key } }
    fun getGrokApiKey(context: Context): Flow<String?> = context.dataStore.data.map { it[GROK_API_KEY] }

    suspend fun saveGeminiModel(context: Context, model: String) { context.dataStore.edit { it[GEMINI_MODEL] = model } }
    fun getGeminiModel(context: Context): Flow<String> = context.dataStore.data.map { it[GEMINI_MODEL] ?: "gemini-2.5-flash" }

    suspend fun saveGrokModel(context: Context, model: String) { context.dataStore.edit { it[GROK_MODEL] = model } }
    fun getGrokModel(context: Context): Flow<String> = context.dataStore.data.map { it[GROK_MODEL] ?: "grok-4-1-fast-non-reasoning" }

    suspend fun saveDefaultProvider(context: Context, provider: String) { context.dataStore.edit { it[DEFAULT_PROVIDER] = provider } }
    fun getDefaultProvider(context: Context): Flow<String> = context.dataStore.data.map { it[DEFAULT_PROVIDER] ?: "grok" }

    suspend fun saveThemeOption(context: Context, themeOption: Int) { context.dataStore.edit { it[APP_THEME] = themeOption } }
    fun getThemeOption(context: Context): Flow<Int> = context.dataStore.data.map { it[APP_THEME] ?: 0 }

    suspend fun saveCustomPrompt(context: Context, prompt: String) { context.dataStore.edit { it[CUSTOM_PROMPT] = prompt } }
    fun getCustomPrompt(context: Context): Flow<String> = context.dataStore.data.map { 
        it[CUSTOM_PROMPT] ?: "Rewrite to fix grammar and improve clarity. Please only return the fixed text and nothing else:" 
    }

    suspend fun saveFloatingButtonEnabled(context: Context, isEnabled: Boolean) { context.dataStore.edit { it[FLOATING_BUTTON_ENABLED] = isEnabled } }
    fun isFloatingButtonEnabled(context: Context): Flow<Boolean> = context.dataStore.data.map { it[FLOATING_BUTTON_ENABLED] ?: false }
}
