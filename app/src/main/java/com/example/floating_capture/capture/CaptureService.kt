package com.example.floating_capture.capture

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import java.io.File

class CaptureService : Service() {

    private var dirPath: String = ""

    companion object {
        val TAG = CaptureService::class.java.simpleName

        val INTENT_DATA = "INTENT_DATA"
        val RESULT_CODE = "RESULT_CODE"
    }

    override fun onCreate() {
        super.onCreate()

        makeDirectory()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun makeDirectory() {
        dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/Screenshots/"

        val dir = File(dirPath)
        if (!dir.exists()) {
            val isDirMakeSuccess = dir.mkdirs()
            if (!isDirMakeSuccess) {
                Log.e(TAG, "Failed : directory $dirPath")
                stopSelf()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}