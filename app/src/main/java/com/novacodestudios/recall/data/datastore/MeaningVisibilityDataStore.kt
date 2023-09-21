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

class MeaningVisibilityDataStore @Inject constructor(context: Context) {
    private val Context.visibility: DataStore<Preferences> by preferencesDataStore(name = "MEANING_VISIBILITY_KEY")
    private val datastore = context.visibility

    companion object {
        val visibilityKey = booleanPreferencesKey("MEANING_VISIBILITY_KEY")
    }

    suspend fun setMeaningVisibility(visibility: Boolean) {
        datastore.edit {
            it[visibilityKey] = visibility
        }
    }

    fun getMeaningVisibility(): Flow<Boolean?> = datastore.data.map {
        it[visibilityKey]
    }
}
