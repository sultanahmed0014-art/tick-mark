package com.example.data.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.FeedbackStatus
import com.example.data.UserFeedback
import com.example.data.WatchlistItem
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseManager(private val context: Context) {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val firestore: FirebaseFirestore by lazy {
        val dbId = try {
            context.getString(R.string.firestore_database_id)
        } catch (e: Exception) {
            "ai-studio-android-tickmark-20f479a2-3eda-4160-8677-0c637f28ca7d"
        }
        try {
            FirebaseFirestore.getInstance(FirebaseApp.getInstance(), dbId)
        } catch (e: Exception) {
            FirebaseFirestore.getInstance()
        }
    }

    private val credentialManager = CredentialManager.create(context)

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isSigningIn = MutableStateFlow(false)
    val isSigningIn: StateFlow<Boolean> = _isSigningIn.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    fun signInWithGoogle(onSuccess: (FirebaseUser) -> Unit = {}, onError: (String) -> Unit = {}) {
        _isSigningIn.value = true
        _authError.value = null

        val serverClientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            "993090246151-0r0jhve0d7ph3f1i5d49svq77dg0l4bd.apps.googleusercontent.com"
        }

        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(serverClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                    val idToken = credential.idToken
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = auth.signInWithCredential(firebaseCredential).await()
                    val user = authResult.user
                    if (user != null) {
                        _currentUser.value = user
                        syncUserProfile(user)
                        onSuccess(user)
                    }
                } else {
                    val msg = "Unexpected credential type received"
                    _authError.value = msg
                    onError(msg)
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w("FirebaseManager", "Google sign-in was cancelled by user: ${e.message}")
                _authError.value = "Sign-in cancelled"
                onError("Sign-in cancelled")
            } catch (e: Exception) {
                Log.e("FirebaseManager", "Google sign-in error", e)
                val msg = e.localizedMessage ?: "Sign-in failed"
                _authError.value = msg
                onError(msg)
            } finally {
                _isSigningIn.value = false
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    private fun syncUserProfile(user: FirebaseUser) {
        val userMap = mapOf(
            "name" to (user.displayName ?: "Muhammad Talha Mazhar"),
            "email" to (user.email ?: "talha.mazhar@tickmark.pro"),
            "lastSignIn" to System.currentTimeMillis()
        )
        firestore.collection("users").document(user.uid)
            .set(userMap, SetOptions.merge())
    }

    fun persistFeedback(feedback: UserFeedback) {
        val fbMap = mapOf(
            "userName" to feedback.userName,
            "userEmail" to feedback.userEmail,
            "rating" to feedback.rating,
            "category" to feedback.category,
            "message" to feedback.message,
            "timestamp" to feedback.timestamp,
            "status" to feedback.status.name,
            "adminReply" to feedback.adminReply
        )
        firestore.collection("feedbacks").document(feedback.id)
            .set(fbMap, SetOptions.merge())
    }

    fun updateFeedbackStatusInCloud(feedbackId: String, newStatus: FeedbackStatus, adminReply: String?) {
        val updateMap = mutableMapOf<String, Any>(
            "status" to newStatus.name
        )
        adminReply?.let { updateMap["adminReply"] = it }
        firestore.collection("feedbacks").document(feedbackId)
            .update(updateMap)
    }

    fun persistWatchlistItem(item: WatchlistItem) {
        val uid = auth.currentUser?.uid ?: return
        val map = mapOf(
            "symbol" to item.symbol,
            "name" to item.name,
            "market" to item.market,
            "addedAt" to item.addedAt,
            "targetPriceAlert" to item.targetPriceAlert,
            "userNotes" to item.userNotes
        )
        firestore.collection("users").document(uid).collection("watchlist").document(item.symbol)
            .set(map, SetOptions.merge())
    }

    fun deleteWatchlistItemFromCloud(symbol: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).collection("watchlist").document(symbol)
            .delete()
    }
}
