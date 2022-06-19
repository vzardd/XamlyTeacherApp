package com.vzardd.xamlyteacher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.*
import com.vzardd.xamlyteacher.model.Quiz
import com.vzardd.xamlyteacher.ui.theme.XamlyTeacherTheme
import com.vzardd.xamlyteacher.viewmodel.HomeViewModel

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val homeViewModel: HomeViewModel = viewModel()
            val context = LocalContext.current
            XamlyTeacherTheme {
                Scaffold(
                    scaffoldState = rememberScaffoldState(),
                    topBar = {
                        TopAppBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colors.primaryVariant),
                            elevation = 0.dp,
                        ) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Xamly.",
                                        color = Color.White,
                                        fontFamily = FontFamily(Font(R.font.roboto)),
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "menu", tint = Color.White, modifier = Modifier
                                    .size(35.dp)
                                    .clickable {
                                        homeViewModel.drawerVisibility.value =
                                            !homeViewModel.drawerVisibility.value
                                    } )
                            }

                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            homeViewModel.drawerVisibility.value = false
                            val intent = Intent(context, CreateQuizActivity::class.java)
                            startActivity(intent)
                        }, backgroundColor = MaterialTheme.colors.primary) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "add quiz",
                                tint = Color.White
                            )
                        }
                    }
                ) {
                    Box{
                        MainContent(homeViewModel)
                        AnimatedVisibility(visible = homeViewModel.drawerVisibility.value) {
                            Column(modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.6f)
                                .background(Color(0xFF1976B2))
                                .padding(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth().clickable {
                                    val intent = Intent(context, LeaderBoardActivity::class.java)
                                    startActivity(intent)
                                }.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                   Icon(painter = painterResource(id = R.drawable.ic_leaderboard), contentDescription = "leaderboard", tint = Color.White, modifier = Modifier.size(35.dp) )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = "Leaderboard", color = Color.White, fontFamily = FontFamily(Font(R.font.roboto)), fontSize = 24.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth().clickable { homeViewModel.auth.signOut()
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(painter = painterResource(id = R.drawable.ic_logout), contentDescription = "leaderboard", tint = Color.White, modifier = Modifier.size(35.dp) )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = "Logout", color = Color.White, fontFamily = FontFamily(Font(R.font.roboto)), fontSize = 24.sp)
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun MainContent(homeViewModel: HomeViewModel) {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(bottomStart = 250.dp, bottomEnd = 250.dp))
                .background(MaterialTheme.colors.primary)){
                LottieAnim()
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "Your Quizzes",
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                if(homeViewModel.quizList.value == null){
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        CircularProgressIndicator()
                    }
                }
                else if(homeViewModel.quizList.value!!.isEmpty()){
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        Text(text = "You've not created any quiz yet.", color = Color.Black, fontFamily = FontFamily(Font(R.font.roboto)))
                    }
                }
                else{
                    LazyVerticalGrid(cells = GridCells.Fixed(2), contentPadding = PaddingValues(10.dp)) {
                        itemsIndexed(homeViewModel.quizList.value!!){ i, item ->
                            QuizBox(item){
                                val intent = Intent(context, ViewQuizActivity::class.java )
                                intent.putExtra("quizId", item.quizId)
                                intent.putExtra("quizName", item.quizName)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
            
        }
    }

    @Composable
    private fun LottieAnim() {
        val composition by rememberLottieComposition(
            LottieCompositionSpec
                .RawRes(R.raw.clock)
        )

        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever,
            isPlaying = true,
            speed = 1f,
            restartOnPlay = true
        )

        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(700.dp)
        )

    }

    @Composable
    private fun QuizBox(item: Quiz, viewQuiz: () -> Unit) {
        Box(modifier = Modifier
            .padding(5.dp)
            .clickable {
                viewQuiz()
            }){
            Card(elevation = 5.dp, shape = RoundedCornerShape(10.dp), backgroundColor = Color(0xFFf1f5f9)) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        item.quizName,
                        fontSize = 22.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.roboto))
                    )
                    Text(
                        "${item.scheduledDate}  ${item.scheduledTime}",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        fontFamily = FontFamily(Font(R.font.roboto))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_time),
                            contentDescription = "duration",
                            tint = Color.DarkGray,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(horizontal = 3.dp)
                        )
                        Text(text = "${item.duration}m", color = Color.DarkGray, fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.roboto)))
                    }

                }
            }
        }
    }

}