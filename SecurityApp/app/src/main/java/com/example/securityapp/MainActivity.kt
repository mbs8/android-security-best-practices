package com.example.securityapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ActivityCompat.startActivityForResult
import android.app.KeyguardManager
import android.content.Context

class MainActivity : AppCompatActivity() {
    val USER_INFO_AUTHENTICATE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sets listeners to mapButton and cameraButton
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

        // Set listener to youtubeLink
        youtubeLink.setOnClickListener{
            // Starts activity if it's safe
            if (webIntent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            }
        }

        // Set listener to protectButton
        protectButton.setOnClickListener{
            // Asks user for the password of the device to acess private content
            promptUserForPassword()
        }
    }

    fun promptUserForPassword() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

            if (km.isKeyguardSecure) {
                val authIntent = km.createConfirmDeviceCredentialIntent(
                    getString(R.string.pinTitle),
                    getString(R.string.pinMsg)
                )
                startActivityForResult(authIntent, USER_INFO_AUTHENTICATE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println(data)
        if (requestCode == USER_INFO_AUTHENTICATE) {
            if (resultCode == Activity.RESULT_OK) {
                val userInfoIntent: Intent = Intent(applicationContext, UserInfoActivity::class.java)
                startActivity(userInfoIntent)
            }
        }
    }
}
