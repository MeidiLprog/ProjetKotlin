package com.example.amioupas

import android.content.Context
import android.media.MediaPlayer


object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    private var isMusicEnabled: Boolean = true
    private var musicVolume: Float = 1.0f

    fun init(context: Context) {
        if (mediaPlayer == null) { // Empêche la recréation multiple
            mediaPlayer = MediaPlayer.create(context, R.raw.music_fond)
            mediaPlayer?.isLooping = true
            mediaPlayer?.setVolume(musicVolume, musicVolume)
            if (isMusicEnabled) {
                mediaPlayer?.start()
            }
        }
    }

    fun playMusic() {
        if (isMusicEnabled && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun stopMusic() {
        mediaPlayer?.pause() // Met en pause au lieu de stop
    }

    fun releaseMusic() {
        mediaPlayer?.release() // Libère la mémoire quand l’app est fermée
        mediaPlayer = null
    }

    fun setMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        if (enabled) {
            playMusic()
        } else {
            stopMusic()
        }
    }
    fun pauseMusic() {
        mediaPlayer?.pause()
    }
    fun pauseIfNotInForeground(context: Context) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return

        val packageName = context.packageName
        val isInForeground = appProcesses.any {
            it.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    it.processName == packageName
        }

        if (!isInForeground) {
            pauseMusic()
        }
    }

    fun setMusicVolume(volume: Float) {
        musicVolume = volume
        mediaPlayer?.setVolume(volume, volume)
    }
    fun resumeMusic() {
        if (isMusicEnabled && mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
        }
    }




    fun getMusicVolume(): Float {
        return musicVolume
    }
}
