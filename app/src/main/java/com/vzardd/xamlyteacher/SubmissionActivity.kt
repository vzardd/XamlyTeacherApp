package com.vzardd.xamlyteacher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vzardd.xamlyteacher.model.Leader
import com.vzardd.xamlyteacher.ui.theme.XamlyTeacherTheme

class SubmissionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val submissionsList = mutableStateListOf<Leader>()

        Firebase.database.reference.child("leaderboard").get().addOnSuccessListener {
            it.children.forEach {
                val name = it.child("name").value.toString()
                val progress = it.child("progress").value.toString().toInt()
                submissionsList.add(Leader(name,progress))
            }
            submissionsList.sortByDescending { it.progress }
        }
        setContent {
            XamlyTeacherTheme {
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
                                    text = "All Submissions",
                                    color = Color.White,
                                    fontFamily = FontFamily(Font(R.font.roboto)),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                ) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.primary)
                        .padding(horizontal = 10.dp), contentAlignment = Alignment.TopCenter){
                        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                            if(submissionsList.isEmpty()){
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                                    CircularProgressIndicator()
                                }
                            }
                            else{
                                LazyColumn(modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)){
                                    items(submissionsList.size){
                                        LeaderRowBox(it+1, submissionsList.get(it).name, submissionsList.get(it).progress)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun LeaderRowBox(index: Int, name: String, progress: Int){
        val context = LocalContext.current
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 5.dp, horizontal = 15.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "$index $name", color = Color.Black, fontFamily = FontFamily(Font(R.font.roboto)), fontSize = 24.sp)
                Box(modifier = Modifier.padding(5.dp), contentAlignment = Alignment.Center){
                    Text(text = "$progress/100", fontSize = 14.sp, color = Color.Black, fontFamily = FontFamily(Font(R.font.roboto)))
                }

            }
            Divider()
        }
    }
}