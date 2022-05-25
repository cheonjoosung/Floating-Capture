package com.example.floating_capture

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.floating_capture.capture.CaptureService
import com.example.floating_capture.databinding.ActivityMainBinding
import com.example.floating_capture.floating.FloatingService
import java.io.File

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

            rvPicture.adapter = PictureAdapter(getFileList())
            rvPicture.layoutManager = GridLayoutManager(applicationContext, 2)
        }

        stopService()


    }

    private fun getFileList(): MutableList<MyFile> {
        val dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/Screenshots/"

        val dir = File(dirPath)
        val fileList = mutableListOf<MyFile>()

        dir.listFiles()?.let { list ->
            list.forEach { file ->
                fileList.add(MyFile(file.path, file.name))
            }
        }

        return fileList
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
                finish()
            } else {
                Toast.makeText(this, "Start Floating Service", Toast.LENGTH_SHORT).show()
                startService()
            }

        }

    private fun startService() {
        Intent(this, FloatingService::class.java).also {
            startService(it)
        }
        finish()
    }

    private fun stopService() {
        if (!isServiceRunningOnBackground()) {
            Intent(this, FloatingService::class.java).also {
                stopService(it)
            }

            Intent(this, CaptureService::class.java).also {
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