package com.sorrytale.camera

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.back
import io.fotoapparat.view.CameraRenderer
import io.fotoapparat.view.CameraView
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    var fileUri = ""

    var fotoapparat: Fotoapparat? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fotoapparat = Fotoapparat(
            this,
            findViewById<CameraView>(R.id.preview),
            scaleType = ScaleType.CenterCrop,
            lensPosition = back()
        ).also {
            it.start()
        }
        findViewById<ImageButton>(R.id.shutter).setOnClickListener {
            val fileName = Calendar.getInstance().timeInMillis.toString() + ".jpg"
            val file = File(Environment.getExternalStorageDirectory(), fileName)
            fotoapparat!!.takePicture().also {
                it.saveToFile(file)
                fileUri = file.toUri().toString()
            }
        }
        findViewById<ImageButton>(R.id.gallery).setOnClickListener {
            if (fileUri != "") {
                Intent().apply {
                    setClassName("com.sorrytale.gallery", "com.sorrytale.gallery.MainActivity")
                    putExtra("photo", fileUri)
                    startActivity(this)
                }
            }
        }
        findViewById<ImageButton>(R.id.flip).setOnClickListener {
            Intent(this, FrontCamera::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onPause() {
        fotoapparat!!.stop()
        super.onPause()
    }

    override fun onResume() {
        if (fotoapparat != null) {
            fotoapparat!!.start()
        }
        super.onResume()
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