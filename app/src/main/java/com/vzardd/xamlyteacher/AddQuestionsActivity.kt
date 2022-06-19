package com.vzardd.xamlyteacher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.DatabaseReference
import com.vzardd.xamlyteacher.model.Question
import com.vzardd.xamlyteacher.ui.theme.XamlyTeacherTheme
import com.vzardd.xamlyteacher.viewmodel.AddQuestionViewModel

class AddQuestionsActivity : ComponentActivity() {
    private val imgUri: MutableState<Uri?> = mutableStateOf(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val quizId = intent.getStringExtra("quizId")
        val questionId = intent.getStringExtra("questionId")
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data!=null && result.data?.data!=null) {
                val data = result.data?.data!!
                imgUri.value = data
            }
        }
        setContent {
            val addQnViewmodel: AddQuestionViewModel = viewModel()
            if(questionId!=null){

                addQnViewmodel.updateValues(quizId!!,questionId)
                imgUri.value = addQnViewmodel.imageUri.value
            }
            XamlyTeacherTheme {
                MainContent(addQnViewmodel, quizId, questionId, resultLauncher)
            }
        }
    }

    @Composable
    private fun MainContent(
        addQnViewmodel: AddQuestionViewModel,
        quizId: String?,
        questionId: String?,
        resultLauncher: ActivityResultLauncher<Intent>
    ) {

        val scrollState = rememberScrollState(0)
        val context = LocalContext.current
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "close",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                finish()
                            })
                    Icon(imageVector = Icons.Default.Check,
                        contentDescription = "submit",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                if (isValid(addQnViewmodel, context)) {
                                    addQuestion(addQnViewmodel, quizId, questionId, context)
                                }
                            })
                }
                TextField(
                    value = addQnViewmodel.questionTitle.value,
                    onValueChange = {
                        addQnViewmodel.questionTitle.value = it
                    },
                    placeholder = {
                        Text(text = "Type your question here...", fontSize = 20.sp)
                    },
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    singleLine = false,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp), contentAlignment = Alignment.Center
            ) {
                if(imgUri.value != null){
                    Box(contentAlignment = Alignment.BottomEnd){
                        Image(
                            painter = rememberAsyncImagePainter(model = imgUri.value, placeholder = painterResource(id = R.drawable.ic_add_photo)),
                            contentDescription = "add photo",
                            modifier = Modifier
                                .size(300.dp),
                            contentScale = ContentScale.Crop
                        )
                        Box(modifier = Modifier.clip(
                            CircleShape).background(Color.Red)
                            .clickable {
                                imgUri.value = null
                            }.padding(10.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "delete", modifier = Modifier.size(20.dp), tint = Color.White)
                        }
                    }
                }
                else{
                    Image(
                        painter = painterResource(id = R.drawable.ic_add_photo),
                        contentDescription = "add photo",
                        modifier = Modifier
                            .size(100.dp)
                            .clickable {
                                addImage(resultLauncher, context)
                            }
                    )
                }

            }

            OptionsBox(addQnViewmodel)

        }
    }

    private fun addImage(
        resultLauncher: ActivityResultLauncher<Intent>,
        context: Context
    ) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        try{
            resultLauncher.launch(intent)
        }
        catch (e:Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun addQuestion(
        addQnViewmodel: AddQuestionViewModel,
        quizId: String?,
        questionId: String?,
        context: Context
    ) {
        val qn = addQnViewmodel.questionTitle.value.trim()
        val op1 = addQnViewmodel.op1.value.trim()
        val op2 = addQnViewmodel.op2.value.trim()
        val op3 = addQnViewmodel.op1.value.trim()
        val op4 = addQnViewmodel.op2.value.trim()
        val ans = addQnViewmodel.answer.value

        if(questionId!=null){
            updateQuestionToFirebase(qn,op1,op2,op3,op4,ans,addQnViewmodel.database, quizId, questionId, onSuccess = { key ->
                if(imgUri.value!=null){
                    Toast.makeText(context, "Please wait...", Toast.LENGTH_LONG).show()
                    postToStorage(addQnViewmodel,key, onSuccessUpload = {
                        Toast.makeText(context, "Question updated successfully!", Toast.LENGTH_LONG).show()
                        finish()
                    }, onFailedUpload = {
                        Toast.makeText(context, "Image upload failed!", Toast.LENGTH_LONG).show()
                        finish()
                    })
                }
                else{
                    Toast.makeText(context, "Please wait...", Toast.LENGTH_LONG).show()
                    addQnViewmodel.storageRef.child("${questionId}.jpg").delete().addOnCompleteListener {
                        Toast.makeText(context, "Question updated successfully!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }


            }, onFailure = {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            })
        }
        else{
            addQuestionToFirebase(qn,op1,op2,op3,op4,ans,addQnViewmodel.database, quizId, onSuccess = { key ->
                if(imgUri.value!=null){
                    Toast.makeText(context, "Please wait...", Toast.LENGTH_LONG).show()
                    postToStorage(addQnViewmodel,key, onSuccessUpload = {
                        Toast.makeText(context, "Question added successfully!", Toast.LENGTH_LONG).show()
                        finish()
                    }, onFailedUpload = {
                        Toast.makeText(context, "Image upload failed!", Toast.LENGTH_LONG).show()
                        finish()
                    })
                }
                else{
                    Toast.makeText(context, "Question added successfully!", Toast.LENGTH_LONG).show()
                    finish()
                }


            }, onFailure = {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            })
        }
    }

    private fun postToStorage(addQnViewmodel: AddQuestionViewModel,key: String, onSuccessUpload: () -> Unit, onFailedUpload: () -> Unit) {
        imgUri.value?.let {
            addQnViewmodel.storageRef.child("${key}.jpg").putFile(
                it
            ).addOnSuccessListener {
                onSuccessUpload()
            }.addOnFailureListener{
                onFailedUpload()
            }
        }
    }

    private fun updateQuestionToFirebase(
        qn: String,
        op1: String,
        op2: String,
        op3: String,
        op4: String,
        ans: Int,
        database: DatabaseReference,
        quizId: String?,
        questionId: String?,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val key = questionId!!
        database.child(quizId!!).child(key!!).setValue(
            Question(
                key,
                qn,
                op1,
                op2,
                op3,
                op4,
                ans
            )
        ).addOnSuccessListener {
            onSuccess(key)
        }.addOnFailureListener {
            onFailure(it.message!!)
        }
    }

    private fun addQuestionToFirebase(
        qn: String,
        op1: String,
        op2: String,
        op3: String,
        op4: String,
        ans: Int,
        database: DatabaseReference,
        quizId: String?,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val key = database.child(quizId!!).push().key
        database.child(quizId).child(key!!).setValue(
            Question(
                key,
                qn,
                op1,
                op2,
                op3,
                op4,
                ans
            )
        ).addOnSuccessListener {
            onSuccess(key)
        }.addOnFailureListener {
            onFailure(it.message!!)
        }
    }

    private fun isValid(addQnViewmodel: AddQuestionViewModel, context: Context): Boolean {
        if (addQnViewmodel.questionTitle.value.trim().isEmpty()) {
            Toast.makeText(context, "Question field cannot be empty.", Toast.LENGTH_LONG).show()
            return false
        }
        if (addQnViewmodel.op1.value.trim().isEmpty() ||
            addQnViewmodel.op2.value.trim().isEmpty() ||
            addQnViewmodel.op3.value.trim().isEmpty() ||
            addQnViewmodel.op4.value.trim().isEmpty()
        ) {
            Toast.makeText(context, "Please fill all options.", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    @Composable
    private fun OptionsBox(addQnViewmodel: AddQuestionViewModel) {
        val options = remember {
            listOf("Option 1", "Option 2", "Option 3", "Option 4")
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            OptionTextField(addQnViewmodel.op1, 1)
            OptionTextField(addQnViewmodel.op2, 2)
            OptionTextField(addQnViewmodel.op3, 3)
            OptionTextField(addQnViewmodel.op4, 4)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Choose Answer: ",
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontSize = 18.sp
                )
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier
                        .clickable {
                            if (addQnViewmodel.answer.value == 0) {
                                addQnViewmodel.answer.value = 3
                            } else {
                                addQnViewmodel.answer.value--
                            }
                        }
                        .padding(5.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_left),
                            contentDescription = "left",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Text(
                        text = options[addQnViewmodel.answer.value],
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.roboto)),
                        fontSize = 20.sp
                    )
                    Box(modifier = Modifier
                        .clickable {
                            if (addQnViewmodel.answer.value == 3) {
                                addQnViewmodel.answer.value = 0
                            } else {
                                addQnViewmodel.answer.value++
                            }
                        }
                        .padding(5.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_right),
                            contentDescription = "right",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun OptionTextField(op: MutableState<String>, i: Int) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            value = op.value,
            label = { Text(text = "Enter Option $i") },
            onValueChange = {
                op.value = it
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Black,
                focusedLabelColor = Color.Black,
                backgroundColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        )
    }
}

