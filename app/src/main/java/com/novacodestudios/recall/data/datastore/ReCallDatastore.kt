package com.novacodestudios.recall.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReCallDatastore @Inject constructor( context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "THEME_KEY")
    private val datastore = context.dataStore

    companion object{
        val darkModeKey= booleanPreferencesKey("DARK_MODE_KEY")
    }

    suspend fun setTheme(isDarkMode:Boolean){
        datastore.edit {
            it[darkModeKey] = isDarkMode
        }
    }

    fun getTheme():Flow<Boolean> = datastore.data.map {
        val uiMode=it[darkModeKey] ?: false
        uiMode
    }

}