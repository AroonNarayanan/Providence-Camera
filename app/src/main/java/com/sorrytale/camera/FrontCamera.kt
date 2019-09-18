package com.sorrytale.camera

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class FrontCamera : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_front_camera)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val videoUri = prefs.getString("video", "")
        if (videoUri != null && videoUri != "") {
            val mediaMetadataReceiver = MediaMetadataRetriever().also {
                it.setDataSource(this, Uri.parse(videoUri))
            }
            val videoView = findViewById<VideoView>(R.id.video)
            videoView.setVideoURI(Uri.parse(videoUri))
            videoView.start()
            findViewById<ImageButton>(R.id.frontshutter).setOnClickListener {
                val frame =
                    mediaMetadataReceiver.getFrameAtTime(videoView.currentPosition.toLong() * 1000)
                findViewById<ImageView>(R.id.framepreview).setImageBitmap(frame)
                findViewById<ImageView>(R.id.framepreview).visibility = View.VISIBLE
                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    frame,
                    Calendar.getInstance().timeInMillis.toString() + ".jpg",
                    Calendar.getInstance().timeInMillis.toString() + ".jpg"
                )
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    findViewById<ImageView>(R.id.framepreview).visibility = View.GONE
                }, 200)
            }
        }
        findViewById<ImageButton>(R.id.frontflip).setOnClickListener {
            Intent(this, CameraSettings::class.java).also {
                startActivity(it)
            }
        }
    }
}
