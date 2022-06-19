package com.vzardd.xamlyteacher.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vzardd.xamlyteacher.model.Question
import com.vzardd.xamlyteacher.model.Quiz

class ViewQuizViewmodel: ViewModel() {
    val quizName = mutableStateOf("")



}