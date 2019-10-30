package com.example.securityapp

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        youtubeLink.setOnClickListener{
            // Build the intent
            val webIntent: Intent = Uri.parse(R.string.link.toString()).let {webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }

            // Verify it resolves
            val activities: List<ResolveInfo> = packageManager.queryIntentActivities(
                webIntent,
                PackageManager.MATCH_ALL
            )
            val isIntentSafe: Boolean = activities.isNotEmpty()

            // Starts activity if it's safe
            if (isIntentSafe) {
                startActivity(webIntent)
            }


        }
    }
}
