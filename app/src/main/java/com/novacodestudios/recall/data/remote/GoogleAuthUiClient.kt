package com.novacodestudios.recall.data.remote

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.novacodestudios.recall.R
import com.novacodestudios.recall.domain.model.User
import com.novacodestudios.recall.presentation.util.UIText
import com.novacodestudios.recall.util.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthUiClient @Inject constructor(
    private val context: Context,
    private val oneTapClient: SignInClient,
    private val auth: FirebaseAuth,
) {
    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(buildSignInRequest()).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): Resource<User> {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user

            if (user != null) {
                Resource.Success(User(id = user.uid, name = user.displayName, email = user.email!!))
            }else{
                Resource.Error(message = UIText.DynamicText("user is null."))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Resource.Error(message =UIText.DynamicText(e.localizedMessage!!) )
        }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUserId(): String? = auth.currentUser?.run { uid }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}