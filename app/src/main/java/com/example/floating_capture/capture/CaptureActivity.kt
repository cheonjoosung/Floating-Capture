package com.example.floating_capture.capture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class CaptureActivity : Activity() {

    companion object {
        val TAG = CaptureActivity::class.java.simpleName

        var staticIntentData: Intent? = null
        var staticResultCode = 0
    }

    private val SCREEN_CAPTURE_REQUEST_CODE = 9999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*binding = ActivityCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)*/

        staticIntentData?.let {
            startCaptureService(it, staticResultCode)
        } ?: run {
            startProjection()
        }
    }

    private fun startCaptureService(intent: Intent, resultCode: Int) {
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

        startActivityForResult(
            projectManager.createScreenCaptureIntent(),
            SCREEN_CAPTURE_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SCREEN_CAPTURE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) finish()
            else {
                staticIntentData = data!!
                staticResultCode = resultCode

                startCaptureService(data, resultCode)
            }
        }
    }
}