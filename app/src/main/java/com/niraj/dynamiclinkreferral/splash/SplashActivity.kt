package com.niraj.dynamiclinkreferral.splash

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.niraj.dynamiclinkreferral.R
import com.niraj.dynamiclinkreferral.base.BaseActivity
import com.niraj.dynamiclinkreferral.login.LoginActivity
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link

                    Log.e("TAG", deepLink.toString())

                    val userId = deepLink.toString().split("=")[1]
                    showToast("Invited By: $userId")
                }
            }
            .addOnFailureListener(this) { e -> Log.e("TAG", "getDynamicLink:onFailure", e) }


        coroutineScope.launch {
            delay(2000)
            navigateScreen(this@SplashActivity, LoginActivity::class.java, true)
        }
    }

    override fun onPause() {
        coroutineScope.cancel()
        super.onPause()
    }
}