package com.example.floating_capture

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.floating_capture.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        val tag = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnStartFloatingService.setOnClickListener {
                overlayPermissionCheck()
            }

            rvPicture.adapter = PictureAdapter(mutableListOf())
        }

        stopService()
    }

    private fun overlayPermissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                ).apply {
                    resultLauncher.launch(this)
                }
            } else {
                startService()
            }

        } else {
            startService()
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Start Floating Service", Toast.LENGTH_SHORT).show()
                startService()
            }

            finish()
        }

    private fun startService() {
        Intent(applicationContext, FloatingService::class.java).also {
            startService(it)
        }
    }

    private fun stopService() {
        if (!isServiceRunningOnBackground()) {
            Intent(applicationContext, FloatingService::class.java).also {
                stopService(it)
            }
        }
    }

    private fun isServiceRunningOnBackground(): Boolean {
        Log.e(tag, "isServiceORunningOnBackground()")
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val runningProcesses: List<ActivityManager.RunningAppProcessInfo> = manager.runningAppProcesses

        for (processInfo in runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    Log.e(tag, "packageName : $activeProcess")
                    if (activeProcess == packageName) {
                        return false
                    }
                }
            }
        }

        return true
    }
}