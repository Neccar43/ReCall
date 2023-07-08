package com.novacodestudios.recall.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.novacodestudios.recall.repository.ReCallRepository
import com.novacodestudios.recall.roomdb.ReCallDao
import com.novacodestudios.recall.roomdb.ReCallDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun injectDatabase(@ApplicationContext context: Context): ReCallDatabase =
        Room.databaseBuilder(context, ReCallDatabase::class.java, "ReCallDB").build()

    @Singleton
    @Provides
    fun injectDao(database: ReCallDatabase): ReCallDao = database.reCallDao()

    @Singleton
    @Provides
    fun injectRepository(dao: ReCallDao):ReCallRepository=ReCallRepository(dao)

    @Singleton
    @Provides
   fun injectFirebaseAuth():FirebaseAuth=Firebase.auth

    @Singleton
    @Provides
    fun injectFirestore():FirebaseFirestore=Firebase.firestore
}