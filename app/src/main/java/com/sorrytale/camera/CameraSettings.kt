package com.sorrytale.camera

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.VideoView

class CameraSettings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_settings)
        findViewById<Button>(R.id.pickvideo).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "video/*"
            }
            startActivityForResult(intent, 42)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            data?.data?.also {
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                prefs.edit().putString("video", it.toString()).apply()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
