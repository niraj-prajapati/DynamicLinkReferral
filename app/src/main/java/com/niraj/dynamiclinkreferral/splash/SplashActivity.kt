package com.niraj.dynamiclinkreferral.splash

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.niraj.dynamiclinkreferral.R
import com.niraj.dynamiclinkreferral.base.BaseActivity
import com.niraj.dynamiclinkreferral.home.HomeActivity
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

        coroutineScope.launch {
            delay(2000)
            navigateScreen(this@SplashActivity, HomeActivity::class.java, true)
        }
    }

    override fun onPause() {
        coroutineScope.cancel()
        super.onPause()
    }
}