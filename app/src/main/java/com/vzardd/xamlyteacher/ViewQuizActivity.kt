package com.vzardd.xamlyteacher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vzardd.xamlyteacher.model.Question
import com.vzardd.xamlyteacher.model.Quiz
import com.vzardd.xamlyteacher.ui.theme.XamlyTeacherTheme
import com.vzardd.xamlyteacher.viewmodel.ViewQuizViewmodel

class ViewQuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Firebase.database.reference.child("questions")
        val quizId = intent.getStringExtra("quizId")

        val questionsList = mutableStateOf<List<Question>?>(null)

        val questionsListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val mutableList: MutableList<Question> = mutableListOf()
                snapshot.child(quizId!!).children.forEach {
                    it?.let {
                        val ans = it.child("ans").value?:0
                        val q = Question(
                            it.child("questionId").value.toString(),
                            it.child("questionTitle").value.toString(),
                            it.child("op1").value.toString(),
                            it.child("op2").value.toString(),
                            it.child("op3").value.toString(),
                            it.child("op4").value.toString(),
                            ans.toString().toInt())
                        mutableList.add(q)
                        Log.e("value", it.value.toString())
                    }
                }
                questionsList.value = mutableList.toList()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        database.addValueEventListener(questionsListener)


        setContent {

            val viewQuizViewmodel: ViewQuizViewmodel = viewModel()
            viewQuizViewmodel.quizName.value = intent.getStringExtra("quizName").toString()

            XamlyTeacherTheme {
                val context = LocalContext.current
                val mDisplayMenu = remember{
                    mutableStateOf(false)
                }
                Scaffold(
                    scaffoldState = rememberScaffoldState(),
                    topBar = {
                        TopAppBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colors.primaryVariant),
                            elevation = 0.dp
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
                                    text = viewQuizViewmodel.quizName.value,
                                    color = Color.White,
                                    fontFamily = FontFamily(Font(R.font.roboto)),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    },
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    if(questionsList.value == null){
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .background(Color.White), contentAlignment = Alignment.Center){
                            CircularProgressIndicator()
                        }
                    }
                    else{

                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter){
                            if(questionsList.value!!.size == 0){
                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                    .background(Color.White)
                                    .padding(20.dp), contentAlignment = Alignment.Center){
                                    Text(text = "You've not added any questions to this quiz yet.", fontFamily = FontFamily(Font(R.font.roboto)), color = Color.Black, fontSize = 24.sp, textAlign = TextAlign.Center)
                                }
                            }
                            else{
                                LazyColumn(modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                    .background(Color.White)){
                                    items(questionsList.value!!.size){
                                        QuestionBox(index = it, question = questionsList.value!!.get(it), quizId!!)
                                    }
                                }
                            }

                            Box(modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                                .background(MaterialTheme.colors.primary)
                                .padding(15.dp)){
                                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Icon(painter = painterResource(id = R.drawable.ic_leaderboard), contentDescription = "submissions", tint = Color.White, modifier = Modifier.size(35.dp).clickable {
                                        val intent = Intent(context, SubmissionActivity::class.java)
                                        startActivity(intent)
                                    })
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "edit", tint = Color.White, modifier = Modifier.size(35.dp).clickable {
                                        val intent = Intent(context, CreateQuizActivity::class.java)
                                        intent.putExtra("quizId",quizId)
                                        startActivity(intent)
                                    })
                                }
                            }
                            Box(modifier = Modifier.clip(CircleShape).background(Color(0xFF1976B2)).clickable { val intent = Intent(context, AddQuestionsActivity::class.java)
                                intent.putExtra("quizId",quizId)
                                startActivity(intent) }.padding(10.dp)){
                                Icon(imageVector = Icons.Default.Add, contentDescription = "add", modifier = Modifier.size(60.dp), tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun QuestionBox(index: Int, question: Question, quizId: String){
        val context = LocalContext.current
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)){
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Question ${index+1}:", color = Color.DarkGray, fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.roboto)), fontWeight = FontWeight.Bold)
                Text(text = question.questionTitle, color = Color.DarkGray, fontSize = 22.sp, fontFamily = FontFamily(Font(R.font.roboto)))
                Row(modifier = Modifier
                    .fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(text = "Edit", color = MaterialTheme.colors.primary, fontFamily = FontFamily(Font(R.font.roboto)), modifier = Modifier.clickable {
                        val intent = Intent(context,AddQuestionsActivity::class.java)
                        intent.putExtra("quizId",quizId)
                        intent.putExtra("questionId", question.questionId)
                        startActivity(intent)
                    })
                }
                Divider()

            }
        }
    }
}