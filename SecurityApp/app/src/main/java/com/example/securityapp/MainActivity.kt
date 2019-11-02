package com.example.securityapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.app.KeyguardManager
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    val USER_INFO_AUTHENTICATE = 0                // Callback code to access private information
    val CAMERA_TAG = "Camera"                     // Tag to camera related logs
    val CAMERA_REQUEST_CODE = 101                 // Request code to ask camera permission to user
    val REQUEST_IMAGE_CAPTURE = 1                 // Request code to take photo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sets listeners to mapButton and cameraButton
        mapButton.setOnClickListener{
            val mapIntent = Intent(applicationContext, MapActivity::class.java)
            startActivity(mapIntent)
        }
        cameraButton.setOnClickListener{
            // Try access to camera
            tryStartCamera()
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

    // Verifies permission and try to start camera
    private fun tryStartCamera() {
        makeRequest()
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent()
        }
    }

    // Asks user for Camera permission
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE)
    }

    // Start the activity to take photo
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    // Function to prompt the user enter the lock screen password
    private fun promptUserForPassword() {
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

    // Callback function according to the request code
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // For authentication
        if (requestCode == USER_INFO_AUTHENTICATE && resultCode == Activity.RESULT_OK) {
            val userInfoIntent: Intent =
                Intent(applicationContext, UserInfoActivity::class.java)
            startActivity(userInfoIntent)
        }
        // Gets the image from the camera and puts in a imageView
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            //ImageView.setImageBitmap(imageBitmap)
        }
    }

}
