package com.example.amioupas

data class AmiiboQuestion(
    val imageUrl: String,
    val choices: List<String>,
    val correctAnswer: String,
    val questionType: String
)
