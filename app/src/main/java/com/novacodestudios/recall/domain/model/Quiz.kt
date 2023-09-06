package com.novacodestudios.recall.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.novacodestudios.recall.util.currentISOLocaleDateTimeString

@Entity
data class Quiz(
    @PrimaryKey(autoGenerate = false) override val id: Int,
    val date: String = currentISOLocaleDateTimeString(),
    val isCompleted: Boolean = false,
    override val version: Long = 0L
) :Synchronizable{
    fun toMap(): Map<String, Any> {
        return mapOf(
            "date" to date,
            "isCompleted" to isCompleted,
            "version" to version
        )
    }
}

fun QueryDocumentSnapshot.toQuiz(): Quiz {
    return Quiz(
        id = id.toInt(),
        date = get("date") as String,
        isCompleted = get("isCompleted") as Boolean,
        version = get("version") as Long
    )
}