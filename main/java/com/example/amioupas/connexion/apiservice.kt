package com.example.amioupas.connexion

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/amiibo/") // ✅ Bonne URL
    fun getAmiibosByGameSeries(@Query("gameseries") gameSeries: String): Call<AmiiboResponse>
    @GET("api/amiibo/")
    fun getAllAmiibos(): Call<AmiiboResponse>

}

