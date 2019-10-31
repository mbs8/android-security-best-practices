package com.example.securityapp

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapButton.setOnClickListener{
            val mapIntent = Intent(applicationContext, MapActivity::class.java)
            startActivity(mapIntent)
        }
        cameraButton.setOnClickListener{
            val cameraIntent = Intent(applicationContext, CameraActivity::class.java)
            startActivity(cameraIntent)
        }

        // Build the intent
        val webIntent: Intent = Uri.parse(getString(R.string.link)).let {webpage ->
            Intent(Intent.ACTION_VIEW, webpage)
        }

        // Configures the intent to show the appChooser when the link is clicked
        val appChooserTitle = getString(R.string.appChooserTitle)
        val chooser = Intent.createChooser(webIntent, appChooserTitle)

        youtubeLink.setOnClickListener{

            // Starts activity if it's safe
            if (webIntent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            }

        }
    }
}
