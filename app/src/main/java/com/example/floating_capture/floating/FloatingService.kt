package com.example.floating_capture.floating

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.view.animation.AnimationUtils
import com.example.floating_capture.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs

class FloatingService : Service() {

    private var isFabOpen: Boolean = false

    private lateinit var fab: FloatingActionButton
    private lateinit var fabCapture: FloatingActionButton
    private lateinit var fabExit: FloatingActionButton

    private lateinit var floatingView: View

    private lateinit var windowManager: WindowManager

    private lateinit var params: WindowManager.LayoutParams

    private val CLICK_DRAG_TOLERANCE = 10f

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        initFloatingWindowView()

        initFab()

        initFabTouchEvent()

        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("InflateParams")
    private fun initFloatingWindowView() {
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        if (!::floatingView.isInitialized) {
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            floatingView = inflater.inflate(R.layout.item_floating_button, null)
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).also {
            it.gravity = Gravity.TOP or Gravity.END
            it.x = 0
            it.y = 100
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)
    }

    private fun initFab() {
        if (::floatingView.isInitialized) {
            fab = floatingView.findViewById(R.id.fab)
            fabCapture = floatingView.findViewById(R.id.fab_capture)
            fabExit = floatingView.findViewById(R.id.fab_exit)

            fab.setOnClickListener {
                if (isFabOpen) closeFabWithAnimation()
                else openFabWithAnimation()
            }

            fabCapture.setOnClickListener {

            }

            fabExit.setOnClickListener {
                stopSelf()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFabTouchEvent() {
        var initialX = 0
        var initialY = 0

        var initialTouchX = 0.0f
        var initialTouchY = 0.0f


        fab.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y

                    initialTouchX = motionEvent.rawX
                    initialTouchY = motionEvent.rawY

                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_UP -> {
                    params.x = initialX + (initialTouchX - motionEvent.rawX).toInt()
                    params.y = initialY + (motionEvent.rawY - initialTouchY).toInt()

                    if (abs(initialTouchX - motionEvent.rawX) < CLICK_DRAG_TOLERANCE
                        && abs(initialTouchY - motionEvent.rawY) < CLICK_DRAG_TOLERANCE
                    ) {
                        if (isFabOpen) closeFabWithAnimation()
                        else openFabWithAnimation()

                        return@setOnTouchListener true // to handle Click
                    }

                    return@setOnTouchListener false
                }

                MotionEvent.ACTION_MOVE -> {
                    if (!::floatingView.isInitialized) return@setOnTouchListener false

                    params.x = initialX + (initialTouchX - motionEvent.rawX).toInt()
                    params.y = initialY + (motionEvent.rawY - initialTouchY).toInt()

                    windowManager.updateViewLayout(floatingView, params)

                    return@setOnTouchListener true
                }
            }

            return@setOnTouchListener false
        }
    }

    private fun openFabWithAnimation() {
        isFabOpen = true

        val openAnimation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.open_floating_animation)

        fabCapture.startAnimation(openAnimation)
        fabExit.startAnimation(openAnimation)

        fabCapture.visibility = View.VISIBLE
        fabExit.visibility = View.VISIBLE
    }

    private fun closeFabWithAnimation() {
        isFabOpen = false

        val closeAnimation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.close_floating_animation)

        fabCapture.startAnimation(closeAnimation)
        fabExit.startAnimation(closeAnimation)

        fabCapture.visibility = View.GONE
        fabExit.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()

        if (::floatingView.isInitialized)
            windowManager.removeView(floatingView)
    }
}