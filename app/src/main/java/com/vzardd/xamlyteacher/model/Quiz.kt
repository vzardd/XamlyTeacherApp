package com.vzardd.xamlyteacher.model

data class Quiz(
    val quizId: String,
    val quizName: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val duration: Int,
    val acceptResponse: Boolean
)
