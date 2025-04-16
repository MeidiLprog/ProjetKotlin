package com.example.amioupas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.amioupas.connexion.Amiibo
import com.example.amioupas.connexion.AmiiboResponse
import com.example.amioupas.connexion.ApiClient
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GameSeriesSelectionActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val gameSeriesList = mutableListOf<String>()
    private var allSelected = false
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.laselection)

        val toolbar: Toolbar = findViewById(R.id.selectionToolbar)
        setSupportActionBar(toolbar)

        val realmConfig = RealmConfiguration.Builder(
            schema = setOf(AmiiboEntity::class)
        )
            .deleteRealmIfMigrationNeeded() // ⚠️ Ajoute cette ligne
            .compactOnLaunch()
            .build()

        realm = Realm.open(realmConfig)


        listView = findViewById(R.id.listViewGames)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, gameSeriesList)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        fetchGameSeries()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_selection, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_toggle_all -> {
                allSelected = !allSelected
                for (i in 0 until listView.count) {
                    listView.setItemChecked(i, allSelected)
                }
                item.title = if (allSelected) "Tout décocher" else "Tout sélectionner"
                return true
            }

            R.id.action_validate -> {
                val selected = gameSeriesList.filterIndexed { i, _ -> listView.isItemChecked(i) }
                if (selected.size < 4) {
                    Toast.makeText(this, "Sélectionnez au moins 4 GameSeries", Toast.LENGTH_SHORT).show()
                    return true
                }

                realm.writeBlocking {
                    deleteAll() // Réinitialise la BDD Realm à chaque lancement
                }

                val api = ApiClient.getApiService(this)
                var remainingCalls = selected.size

                selected.forEach { series ->
                    api.getAmiibosByGameSeries(series).enqueue(object : Callback<AmiiboResponse> {
                        override fun onResponse(call: Call<AmiiboResponse>, response: Response<AmiiboResponse>) {
                            if (response.isSuccessful && response.body() != null) {
                                val amiibos = response.body()!!.amiibo

                                realm.writeBlocking {
                                    amiibos.forEach { amiibo ->
                                        if (query<AmiiboEntity>().find().none { it.name == amiibo.name }) {
                                            copyToRealm(
                                                AmiiboEntity().apply {
                                                    name = amiibo.name
                                                    image = amiibo.image
                                                    gameSeries = amiibo.gameSeries
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            remainingCalls--
                            if (remainingCalls == 0) {
                                launchGameActivity(selected)
                            }
                        }

                        override fun onFailure(call: Call<AmiiboResponse>, t: Throwable) {
                            remainingCalls--
                            if (remainingCalls == 0) {
                                launchGameActivity(selected)
                            }
                        }
                    })
                }

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun launchGameActivity(selected: List<String>) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putStringArrayListExtra("selectedSeries", ArrayList(selected))
        startActivity(intent)
    }

    private fun fetchGameSeries() {
        val api = ApiClient.getApiService(this)
        api.getAllAmiibos().enqueue(object : Callback<AmiiboResponse> {
            override fun onResponse(call: Call<AmiiboResponse>, response: Response<AmiiboResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    gameSeriesList.clear()
                    val series = response.body()!!.amiibo.mapNotNull { it.gameSeries }.distinct().sorted()
                    gameSeriesList.addAll(series)
                    adapter.notifyDataSetChanged()

                    listView.post {
                        val indices = gameSeriesList.indices.shuffled().take(4)
                        indices.forEach { listView.setItemChecked(it, true) }
                    }
                }
            }

            override fun onFailure(call: Call<AmiiboResponse>, t: Throwable) {
                Toast.makeText(this@GameSeriesSelectionActivity, "Erreur API: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
