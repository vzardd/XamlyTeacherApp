package com.vzardd.xamlyteacher

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.database.DatabaseReference
import com.vzardd.xamlyteacher.model.Quiz
import com.vzardd.xamlyteacher.ui.theme.XamlyTeacherTheme
import com.vzardd.xamlyteacher.viewmodel.CreateQuizViewModel
import java.time.LocalTime

class CreateQuizActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val cqViewmodel: CreateQuizViewModel = viewModel()
            val quizId = intent.getStringExtra("quizId")
            if(quizId!=null){
                updateValues(quizId, cqViewmodel)
            }
            val titleText = remember{
                if(quizId.isNullOrEmpty()){
                    "Create"
                }
                else{
                    "Update"
                }
            }
            XamlyTeacherTheme {
                Scaffold(
                    scaffoldState = rememberScaffoldState(),
                    topBar = {
                        TopAppBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colors.primaryVariant),
                            elevation = 5.dp,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier
                                    .padding(10.dp)
                                    .clickable {
                                        finish()
                                    }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "back",
                                        tint = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Text(
                                    text = "$titleText Quiz",
                                    color = Color.White,
                                    fontFamily = FontFamily(Font(R.font.roboto)),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                ) {
                    MainContent(cqViewmodel, titleText, quizId)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateValues(quizId: String, cqViewmodel: CreateQuizViewModel) {
        cqViewmodel.database.child("quiz").child(quizId).get().addOnSuccessListener {
            cqViewmodel.quizName.value = it.child("quizName").value.toString()
            cqViewmodel.acceptResponses.value = it.child("acceptResponse").value.toString().toBoolean()
            cqViewmodel.duration.value = it.child("duration").value.toString()
            cqViewmodel.mDate.value = it.child("scheduledDate").value.toString()
            cqViewmodel.mTime.value = it.child("scheduledTime").value.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun MainContent(cqViewmodel: CreateQuizViewModel, titleText: String, quizId: String?) {
        val context = LocalContext.current
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Column {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    value = cqViewmodel.quizName.value,
                    label = { Text(text = "Quiz Name") },
                    onValueChange = {
                        cqViewmodel.quizName.value = it
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        backgroundColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Schedule on:",
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )

                DateTimePickerBox(cqViewmodel)

                Text(
                    text = "Duration (in minutes):",
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    value = cqViewmodel.duration.value,
                    placeholder = { Text(text = "Enter total duration of test.") },
                    onValueChange = {
                        cqViewmodel.duration.value = it
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        backgroundColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Accept Responses:", fontSize = 20.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.roboto)))
                    Spacer(modifier = Modifier.width(10.dp))
                    Switch(checked = cqViewmodel.acceptResponses.value, onCheckedChange = {
                        cqViewmodel.acceptResponses.value = it
                    })
                }
            }

            Button(modifier = Modifier.fillMaxWidth(),onClick = { if(quizId.isNullOrEmpty()){
                createQuiz(cqViewmodel, context)
            }else{
                updateQuiz(cqViewmodel, quizId, context)
            }
            }) {
                Text(text = titleText, color = Color.White, fontSize = 24.sp)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateQuiz(cqViewmodel: CreateQuizViewModel, quizId: String, context: Context) {
        val name = cqViewmodel.quizName.value
        val date = cqViewmodel.mDate.value
        val time = cqViewmodel.mTime.value
        val duration = cqViewmodel.duration.value
        val acceptResponse = cqViewmodel.acceptResponses.value

        if(name.trim().isEmpty() || date.trim().isEmpty() || time.trim().isEmpty() || duration.trim().isEmpty()){
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_LONG).show()
            return
        }

        updateQuizToFirebase(name.trim(),date.trim(),time.trim(),duration.trim().toInt(),acceptResponse, quizId, cqViewmodel.database, onSuccess = {
            Toast.makeText(context, "Quiz updated successfully!", Toast.LENGTH_LONG).show()
            finish()
        }, onFailure = {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun DateTimePickerBox(cqViewmodel: CreateQuizViewModel) {

        val mDatePickerDialog = DatePickerDialog(
            LocalContext.current,
            { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                cqViewmodel.mDate.value = getFormattedDate(mDayOfMonth,mMonth+1,mYear)
            }, cqViewmodel.mYear, cqViewmodel.mMonth,cqViewmodel.mDay
        )

        val mTimePickerDialog = TimePickerDialog(
            LocalContext.current,
            {_, mHour : Int, mMinute: Int ->
                cqViewmodel.mTime.value = LocalTime.of(mHour,mMinute,0).toString()
            }, cqViewmodel.mHour, cqViewmodel.mMinute, true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.clickable { mDatePickerDialog.show() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "calendar",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .size(30.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = cqViewmodel.mDate.value,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(modifier = Modifier.clickable { mTimePickerDialog.show() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_time),
                    contentDescription = "time",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .size(30.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = cqViewmodel.mTime.value,
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createQuiz(cqViewmodel: CreateQuizViewModel, context: Context) {
        val name = cqViewmodel.quizName.value
        val date = cqViewmodel.mDate.value
        val time = cqViewmodel.mTime.value
        val duration = cqViewmodel.duration.value
        val acceptResponse = cqViewmodel.acceptResponses.value

        if(name.trim().isEmpty() || date.trim().isEmpty() || time.trim().isEmpty() || duration.trim().isEmpty()){
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_LONG).show()
            return
        }

        addQuizToFirebase(name.trim(),date.trim(),time.trim(),duration.trim().toInt(),acceptResponse, cqViewmodel.database, onSuccess = {
            Toast.makeText(context, "Quiz created successfully!", Toast.LENGTH_LONG).show()
            finish()
        }, onFailure = {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })
    }

    private fun addQuizToFirebase(
        name: String,
        date: String,
        time: String,
        duration: Int,
        acceptResponse:Boolean,
        database: DatabaseReference,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val key = database.child("quiz").push().key
        if(!key.isNullOrEmpty()){
            database.child("quiz").child(key).setValue(
                Quiz(
                key,
                name,
                date,
                time,
                duration,
                acceptResponse
            )
            ).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                it.message?.let { it1 -> onFailure(it1) }
            }
        }
    }

    private fun updateQuizToFirebase(
        name: String,
        date: String,
        time: String,
        duration: Int,
        acceptResponse:Boolean,
        quizId: String,
        database: DatabaseReference,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val key = quizId
        if(!key.isNullOrEmpty()){
            database.child("quiz").child(key).setValue(
                Quiz(
                    key,
                    name,
                    date,
                    time,
                    duration,
                    acceptResponse
                )
            ).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                it.message?.let { it1 -> onFailure(it1) }
            }
        }
    }

}