package com.vzardd.xamlyteacher.viewmodel

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalTime
import java.util.*

class CreateQuizViewModel: ViewModel() {
    val quizName = mutableStateOf("")
    val duration = mutableStateOf("")
    val mCalendar = Calendar.getInstance()

    val database = Firebase.database.reference

    var mYear: Int = mCalendar.get(Calendar.YEAR)
    var mMonth: Int = mCalendar.get(Calendar.MONTH)
    var mDay: Int = mCalendar.get(Calendar.DAY_OF_MONTH)
    val mHour = mCalendar.get(Calendar.HOUR)
    val mMinute = mCalendar.get(Calendar.MINUTE)

    init{
        mCalendar.time = Date()
    }

    val mDate = mutableStateOf(getFormattedDate(mDay,mMonth+1,mYear))
    @RequiresApi(Build.VERSION_CODES.O)
    val mTime = mutableStateOf(LocalTime.of(mHour,mMinute,0).toString())
    val acceptResponses = mutableStateOf(false)

    private fun getFormattedDate(mDayOfMonth: Int, mMonth: Int, mYear: Int): String {
        var s = ""
        if(mDayOfMonth<10){
            s += "0$mDayOfMonth/"
        }
        else{
            s += "$mDayOfMonth/"
        }
        if(mMonth<10){
            s += "0$mMonth/"
        }
        else{
            s += "$mMonth/"
        }

        s += "$mYear"

        return s
    }
}