package com.novacodestudios.recall.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.novacodestudios.recall.util.currentISOLocaleDateTimeString
import kotlin.random.Random

@Entity
data class Group(
    @PrimaryKey(autoGenerate = false)
    override val id: Int= Random.nextInt(Int.MAX_VALUE),
    override val version: Long=0L,
    val groupName:String,
    val creationDate:String= currentISOLocaleDateTimeString(),
):Synchronizable{
    fun toMap():Map<String,Any>{
        return mapOf(
            "id" to id,
            "version" to version,
            "groupName" to groupName,
            "creationDate" to creationDate
        )
    }
}

fun QueryDocumentSnapshot.toGroup():Group{
    return Group(
        id = id.toInt(),
        version = get("version") as Long,
        groupName = get("groupName") as String,
        creationDate = get("creationDate") as String
    )
}
