package com.vzardd.xamlyteacher.model

data class Question(
    val questionId: String,
    val questionTitle: String,
    val op1: String,
    val op2: String,
    val op3: String,
    val op4: String,
    val ans: Int,
)
