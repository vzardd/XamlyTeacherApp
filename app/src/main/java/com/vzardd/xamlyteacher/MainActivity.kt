package com.vzardd.xamlyteacher

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vzardd.xamlyteacher.ui.theme.XamlyTeacherTheme
import com.vzardd.xamlyteacher.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val loginViewModel: LoginViewModel = viewModel()
            val context = LocalContext.current
            LaunchedEffect(Unit){
                if (loginViewModel.auth.currentUser != null) {
                    val intent = Intent(context, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
            XamlyTeacherTheme {
                MainContent(loginViewModel)
            }
        }
    }

    @Composable
    private fun MainContent(loginViewModel: LoginViewModel) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colors.primaryVariant,
                            MaterialTheme.colors.primary
                        )
                    )
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.doodle),
                contentDescription = "doodle", modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.2f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LogoText()
                Image(
                    painter = painterResource(id = R.drawable.teacherlogo),
                    contentDescription = " teacher logo",
                    modifier = Modifier.size(220.dp)
                )
                LoginBox(loginViewModel)
            }
        }
    }

    @Composable
    private fun LogoText() {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontSize = 75.sp, color = Color.White)) {
                    append("X")
                }
                append("amly.")
            }, color = Color.White, fontSize = 50.sp, fontFamily = FontFamily(
                Font(R.font.roboto)
            )
        )
    }

    @Composable
    private fun LoginBox(loginViewModel: LoginViewModel) {
        val focusManager = LocalFocusManager.current
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Teacher Login",
                color = Color.White,
                fontSize = 38.sp,
                modifier = Modifier.padding(5.dp),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.roboto))
            )
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
                value = loginViewModel.usernameTextField.value,
                placeholder = { Text(text = "Email") },
                onValueChange = {
                    loginViewModel.usernameTextField.value = it
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    backgroundColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "email",
                        tint = Color.LightGray,
                        modifier = Modifier.size(25.dp)
                    )
                }
            )
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
                value = loginViewModel.passTextField.value,
                placeholder = { Text(text = "Password") },
                onValueChange = {
                    loginViewModel.passTextField.value = it
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    backgroundColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "password",
                        tint = Color.LightGray,
                        modifier = Modifier.size(25.dp)
                    )
                }
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), onClick = {
                    if (isValid(
                            loginViewModel.usernameTextField.value,
                            loginViewModel.passTextField.value
                        )
                    ) {
                        signIn(
                            loginViewModel,
                            onSuccess = {
                                val intent = Intent(context, HomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            },
                            onFailure = {
                                it?.let {
                                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                }
                            }
                        )
                    }
                }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF1976B2)
                )
            ) {
                Text(
                    text = "Login",
                    fontSize = 28.sp,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.roboto))
                )
            }
        }
    }

    private fun signIn(loginViewModel: LoginViewModel, onSuccess: () -> Unit, onFailure: (String?) -> Unit) {
        val email = loginViewModel.usernameTextField.value.trim()
        val pass = loginViewModel.passTextField.value.trim()
        loginViewModel.auth.signInWithEmailAndPassword(
            email,
            pass
        ).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener{
            onFailure(it.message)
        }
    }

    private fun isValid(email: String, pass: String): Boolean {
        if (email.trim().isEmpty() || pass.trim().isEmpty()) {
            return false;
        }
        return true;
    }
}
