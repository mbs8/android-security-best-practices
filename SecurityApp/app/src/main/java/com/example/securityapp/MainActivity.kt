package com.example.securityapp

import android.content.Intent
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
    }
}
