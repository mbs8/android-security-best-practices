package com.example.securityapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.security.crypto.EncryptedFile
import java.io.BufferedWriter
import java.io.File

import androidx.security.crypto.MasterKeys
import kotlinx.android.synthetic.main.activity_user_info.*
import java.io.ByteArrayOutputStream
import java.util.*


class UserInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        // Gets the context of the application
        val context = applicationContext

        // Loads the user data to display on the screen
        loadUserInfo(context)

        saveButton.setOnClickListener{
            val dictionary = hashMapOf<String,String>()

            dictionary.put(emailTextView.text.toString(), emailPlainText.text.toString())
            dictionary.put(passwordTextView.text.toString(), passwordPlainText.text.toString())

            writeDataToFile(dictionary, context)
        }
    }

    private fun writeDataToFile(dictionary: HashMap<String, String>, context: Context) {
        // Verifies if the files exist, if so, deletes it
        val fileToWrite = "user_sensitive_data.txt"
        val dataFile = File(context.filesDir, fileToWrite)
        if (dataFile.exists()) {
            dataFile.delete()
        }

        // Although you can define your own key generation parameter specification, it's
        // recommended that you use the value specified here.
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        // Creates a file with this name, or replaces an existing file
        // that has the same name. Note that the file name cannot contain
        // path separators.
        val encryptedFile = EncryptedFile.Builder(
            File(context.filesDir, fileToWrite),
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encryptedFile.openFileOutput().bufferedWriter().use { writer ->
            for ((key, value) in dictionary) {
                writer.write("$key:$value\n")
            }
        }

    }

    private fun loadUserInfo(context: Context) {
        // Checks if the files exists
        val fileToRead = "user_sensitive_data.txt"
        val dataFile = File(context.filesDir, fileToRead)
        if (!dataFile.exists()) {
            return
        }

        // Although you can define your own key generation parameter specification, it's
        // recommended that you use the value specified here.
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val encryptedFile = EncryptedFile.Builder(
            File(context.filesDir, fileToRead),
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        // Reads user info from encrypted file
        val contents = encryptedFile.openFileInput().bufferedReader().useLines { lines ->
            lines.fold("") { working, line ->
                "$working\n$line"
            }
        }

        // Parse the user data file
        val words = contents.split("\n")
        words.forEach{line ->
            val info= line.split(":")
            updateUserInfo(info)
        }
    }

    // Updates the form on screen
    private fun updateUserInfo(info: List<String>) {
        if (info[0] == emailTextView.text.toString())
            emailPlainText.setText(info[1])
        else if (info[0] == passwordTextView.text.toString())
            passwordPlainText.setText(info[1])
    }
}


