package com.sooum.android.ui.myprofile

import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.ui.viewmodel.EnterUserCodeViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EnterUserCodeScreen(navController: NavHostController) {
    val android_id = Settings.Secure.getString(
        LocalContext.current.getContentResolver(),
        Settings.Secure.ANDROID_ID
    )
    val viewModel: EnterUserCodeViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getEncryptedDeviceId(android_id)
    }

    var code by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    tint = colorResource(R.color.gray_black),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            navController.popBackStack()
                        }
                        .padding(15.dp)
                )
                Text(
                    text = "계정 이관 코드 입력",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(15.dp)
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 149.dp)
                    .height(200.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF9F9F9),
                        shape = RoundedCornerShape(22.dp)
                    )

            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 34.dp, start = 20.dp, end = 20.dp)
                ) {
                    Text(
                        "발급받은 코드를 입력해주세요",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    OutlinedTextField(
                        value = code,
                        onValueChange = {
                            code = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 46.dp),
//                        .border(width = 1.dp, color = colorResource(R.color.gray02), shape = RoundedCornerShape(12.dp))
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(R.color.primary_color),
                            unfocusedBorderColor = colorResource(R.color.primary_color),
                            cursorColor = colorResource(R.color.primary_color),
                            unfocusedContainerColor = colorResource(R.color.gray50),
                            focusedContainerColor = Color.White
                        ),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = colorResource(R.color.black),
                            fontWeight = FontWeight.Medium,
                            textDecoration = TextDecoration.None,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Center
                        ),
                        singleLine = true
                    )
                }
            }

        }

        Button(
            onClick = {
                viewModel.postUserCode(code)
            },
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.BottomCenter),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primary_color)),
            shape = RoundedCornerShape(10.dp),
            enabled = code.isNotEmpty()
        ) {
            Text(text = "계정 이관하기")
        }
    }
}