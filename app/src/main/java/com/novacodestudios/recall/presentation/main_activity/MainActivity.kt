package com.novacodestudios.recall.presentation.main_activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.farimarwat.permissionmate.PMate
import com.farimarwat.permissionmate.rememberPermissionMateState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.novacodestudios.recall.R
import com.novacodestudios.recall.domain.worker.SyncDataWorker
import com.novacodestudios.recall.presentation.navigation.RootGraph
import com.novacodestudios.recall.presentation.util.StandardDialog
import com.novacodestudios.recall.ui.theme.ReCallTheme
import com.novacodestudios.recall.util.ConnectionState
import com.novacodestudios.recall.util.Constants.TAG
import com.novacodestudios.recall.util.connectivityState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ReCallTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val permission = listOf(
                        PMate(
                            Manifest.permission.POST_NOTIFICATIONS,
                            false,
                            stringResource(id = R.string.quiz_notification)
                        )
                    )
                    val pms = rememberPermissionMateState(permissions = permission)
                    pms.start()

                    val connection by connectivityState()

                    val isConnected = connection === ConnectionState.Available

                    RootGraph(
                        navController = rememberNavController(),
                        currentUser = Firebase.auth.currentUser,
                        context = this@MainActivity,
                        isConnected=isConnected,
                    )

                    var isVisible by remember {
                        mutableStateOf(false)
                    }

                    LaunchedEffect(isConnected){
                        isVisible = if (!isConnected) {
                            true
                        } else {
                            delay(2000)
                            false
                        }
                    }

                    if (isVisible){
                        StandardDialog(
                            title = stringResource(id = R.string.no_connection),
                            onDismiss = {  },
                            dismissText = "",
                            requestText = stringResource(id = R.string.exit),
                            onRequest = {finish()}
                        ) {
                            LaunchedEffect(isConnected){
                                if (isConnected){
                                    recreate()
                                }

                            }
                            
                            Text(text = stringResource(id = R.string.no_internet))
                        }
                    }


                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val syncConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val oneTimeSyncDataRequest = OneTimeWorkRequestBuilder<SyncDataWorker>()
            .setInputData(workDataOf(SyncDataWorker.IS_PRIMARY_DB_ROOM to true))
            .setConstraints(syncConstraints)
            .addTag(SyncDataWorker.WORK_NAME)
            .build()

        WorkManager.getInstance(this).enqueue(oneTimeSyncDataRequest)
        Log.d(TAG, "onPause: oneTimeSyncDataRequest çalıştı")
    }

}












