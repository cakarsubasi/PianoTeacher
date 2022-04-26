package com.kocuni.pianoteacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.Manifest
import android.content.pm.PackageManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.kocuni.pianoteacher.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val REQUEST_MIC = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        if (startEngine()) {
            binding.sampleText.text = "Hell yes"
        } else {
            binding.sampleText.text = "Oh no"
        }

        val button: Button = findViewById(R.id.closeButton);
        button.setOnClickListener { view ->
            binding.sampleText.text = "Button pressed"
        }

        if (!isRecordPermissionGranted()) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_MIC
            )
        }
    }


    fun isRecordPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this,
        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * A native method that is implemented by the 'pianoteacher' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String
    external fun startEngine(): Boolean

    companion object {
        // Used to load the 'pianoteacher' library on application startup.
        init {
            System.loadLibrary("pianoteacher")
        }
    }
}