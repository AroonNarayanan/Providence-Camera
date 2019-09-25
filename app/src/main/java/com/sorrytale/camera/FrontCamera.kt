package com.sorrytale.camera

import android.content.ComponentName
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
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

        //Set notifications
        val bgDelay = prefs.getLong("bgDelay", 1000)
        val bgEnabled = prefs.getBoolean("bgEnabled", false)
        if (bgEnabled) {
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                Intent().apply {
                    component =
                        ComponentName("com.sorrytale.smartr", "com.sorrytale.smartr.Notifier")
                    action = "com.sorrytale.smartr.action.NOTIFY"
                }.also {
                    startForegroundService(it)
                }
            }, bgDelay)
        }
        val instaDelay = prefs.getLong("instaDelay", 1000)
        val instaEnabled = prefs.getBoolean("instaEnabled", false)
        if (instaEnabled) {
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                Intent().apply {
                    component =
                        ComponentName("com.sorrytale.instagram", "com.sorrytale.instagram.Notifier")
                    action = "com.sorrytale.instagram.action.NOTIFY"
                }.also {
                    startForegroundService(it)
                }
            }, instaDelay)
        }

        //Play Video
        val videoUri = prefs.getString("video", "")
        Log.e("VIDEO", videoUri)
        if (videoUri != null && videoUri != "") {
            try {
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
                    val imagePath = MediaStore.Images.Media.insertImage(
                        contentResolver,
                        frame,
                        Calendar.getInstance().timeInMillis.toString() + ".jpg",
                        Calendar.getInstance().timeInMillis.toString() + ".jpg"
                    )
                    android.os.Handler(Looper.getMainLooper()).postDelayed({
                        findViewById<ImageView>(R.id.framepreview).visibility = View.GONE
                    }, 200)
                    Intent().apply {
                        component =
                            ComponentName("com.sorrytale.gallery", "com.sorrytale.gallery.SetPhoto")
                        action = "com.sorrytale.gallery.action.SET"
                        putExtra("image", imagePath)
                    }.also {
                        startForegroundService(it)
                    }
                }
            } catch (ex: Exception) {
            }
        }


        // Button listeners
        findViewById<ImageButton>(R.id.frontflip).setOnClickListener {
            Intent(this, CameraSettings::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}
