package com.venom.lockedin

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.os.*
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.inputmethod.InputMethodManager

class MainActivity : Activity() {

    private var endTime = 0L
    private lateinit var timerText: TextView
    private lateinit var lockButton: Button
    private lateinit var durationInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)

        timerText = findViewById(R.id.timerText)
        lockButton = findViewById(R.id.lockButton)
        durationInput = findViewById(R.id.durationInput)

        timerText.setText(R.string.ready)
        lockButton.setText(R.string.start_lock)

        lockButton.setOnClickListener {
            val inputText = durationInput.text.toString()
            val minutes = inputText.toLongOrNull()
            if (minutes != null && minutes > 0) {
                startLock(minutes * 60 * 1000) // convert minutes → milliseconds
            } else {
                durationInput.error = getString(R.string.invalid_input)
            }
        }
    }

    private fun startLock(duration: Long) {
        endTime = System.currentTimeMillis() + duration

        // Start foreground service for notification
        startService(
            Intent(this, LockService::class.java)
                .putExtra("END_TIME", endTime)
        )

        lockButton.visibility = Button.GONE          // hide button
        durationInput.visibility = EditText.GONE     // hide input field
        hideKeyboard()
        enterImmersive()
        startLockTask()
        startTimer()
    }

    override fun onResume() {
        super.onResume()
        enforceLockTaskIfNeeded()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            enforceLockTaskIfNeeded()
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (System.currentTimeMillis() < endTime) {
            startActivity(
                Intent(this, MainActivity::class.java).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                    )
                }
            )
        }
    }

    /** Re-enter Lock Task Mode if user tries to escape */
    private fun enforceLockTaskIfNeeded() {
        if (System.currentTimeMillis() < endTime) {
            if (!isInLockTask()) startLockTask()
            enterImmersive()
        }
    }

    /** Check if activity is in lock task mode */
    private fun isInLockTask(): Boolean {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return am.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE
    }

    /** Timer logic: updates both screen TextView */
    private fun startTimer() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                val remaining = endTime - System.currentTimeMillis()
                if (remaining > 0) {
                    val totalSeconds = remaining / 1000
                    val minutes = totalSeconds / 60
                    val seconds = totalSeconds % 60
                    timerText.text = String.format("%02d:%02d", minutes, seconds)
                    handler.postDelayed(this, 1000)
                } else {
                    stopLockTask()
                    stopService(Intent(this@MainActivity, LockService::class.java))
                    timerText.setText(R.string.unlocked)
                    lockButton.visibility = Button.VISIBLE
                    durationInput.visibility = EditText.VISIBLE // show input again
                }
            }
        })
    }

    /** Enter fullscreen immersive mode */
    private fun enterImmersive() {
        window.insetsController?.apply {
            hide(WindowInsets.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(durationInput.windowToken, 0)
    }
}
