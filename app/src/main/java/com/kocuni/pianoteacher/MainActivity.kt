package com.kocuni.pianoteacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.kocuni.pianoteacher.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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