package com.kocuni.pianoteacher.utils

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import org.junit.Test

class FileManagerTest {

    val TAG = "FileManager"

    @Test
    fun testContext() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        appContext.assets.list("")?.forEach {
            Log.d(TAG, it)
        }

    }


}