package com.niraj.dynamiclinkreferral.base

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.niraj.dynamiclinkreferral.R
import com.niraj.internetobserve.NetworkConnectivityObserver
import com.onurkagan.ksnack_lib.Animations.Slide
import com.onurkagan.ksnack_lib.KSnack.KSnack
import com.onurkagan.ksnack_lib.KSnack.KSnackBarEventListener
import com.onurkagan.ksnack_lib.MinimalKSnack.MinimalKSnack
import com.onurkagan.ksnack_lib.MinimalKSnack.MinimalKSnackStyle
import kotlinx.android.synthetic.main.layout_toast_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

open class BaseActivity : AppCompatActivity() {
    val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    var connected: Boolean = false
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("GiftsCM", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        NetworkConnectivityObserver.internetAvailable.observe(this, Observer {
            it?.let {
                connected = sharedPreferences.getBoolean("connected", false)
                if (connected != it) {
                    connected = it
                    editor.putBoolean("connected", it)
                    editor.apply()
                    showConnectionStatus()
                }
            }
        })
    }

    private fun showConnectionStatus() {
        val message = if (connected) "Connected" else "Disconnected"
        val color = if (connected) R.color.connected else R.color.disconnected
        val minimalKSnack = MinimalKSnack(this@BaseActivity)
        minimalKSnack
            .setMessage(message)
            .setStyle(MinimalKSnackStyle.STYLE_DEFAULT)
            .setBackgroundColor(color)
            .setAnimation(
                Slide.Up.getAnimation(minimalKSnack.minimalSnackView),
                Slide.Down.getAnimation(minimalKSnack.minimalSnackView)
            )
            .alignBottom()
            .setDuration(4000)
            .show()
    }

    fun showSnack(message: String) {
        val isPanelAvailable =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && message == "No Internet."
        val kSnack = KSnack(this@BaseActivity)
        kSnack
            .setListener(object : KSnackBarEventListener {
                override fun showedSnackBar() {
                    println("Showed")
                }

                override fun stoppedSnackBar() {
                    println("Stopped")
                }
            })
            .setAction(if (isPanelAvailable) "Turn On" else "Dismiss") {
                if (isPanelAvailable) {
                    val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
                    startActivityForResult(panelIntent, 545)
                } else {
                    kSnack.dismiss()
                }
            }
            .setMessage(message)
            .setAnimation(
                Slide.Up.getAnimation(kSnack.getSnackView()),
                Slide.Down.getAnimation(kSnack.getSnackView())
            )
            .setDuration(4000) // you can use for auto close.
            .show()
    }

    /** intent to navigate to other screen
     *
     * @param context, Context of Activity/Fragment from where function called
     * @param destinationActivity, Class of Activity to be opened
     * @param finishCurrentActivity, Boolean true if current activity needs to be close
     *
     * @return -
     */
    fun navigateScreen(
        context: Context,
        destinationActivity: Class<*>,
        finishCurrentActivity: Boolean = false
    ) {
        val intent = Intent(context, destinationActivity)
        startActivity(intent)
        if (finishCurrentActivity) {
            finish()
        }
    }

    fun onTabSelected(container: Int, fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .add(container, fragment)
            .commit()
    }

    /** Function Comment
     *
     * <p>Show toast message</p>
     *
     * @param message, Toast message to be displayed
     *
     * @return -
     */
    fun showToast(message: String?, duration: Int = Toast.LENGTH_LONG) {
        val toast = Toast(this@BaseActivity)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 100)
        val toastView = layoutInflater.inflate(R.layout.layout_toast_view, null)
        toastView.txtMessage.text = message
        toast.view = toastView
        toast.duration = duration

        toast.show()
    }

    /** Fetch current app version name
     *
     * @return current app version name
     */
    fun fetchAppVersionName(): String {
        var version = "1.0.0"
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            version = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return version
    }

    @VisibleForTesting
    val progressDialog by lazy {
        ProgressDialog(this)
    }

    fun showProgressDialog() {
        progressDialog.setMessage("Loading...")
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }
}