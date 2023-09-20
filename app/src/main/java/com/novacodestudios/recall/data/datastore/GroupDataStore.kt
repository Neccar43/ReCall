package com.novacodestudios.recall.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroupDataStore @Inject constructor(context: Context) {
    private val Context.groupDataStore: DataStore<Preferences> by preferencesDataStore(name = "GROUP_ID_KEY")
    private val datastore = context.groupDataStore

    companion object {
        val groupIdKey = intPreferencesKey("GROUP_ID_KEY")
    }

    suspend fun setGroupId(groupId: Int) {
        datastore.edit {
            it[groupIdKey] = groupId
        }
    }

    fun getGroupId(): Flow<Int?> = datastore.data.map {
        it[groupIdKey]
    }
}
