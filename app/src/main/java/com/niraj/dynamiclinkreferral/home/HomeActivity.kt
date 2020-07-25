package com.niraj.dynamiclinkreferral.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.niraj.dynamiclinkreferral.R
import com.niraj.dynamiclinkreferral.base.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : BaseActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        setUserInfo(currentUser)

        btnGenerateInvitationLink.setOnClickListener(this)
        btnShareInvitationLink.setOnClickListener(this)
    }

    private fun setUserInfo(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            userId = currentUser.uid
            tvUserId.text = userId
            tvUserName.text = currentUser.displayName
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnGenerateInvitationLink -> generateInvitationLink()
            R.id.btnShareInvitationLink -> shareInvitationLink()
        }
    }

    private fun generateInvitationLink() {
        val manualLink = "https://dynamiclinkreferral.page.link/?" +
                "link=https://dynamiclinkreferrel.web.app/invitedBy=$userId" +
                "&apn=$packageName" +
                "&st=TITLE" +
                "&sd=DESCRIPTION" +
                "&si=https://nandroidex.files.wordpress.com/2017/06/programmer-wallpaper-1.jpg"
        FirebaseDynamicLinks.getInstance().createDynamicLink()
//            .setLink(Uri.parse("https://dynamiclinkreferrel.web.app/invitedBy=$userId"))
//            .setDomainUriPrefix("https://dynamiclinkreferral.page.link")
//            .setAndroidParameters(
//                DynamicLink.AndroidParameters.Builder("com.niraj.dynamiclinkreferral").build()
//            )
            .setLongLink(Uri.parse(manualLink))
            .buildShortDynamicLink()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val shortLink = task.result!!.shortLink
                    val flowchartLink = task.result!!.previewLink
                    Log.e("TAG", shortLink.toString())
                    tvInvitationLink.text = shortLink.toString()
                } else {
                    Log.e("TAG", task.exception?.message.toString())
                }
            }
    }

    private fun shareInvitationLink() {
        val invitationLink = tvInvitationLink.text.toString()
        if (invitationLink.isNotEmpty()) {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, invitationLink)
            intent.type = "text/plain"
            startActivity(intent)
        } else {
            showSnack("Generate link first")
        }
    }
}