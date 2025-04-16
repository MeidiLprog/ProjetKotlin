package com.example.amioupas

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MusicControlReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SCREEN_OFF -> {
                MusicManager.pauseMusic()
            }
            Intent.ACTION_USER_PRESENT -> {
                MusicManager.resumeMusic()
            }
        }
    }
}
