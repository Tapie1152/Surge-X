package com.surgex.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.surgex.app.auth.AuthController
import com.surgex.app.ui.navigation.SurgeXNavigation
import com.surgex.app.ui.theme.SurgeXTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val authController = AuthController()
    private var googleSignInCallback: ((Boolean) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurgeXTheme {
                SurgeXNavigation(
                    onGoogleSignInRequest = { callback ->
                        googleSignInCallback = callback
                    }
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 9001) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken ?: return

                CoroutineScope(Dispatchers.Main).launch {
                    val result = authController.handleGoogleSignInResult(idToken)
                    googleSignInCallback?.invoke(result is com.surgex.app.auth.AuthResult.Success)
                }
            } catch (e: ApiException) {
                googleSignInCallback?.invoke(false)
            }
        }
    }
}
