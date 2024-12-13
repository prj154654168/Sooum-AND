package com.sooum.android.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.SooumApplication
import com.sooum.android.ui.common.LogInNav
import com.sooum.android.ui.theme.Gray1
import com.sooum.android.ui.theme.Gray50
import com.sooum.android.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NickNameScreen(navController: NavHostController) {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    Scaffold(topBar = {
        TopAppBar(title = {}, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기",
                )
            }
        })
    }) {
        it
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 50.dp),
                    text = "반가워요!", fontSize = 22.sp,
                )
                Text(text = "당신을 어떻게 부르면 될까요?", fontSize = 22.sp)
                Text(
                    text = "닉네임은 추후 변경 가능해요",
                    fontSize = 14.sp,
                    color = Gray1,
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                )

                BasicTextField(
                    value = text,
                    onValueChange = { newText ->
                        // 텍스트 길이 제한
                        if (newText.length <= 8) {
                            text = newText
                        }
                    },
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start
                    ),
                    cursorBrush = SolidColor(Color.Black),
                    decorationBox = { innerTextField ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(color = Gray50, shape = RoundedCornerShape(20.dp))
                                .padding(all = 16.dp)
                            , contentAlignment = Alignment.CenterStart
                        ) {
                            innerTextField()  // TextField의 텍스트를 표시
                            IconButton(
                                onClick = { text = "" },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear text",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (text.isEmpty()) {
                        Row() {
                            Image(
                                painter = painterResource(id = R.drawable.ic_warning),
                                contentDescription = "warning",
                                modifier = Modifier.padding(end = 5.dp)
                            )
                            Text("한글자 이상 입력해주세요", color = Color.Red, fontSize = 14.sp)
                        }
                    }

                    Text(
                        "${text.length}/8",
                        color = Gray1,
                        fontSize = 14.sp,
                        modifier = Modifier.align(
                            Alignment.TopEnd
                        )
                    )
                }

            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.BottomCenter),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                onClick = {
                    SooumApplication().saveVariable("nickName", text)
                    navController.navigate(LogInNav.LogInProfile.screenRoute)
                }) {
                Text(text = "확인")
            }
        }
    }
}

@Composable
fun CustomBasicTextField(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String = "Enter text...",
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = Gray50, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
        ) {
            // BasicTextField
            BasicTextField(
                value = text,
                onValueChange = { newText ->
                    // 텍스트 길이 제한
                    if (newText.length <= 8) {
                        onTextChange(newText)
                    }
                },
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth(),
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start
                ),
                cursorBrush = SolidColor(Color.Black),
                decorationBox = { innerTextField ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically)
                    ) {
                        if (text.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = TextStyle(color = Color.Gray, fontSize = 16.sp),
                                modifier = Modifier
                                    .padding(top = 10.dp, bottom = 10.dp)
                                    .align(Alignment.CenterStart)
                            )
                        }
                        innerTextField()  // TextField의 텍스트를 표시
                    }
                }
            )

            // Clear (X) Icon Button
            if (text.isNotEmpty()) {
                IconButton(onClick = { onTextChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear text",
                        tint = Color.Gray
                    )
                }
            }
        }
    }

}
