package com.novacodestudios.recall.domain.model

data class User(
    val id: String,
    val name: String?,
    val email: String,
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "email" to email
        )
    }
}

