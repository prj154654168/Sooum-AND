package com.sooum.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sooum.android.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NickNameScreen(navController: NavHostController) {
    var text by remember { mutableStateOf("") }

    Scaffold(topBar = {
        TopAppBar(title = {}, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "뒤로가기",
                )
            }
        })
    }) {
        it
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
            Text(text = "닉네임은 추후 변경 가능해요", fontSize = 14.sp)

            CustomBasicTextField(
                text = text,
                onTextChange = { newText -> text = newText }
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                onClick = { /*TODO*/ }) {
                Text(text = "확인")
            }
        }

    }
}
@Composable
fun CustomBasicTextField(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String = "Enter text..."
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // BasicTextField
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                cursorBrush = SolidColor(Color.Black),
                decorationBox = { innerTextField ->
                    Box(
                        Modifier.fillMaxWidth()
                    ) {
                        if (text.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = TextStyle(color = Color.Gray, fontSize = 16.sp)
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
