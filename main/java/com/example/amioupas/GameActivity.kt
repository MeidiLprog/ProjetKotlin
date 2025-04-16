    package com.example.amioupas

    import android.content.Context
    import android.content.Intent
    import android.graphics.Color
    import android.os.Bundle
    import android.os.Handler
    import android.os.Looper
    import android.text.SpannableString
    import android.text.style.ForegroundColorSpan
    import android.view.*
    import android.widget.*
    import androidx.appcompat.app.AppCompatActivity
    import androidx.appcompat.widget.Toolbar
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import io.realm.kotlin.Realm
    import io.realm.kotlin.RealmConfiguration
    import io.realm.kotlin.ext.query
    import kotlin.math.abs





    class GameActivity : AppCompatActivity() {

        private lateinit var questionText: TextView
        private lateinit var scoreText: TextView
        private lateinit var amiiboImage: ImageView
        private lateinit var answerRecyclerView: RecyclerView
        private lateinit var answerAdapter: AnswerAdapter
        private lateinit var realm: Realm

        private var currentAmiibo: AmiiboEntity? = null
        private var score = 0
        private var questionType = "name"
        private var selectedSeries: List<String> = listOf("Zelda")

        private var x1 = 0f
        private var x2 = 0f
        private val SWIPE_THRESHOLD = 100

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_game)




            selectedSeries = intent.getStringArrayListExtra("selectedSeries") ?: emptyList()

            val toolbar: Toolbar = findViewById(R.id.gameToolbar)
            setSupportActionBar(toolbar)

            val realmConfig = RealmConfiguration.Builder(
                schema = setOf(AmiiboEntity::class)
            )
                .deleteRealmIfMigrationNeeded()
                .compactOnLaunch()
                .build()

            realm = Realm.open(realmConfig)


            questionText = findViewById(R.id.questionText)
            scoreText = findViewById(R.id.scoreText)
            amiiboImage = findViewById(R.id.amiiboImage)
            answerRecyclerView = findViewById(R.id.answerRecyclerView)
            answerRecyclerView.layoutManager = LinearLayoutManager(this)

            answerAdapter = AnswerAdapter(mutableListOf()) { selected ->
                checkAnswer(selected)
            }
            answerRecyclerView.adapter = answerAdapter

            amiiboImage.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> x1 = event.x
                    MotionEvent.ACTION_UP -> {
                        x2 = event.x
                        if (abs(x2 - x1) > SWIPE_THRESHOLD) {
                            questionType = if (x2 > x1) "name" else "gameseries"
                            score -= 1
                            Toast.makeText(this, if (x2 > x1) "Swipe → Nom" else "Swipe ← Gameserie", Toast.LENGTH_SHORT).show()
                            updateScoreDisplay()
                            startGame()
                        }
                    }
                }
                true
            }

            startGame()
        }

        private fun startGame() {
            val amiibos = realm.query<AmiiboEntity>().find()
            if (amiibos.size < 3) {
                Toast.makeText(this, "Pas assez d'amiibos", Toast.LENGTH_SHORT).show()
                return
            }

            currentAmiibo = amiibos.random()

            questionText.text = if (questionType == "gameseries")
                "Devine le jeu de cet Amibo ?" else "Quel est cet Amiibo ?"

            Glide.with(this)
                .load(currentAmiibo?.image)
                .thumbnail(0.1f)
                .placeholder(R.drawable.default_image)
                .into(amiiboImage)

            val correct = if (questionType == "gameseries") currentAmiibo!!.gameSeries else currentAmiibo!!.name
            val wrongs = if (questionType == "gameseries") {
                selectedSeries.filter { it != correct }.shuffled()
            } else {
                amiibos.map { it.name }.filter { it != correct }.shuffled()
            }

            if (wrongs.size < 2) {
                Toast.makeText(this, "Pas assez de choix pour 3 réponses", Toast.LENGTH_SHORT).show()
                return
            }

            val answers = (listOf(correct) + wrongs.take(2)).shuffled()
            answerAdapter.updateAnswers(answers)
        }

        private fun checkAnswer(selectedAnswer: String) {
            val correct = if (questionType == "gameseries") currentAmiibo?.gameSeries else currentAmiibo?.name
            if (selectedAnswer == correct) {
                score += 2
                Toast.makeText(this, "BRAVO ! Score : $score", Toast.LENGTH_SHORT).show()
            } else {
                score -= 2
                Toast.makeText(this, "Faux ! C'eeeeesstt! : $correct", Toast.LENGTH_SHORT).show()
            }
            updateScoreDisplay()
            startGame()
        }

        private fun updateScoreDisplay() {
            scoreText.text = "Score: $score"
            scoreText.setTextColor(
                when {
                    score > 0 -> Color.GREEN
                    score < 0 -> Color.RED
                    else -> Color.BLACK
                }
            )
        }


        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.menu_game, menu)
            return true
        }



        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.reset_score -> {
                    score = 0
                    updateScoreDisplay()
                    Toast.makeText(this, "Score réinitialisé", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.back_to_selection -> {
                    finish()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }

        @Suppress("DEPRECATION")
        fun String.parseAsHtml(): android.text.Spanned {
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                android.text.Html.fromHtml(this, android.text.Html.FROM_HTML_MODE_LEGACY)
            } else {
                android.text.Html.fromHtml(this)
            }
        }

        fun MenuItem.setTitleColor(color: Int) {
            val hexColor = Integer.toHexString(color).uppercase().substring(2)
            val html = "<font color='#$hexColor'>$title</font>"
            this.title = html.parseAsHtml()
        }


        override fun onDestroy() {
            super.onDestroy()
            realm.close()


        }
    }
