package com.novacodestudios.recall.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SortingDataStore @Inject constructor(context: Context) {
    private val Context.sortingDataStore: DataStore<Preferences> by preferencesDataStore(name = "SORTING_KEY")
    private val datastore = context.sortingDataStore

    companion object {
        val sortingTypeKey = stringPreferencesKey("SORTING_TYPE_KEY")
    }

    suspend fun setSortingType(sortingType: String) {
        datastore.edit {
            it[sortingTypeKey] = sortingType
        }
    }

    fun getSortingType(): Flow<String?> = datastore.data.map {
        it[sortingTypeKey]
    }
}
