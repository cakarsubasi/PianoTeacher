package com.kocuni.pianoteacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.kocuni.pianoteacher.databinding.ActivityMainBinding
import com.kocuni.pianoteacher.ui.Permissions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val permissions = 0
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        // get permissions
        if (!Permissions.isRecordPermissionGranted(this) ||
                !Permissions.isReadExternalStoragePermissionGranted(this)) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE),
                permissions
            )
        }

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val songButton: Button = findViewById(R.id.songButton)
        songButton.setOnClickListener { _ ->
            val intent = Intent(this, SongTutorActivity::class.java).apply {  }
            startActivity(intent)
        }

        val serverButton: Button = findViewById(R.id.serverButton)
        serverButton.setOnClickListener { _ ->
            val intent = Intent(this, ServerActivity::class.java).apply {  }
            startActivity(intent)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

    }

}