package com.example.playlistmaker



import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backButton = findViewById<ImageView>(R.id.backbutton)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
val shareButton= findViewById<Button>(R.id.share)
        val talkToSupport= findViewById<Button>(R.id.talktosupport)
        val uaButton=findViewById<Button>(R.id.usag)
        talkToSupport.setOnClickListener {
            val message = getString(R.string.thanksforad)
            val email = getString(R.string.email)
            val subject  = getString(R.string.messagefoad)
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(supportIntent)
        }
        shareButton.setOnClickListener {
            val linkToShare =  getString(R.string.linkforad)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, linkToShare)
            }

            startActivity(Intent.createChooser(shareIntent, null))
        }


        uaButton.setOnClickListener {
            val linkToOpen =  getString(R.string.linkfoua)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkToOpen))
            startActivity(browserIntent)
        }

    }
}

