package com.example.amioupas

import android.app.Application
import android.content.Intent
import com.example.amioupas.connexion.Amiibo
import io.realm.kotlin.Realm

class AmiiboApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MusicManager.init(applicationContext)
        Musique_Glob
    }


    fun generateQuestion(amiibos: List<Amiibo>, questionType: String): AmiiboQuestion? {
        if (amiibos.isEmpty()) return null

        val current = amiibos.random()
        val wrong = amiibos.filter { it != current }.randomOrNull() ?: current

        val choices = if (questionType == "gameseries") {
            listOf(current.gameSeries, wrong.gameSeries).shuffled()
        } else {
            listOf(current.name, wrong.name).shuffled()
        }

        val correctAnswer = if (questionType == "gameseries") current.gameSeries else current.name

        return AmiiboQuestion(
            imageUrl = current.image,
            choices = choices,
            correctAnswer = correctAnswer,
            questionType = questionType
        )
    }

    fun getRandomQuestionFromRealm(realm: Realm, type: String): AmiiboQuestion? {
        val allAmiibos = realm.query(AmiiboEntity::class).find()
        if (allAmiibos.size < 3) return null

        val current = allAmiibos.random()
        val choices = if (type == "gameseries") {
            val wrong = allAmiibos.map { it.gameSeries }.filter { it != current.gameSeries }.distinct().shuffled()
            (listOf(current.gameSeries) + wrong.take(2)).shuffled()
        } else {
            val wrong = allAmiibos.map { it.name }.filter { it != current.name }.distinct().shuffled()
            (listOf(current.name) + wrong.take(2)).shuffled()
        }

        return AmiiboQuestion(
            imageUrl = current.image,
            choices = choices,
            correctAnswer = if (type == "gameseries") current.gameSeries else current.name,
            questionType = type
        )
    }
}
