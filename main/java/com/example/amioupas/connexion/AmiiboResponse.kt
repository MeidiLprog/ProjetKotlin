package com.example.amioupas.connexion

import com.google.gson.annotations.SerializedName

data class AmiiboResponse(
    val amiibo: List<Amiibo>
)

data class Amiibo(
    val name: String,
    val image: String,
    @SerializedName("gameSeries") val gameSeries: String
)
