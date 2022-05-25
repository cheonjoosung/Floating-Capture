package com.example.floating_capture.capture

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.view.Display
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

class CaptureService : Service() {

    private var dirPath: String = ""

    companion object {
        val TAG = CaptureService::class.java.simpleName

        val INTENT_DATA = "INTENT_DATA"
        val RESULT_CODE = "RESULT_CODE"
    }

    private var mediaProjection: MediaProjection? = null

    private val SCREEN_NAME = "screen"

    private val VIRTUAL_DISPLAY_FLAG =
        DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        makeDirectory()

        setNotificationChannel()
    }


    private fun makeDirectory() {
        dirPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/Screenshots/"

        val dir = File(dirPath)
        if (!dir.exists()) {
            val isDirMakeSuccess = dir.mkdirs()
            if (!isDirMakeSuccess) {
                Log.e(TAG, "Failed : directory $dirPath")
                stopSelf()
            }
        }
    }

    private fun setNotificationChannel() {
        val (first, second) = NotificationUtils.getNotification(this)
        startForeground(first, second)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand")
        val resultCode = intent?.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED)
        val data = intent?.getParcelableExtra<Intent>(INTENT_DATA)

        if (resultCode == Activity.RESULT_CANCELED || data == null) stopSelf()

        val mediaProjectManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = mediaProjectManager.getMediaProjection(resultCode!!, data!!)

        if (mediaProjection == null) stopSelf()

        val density = Resources.getSystem().displayMetrics.densityDpi

        createVirtualDisplay(density)

        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("WrongConstant")
    private fun createVirtualDisplay(density: Int) {
        // get width & height
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels

        // capture reader
        val imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1)
        val virtualDisplay =
            mediaProjection?.createVirtualDisplay(SCREEN_NAME,
                width,
                height,
                density,
                VIRTUAL_DISPLAY_FLAG,
                imageReader.surface,
                null,
                null)

        imageReader.setOnImageAvailableListener(
            {
                var fos: FileOutputStream? = null
                try {
                    it.acquireLatestImage().use { image ->
                        image?.let {
                            val planes: Array<Image.Plane> = image.planes
                            val buffer: ByteBuffer = planes[0].buffer
                            val pixelStride: Int = planes[0].pixelStride
                            val rowStride: Int = planes[0].rowStride
                            val rowPadding: Int = rowStride - pixelStride * width

                            // write bitmap to a file
                            val format = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault())
                            val fileName = "capture_${format.format(System.currentTimeMillis())}.jpg"
                            val imageFile = File(dirPath, fileName)

                            if (!imageFile.exists()) {
                                val result = imageFile.createNewFile()
                                Log.e(TAG, "createFile $result")

                                // create bitmap
                                val bitmap = Bitmap.createBitmap(
                                    width + rowPadding / pixelStride,
                                    height,
                                    Bitmap.Config.ARGB_8888
                                )

                                bitmap.copyPixelsFromBuffer(buffer)

                                fos = FileOutputStream(imageFile)
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                                bitmap.recycle()

                                Log.e(TAG, "fileName is $fileName")
                            } else {
                                val imagePath = dirPath + fileName
                                //imageCaptured = true
                                Log.e(TAG, "exists $imagePath")
                                imageReader.setOnImageAvailableListener(null, null)

                                virtualDisplay?.release()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "capture or IO or etc ${e.message}")
                } finally {
                    fos?.close()
                }
            }, null
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }
}