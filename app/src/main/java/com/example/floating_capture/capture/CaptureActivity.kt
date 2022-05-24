package com.example.floating_capture.capture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.floating_capture.databinding.ActivityCaptureBinding

class CaptureActivity : AppCompatActivity() {

    companion object {
        val TAG = CaptureActivity::class.java.simpleName

        var staticIntentData: Intent? = null
        var staticResultCode = 0
    }

    private lateinit var binding: ActivityCaptureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        staticIntentData?.let {
            startCaptureService(it, staticResultCode)
        } ?: run {
            startProjection()
        }
    }

    private fun startCaptureService(intent: Intent, resultCode: Int) {
        Log.e(TAG, "Good")
        Handler(Looper.getMainLooper()).postDelayed(
            {
                Intent(applicationContext, CaptureService::class.java).apply {
                    putExtra(CaptureService.INTENT_DATA, intent)
                    putExtra(CaptureService.RESULT_CODE, resultCode)
                }.apply {
                    startService(this)
                }
            }, 1000
        )

        finish()
    }

    private fun startProjection() {
        val projectManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        resultListener.launch(projectManager.createScreenCaptureIntent())
    }

    private val resultListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    staticIntentData = it
                    staticResultCode = result.resultCode

                    startCaptureService(it, staticResultCode)
                }
            }
        }
}