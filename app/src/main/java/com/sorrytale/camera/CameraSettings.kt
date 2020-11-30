package com.sorrytale.camera

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.VideoView
import java.lang.Exception

class CameraSettings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_settings)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        findViewById<CheckBox>(R.id.bgEnabled).isChecked = prefs.getBoolean("bgEnabled", false)
        findViewById<CheckBox>(R.id.instaEnabled).isChecked =
            prefs.getBoolean("instaEnabled", false)
        findViewById<Button>(R.id.pickvideo).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "video/*"
            }
            startActivityForResult(intent, 42)
        }
        findViewById<Button>(R.id.pickvideo2).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "video/*"
            }
            startActivityForResult(intent, 45)
        }
        findViewById<Button>(R.id.bgSave).setOnClickListener {
            prefs.edit().putBoolean("bgEnabled", findViewById<CheckBox>(R.id.bgEnabled).isChecked)
                .putLong("bgDelay", getDelay(R.id.bgDelay)).apply()
        }
        findViewById<Button>(R.id.instaSave).setOnClickListener {
            prefs.edit()
                .putBoolean("instaEnabled", findViewById<CheckBox>(R.id.instaEnabled).isChecked)
                .putLong("instaDelay", getDelay(R.id.instaDelay)).apply()
        }
    }

    private fun getDelay(element: Int): Long {
        var delay: Long = 1000
        try {
            delay = findViewById<EditText>(element).text.toString().toLong()
        } catch (ex: Exception) {
        }
        return delay
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            data?.data?.also {
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                prefs.edit().putString("video", it.toString()).apply()
            }
        } else if (requestCode == 45 && resultCode == Activity.RESULT_OK) {
            data?.data?.also {
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                prefs.edit().putString("video2", it.toString()).apply()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
