package com.example.securityapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.app.KeyguardManager
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.provider.ContactsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    private val USER_INFO_AUTHENTICATE = 0                // Callback code to access private information
    private val REQUEST_IMAGE_CAPTURE = 1                 // Request code to take photo
    private val CAMERA_REQUEST_CODE = 101                 // Request code to ask camera permission to user
    private val MAP_REQUEST_CODE = 102
    private val CAMERA_TAG = "Camera"                     // Tag to camera related logs
    private val MAP_TAG = "Map"

    // Client to get the last know user location
    lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sets listeners to mapButton and cameraButton
        mapButton.setOnClickListener{
            // Try to open map
            openMap()
        }
        cameraButton.setOnClickListener{
            // Try access to camera
            startCamera()
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

        // Creates a new contact using the default app of the system
        createContactButton.setOnClickListener{
            Intent(Intent.ACTION_INSERT).apply {
                type = ContactsContract.Contacts.CONTENT_TYPE
            }.also { intent ->
                // Make sure that the user has a contacts app installed on their device.
                intent.resolveActivity(packageManager)?.run {
                    startActivity(intent)
                }
            }
        }
    }

    private fun openMap() {
        // Asks user for permission, and then execute onRequestPermissionResult with MAP_REQUEST_CODE
        ActivityCompat.requestPermissions(this,
                                           arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                           MAP_REQUEST_CODE)

    }

    private fun showUserLocationOnMap() {
        // Gets user current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val location = fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                val uri = String.format("geo: %f,%f", location?.latitude, location?.longitude)

                // Create a Uri from the location latitude and longitude
                val gmmIntentUri = Uri.parse(uri)

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                //Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps")

                // Attempt to start an activity that can handle the Intent
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent)
                }
            }


    }

    // Verifies permission and try to start camera
    private fun startCamera() {
        // Asks user for permission
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

    // Callback function when asks for permission
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission to camera was granted
                    dispatchTakePictureIntent()
                } else {
                    // permission to camera was denied
                    Toast.makeText(applicationContext, "A permissão à câmera foi negada.", Toast.LENGTH_SHORT).show()

                }
                return
            }

            MAP_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission to map was granted
                    showUserLocationOnMap()
                } else {
                    // permission to map was denied
                    Toast.makeText(applicationContext, "A permissão ao mapa foi negada.", Toast.LENGTH_SHORT).show()

                }
                return
            }

        }
    }

}
