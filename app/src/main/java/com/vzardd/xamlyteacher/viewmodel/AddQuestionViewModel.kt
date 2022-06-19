package com.vzardd.xamlyteacher.viewmodel

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AddQuestionViewModel: ViewModel() {
    val questionTitle = mutableStateOf("")
    val imageUri: MutableState<Uri?> = mutableStateOf(null)

    val op1 = mutableStateOf("")
    val op2 = mutableStateOf("")
    val op3 = mutableStateOf("")
    val op4 = mutableStateOf("")

    val answer = mutableStateOf(0)

    val database = Firebase.database.reference.child("questions")
    val storageRef = Firebase.storage.reference.child("QuestionImages")

    fun updateValues(quizId: String,questionId: String) {
        database.child(quizId).child(questionId).get().addOnSuccessListener {
            questionTitle.value = it.child("questionTitle").value.toString()
            op1.value = it.child("op1").value.toString()
            op2.value = it.child("op2").value.toString()
            op3.value = it.child("op3").value.toString()
            op4.value = it.child("op4").value.toString()
            answer.value = it.child("ans").value.toString().toInt()
        }
        storageRef.child("${questionId}.jpg").downloadUrl.addOnSuccessListener {
            imageUri.value = it
        }
    }
}