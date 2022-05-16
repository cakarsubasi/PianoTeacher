package com.kocuni.pianoteacher.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object Permissions {

    fun isRecordPermissionGranted(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(context,
            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    }

    fun isReadExternalStoragePermissionGranted(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(context,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

}