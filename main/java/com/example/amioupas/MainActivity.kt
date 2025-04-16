package com.example.amioupas

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.amioupas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var selectedGameSeries: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.optionsButton.setOnClickListener {
            startActivity(Intent(this, OptionsActivity::class.java))
        }

        binding.startGameButton.setOnClickListener {
            val intent = Intent(this, GameSeriesSelectionActivity::class.java)
            startActivityForResult(intent, 1) // 🔥 Ouvre l’écran de sélection
        }

        binding.quitButton.setOnClickListener {
            finishAffinity()
            MusicManager.releaseMusic()
            System.exit(0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            selectedGameSeries = data?.getStringArrayListExtra("selectedSeries")

            println("Les GameSeries reçues dans MainActivity : $selectedGameSeries") // 🔍 Debug

            if (!selectedGameSeries.isNullOrEmpty()) {
                val intent = Intent(this, GameActivity::class.java)
                intent.putStringArrayListExtra("selectedSeries", selectedGameSeries)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Aucune GameSeries sélectionnée", Toast.LENGTH_SHORT).show()
            }
        }
    }






}
