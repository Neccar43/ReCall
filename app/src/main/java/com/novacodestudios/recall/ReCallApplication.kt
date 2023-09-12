package com.novacodestudios.recall

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.novacodestudios.recall.domain.worker.QuizWorker
import com.novacodestudios.recall.domain.worker.SyncDataWorker
import com.novacodestudios.recall.util.Constants.TAG
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class ReCallApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        val quizRequest = PeriodicWorkRequestBuilder<QuizWorker>(24, TimeUnit.HOURS)
            .build()

        val workManager = WorkManager.getInstance(this)

        workManager.enqueueUniquePeriodicWork(
            QuizWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            quizRequest
        )
        workManager.getWorkInfosForUniqueWorkLiveData(QuizWorker.WORK_NAME)
            .observeForever { workInfos ->
                Log.d(TAG, "${QuizWorker.WORK_NAME}: ${workInfos.map { it.state }}")
            }

        val syncConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val oneTimeSyncDataRequest= OneTimeWorkRequestBuilder<SyncDataWorker>()
            .setInputData(workDataOf(SyncDataWorker.IS_PRIMARY_DB_ROOM to false))
            .setConstraints(syncConstraints)
            .addTag(SyncDataWorker.WORK_NAME)
            .build()

        workManager.enqueue(oneTimeSyncDataRequest)

        // TODO: Burda sıkıntı var room true yapınca firestore dan room a yüklenmesi gereken veriler siliniyor
        // TODO: Kullanıcı bu uygulamayı kullanıyorken ön plandaysa true yap eğer arka plandaysa false yap
        val periodicSyncDataRequest = PeriodicWorkRequestBuilder<SyncDataWorker>(15, TimeUnit.MINUTES)
            .setInitialDelay(15,TimeUnit.MINUTES)
            .setInputData(workDataOf(SyncDataWorker.IS_PRIMARY_DB_ROOM to true))
            .setConstraints(syncConstraints)
            .addTag(SyncDataWorker.WORK_NAME)
            .build()


        workManager.enqueueUniquePeriodicWork(
            SyncDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicSyncDataRequest
        )

        workManager.getWorkInfosForUniqueWorkLiveData(SyncDataWorker.WORK_NAME)
            .observeForever { workInfos ->
                Log.d(TAG, "${SyncDataWorker.WORK_NAME}: ${workInfos.map { it.state }}")
            }
    }



}