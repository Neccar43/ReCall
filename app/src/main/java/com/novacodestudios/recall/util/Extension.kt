package com.novacodestudios.recall.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.novacodestudios.recall.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun Any?.isNotNull(): Boolean {
    return this != null
}

fun <R> List<R>.updateElementByIndex(index: Int, newElement: R): List<R> {
    if (index < 0 || index >= size) {
        throw IndexOutOfBoundsException()
    }
    val updatedList = ArrayList(this)
    updatedList[index] = newElement

    return updatedList.toList()
}

fun relativeTimeAgo(dateTimeString: String, context: Context): String {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val givenDateTime = LocalDateTime.parse(dateTimeString, formatter)
    val currentDateTime = LocalDateTime.now()

    //iki tarih arasındaki farkı belirlenen zaman türünden verir.
    val difference = ChronoUnit.SECONDS.between(givenDateTime, currentDateTime)

    val secondsInMinute = 60
    val secondsInHour = secondsInMinute * 60
    val secondsInDay = secondsInHour * 24
    val secondsInWeek = secondsInDay * 7
    val secondsInMonth = secondsInDay * 30

    return when {
        difference < secondsInMinute -> context.getString(R.string.now)
        difference < secondsInHour -> "${difference / secondsInMinute} ${context.getString(R.string.minute_ago)}"
        difference < secondsInDay -> "${difference / secondsInHour} ${context.getString(R.string.hour_ago)}"
        difference < secondsInWeek -> "${difference / secondsInDay} ${context.getString(R.string.day_ago)}"
        difference < secondsInMonth -> "${difference / secondsInWeek} ${context.getString(R.string.week_ago)}"
        else -> "${difference / secondsInMonth} ${context.getString(R.string.month_ago)}"
    }

}

fun currentISOLocaleDateTimeString(): String {
    val currentDateTime = LocalDateTime.now()
    return currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

fun formatTime(milliSec: Long, context: Context): String {
    val second = milliSec / 1000
    val minute = second / 60
    val remainSecond = second % 60
    return if (minute > 0 && remainSecond > 0) {
        context.getString(R.string.formatted_time_minutes_seconds, minute, remainSecond)
    } else {
        context.getString(R.string.formatted_time_seconds, remainSecond)
    }
}

sealed class ConnectionState {
    data object Available : ConnectionState()
    data object Unavailable : ConnectionState()
}

val Context.currentConnectivityState: ConnectionState
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return getCurrentConnectivityState(connectivityManager)
    }

private fun getCurrentConnectivityState(
    connectivityManager: ConnectivityManager
): ConnectionState {
    val connected = connectivityManager.allNetworks.any { network ->
        connectivityManager.getNetworkCapabilities(network)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }

    return if (connected) ConnectionState.Available else ConnectionState.Unavailable
}


@ExperimentalCoroutinesApi
fun Context.observeConnectivityAsFlow() = callbackFlow {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val callback = NetworkCallback { connectionState -> trySend(connectionState) }

    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    connectivityManager.registerNetworkCallback(networkRequest, callback)

    // Set current state
    val currentState = getCurrentConnectivityState(connectivityManager)
    trySend(currentState)

    // Remove callback when not used
    awaitClose {
        // Remove listeners
        connectivityManager.unregisterNetworkCallback(callback)
    }
}

fun NetworkCallback(callback: (ConnectionState) -> Unit): ConnectivityManager.NetworkCallback {
    return object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            callback(ConnectionState.Available)
        }

        override fun onLost(network: Network) {
            callback(ConnectionState.Unavailable)
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun connectivityState(): State<ConnectionState> {
    val context = LocalContext.current

    // Creates a State<ConnectionState> with current connectivity state as initial value
    return produceState(initialValue = context.currentConnectivityState) {
        // In a coroutine, can make suspend calls
        context.observeConnectivityAsFlow().collect { value = it }
    }
}