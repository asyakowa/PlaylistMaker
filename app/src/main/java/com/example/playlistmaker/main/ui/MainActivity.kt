package com.example.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.media.MediaActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.settings.ui.SettingsActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1 кнопка
        val searchButton = findViewById<Button>(R.id.se)
        searchButton.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }

        // 2 кнопка
        val medias = findViewById<Button>(R.id.media)
        medias.setOnClickListener {
            val displayIntent = Intent(this, MediaActivity::class.java)
            startActivity(displayIntent)
        }

        // 3 кнопка
        val settingsButton = findViewById<Button>(R.id.settings)
        settingsButton.setOnClickListener {
            val displayIntent = Intent(this,  SettingsActivity ::class.java)
            startActivity(displayIntent)
        }
     }

}