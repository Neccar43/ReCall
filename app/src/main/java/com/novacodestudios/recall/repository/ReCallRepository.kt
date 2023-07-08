package com.novacodestudios.recall.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.novacodestudios.recall.roomdb.ReCallDao
import com.novacodestudios.recall.roomdb.table.User
import javax.inject.Inject

class ReCallRepository @Inject constructor(
    private val dao: ReCallDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun insertUserToRoom(user: User) {
        dao.insertUser(user)
    }

    fun insertUserToFirebase(user: User){
    }
}