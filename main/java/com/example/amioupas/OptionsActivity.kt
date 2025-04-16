package com.example.amioupas

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class OptionsActivity : AppCompatActivity() {
    private lateinit var musicSwitch: Switch
    private lateinit var volumeSeekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        // Activer le bouton retour en haut
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        musicSwitch = findViewById(R.id.switch_music)
        volumeSeekBar = findViewById(R.id.seekBar_volume)

        // Charger les préférences actuelles
        musicSwitch.isChecked = MusicManager.getMusicVolume() > 0
        volumeSeekBar.progress = (MusicManager.getMusicVolume() * 100).toInt()

        // Activer/désactiver la musique
        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            MusicManager.setMusicEnabled(isChecked)
        }

        // Changer le volume en direct
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                MusicManager.setMusicVolume(volume)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }


    fun onReturnClicked(view: View) {
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
