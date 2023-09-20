package com.novacodestudios.recall.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.novacodestudios.recall.data.datastore.GroupDataStore
import com.novacodestudios.recall.data.datastore.SortingDataStore
import com.novacodestudios.recall.data.datastore.ThemeDatastore
import com.novacodestudios.recall.data.local.ReCallDao
import com.novacodestudios.recall.data.local.ReCallDatabase
import com.novacodestudios.recall.data.remote.GoogleAuthUiClient
import com.novacodestudios.recall.data.remote.TranslationApi
import com.novacodestudios.recall.data.repository.ReCallRepositoryImpl
import com.novacodestudios.recall.domain.algorithm.SpacedRepetitionAlgorithm
import com.novacodestudios.recall.domain.repository.ReCallRepository
import com.novacodestudios.recall.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    fun injectRepository(
        dao: ReCallDao,
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        algorithm: SpacedRepetitionAlgorithm,
        googleAuthUiClient: GoogleAuthUiClient,
        datastore: ThemeDatastore,
        workManager: WorkManager,
        api: TranslationApi
    ): ReCallRepository =
        ReCallRepositoryImpl(
            dao,
            auth,
            firestore,
            algorithm,
            googleAuthUiClient,
            datastore,
            workManager,
            api
        )

    @Singleton
    @Provides
    fun injectFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Singleton
    @Provides
    fun injectFirestore(): FirebaseFirestore = Firebase.firestore

    @Singleton
    @Provides
    fun injectAlgorithm(): SpacedRepetitionAlgorithm = SpacedRepetitionAlgorithm

    @Singleton
    @Provides
    fun injectGoogleAuth(
        @ApplicationContext context: Context,
        oneTapClient: SignInClient,
        auth: FirebaseAuth,
    ): GoogleAuthUiClient =
        GoogleAuthUiClient(context, oneTapClient, auth)

    @Singleton
    @Provides
    fun injectSignInClient(@ApplicationContext context: Context): SignInClient =
        Identity.getSignInClient(context)

    @Singleton
    @Provides
    fun injectThemeDatastore(@ApplicationContext context: Context) = ThemeDatastore(context)

    @Singleton
    @Provides
    fun injectGroupDatastore(@ApplicationContext context: Context) = GroupDataStore(context)

    @Singleton
    @Provides
    fun injectSortingDatastore(@ApplicationContext context: Context) = SortingDataStore(context)


    @Singleton
    @Provides
    fun injectWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)


    @Singleton
    @Provides
    fun injectTranslationAPI(): TranslationApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TranslationApi::class.java)


}