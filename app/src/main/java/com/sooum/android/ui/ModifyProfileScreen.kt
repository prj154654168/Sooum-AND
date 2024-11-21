package com.sooum.android.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sooum.android.R

@Preview
@Composable
fun ModifyProfileScreen() {
    var nicknameTextField by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(46.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    tint = colorResource(R.color.gray_black),
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "프로필 수정",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(42.dp))
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.align(Alignment.Center)) {
                    Image(
                        painter = painterResource(R.drawable.ic_sooum_logo),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_picture_button),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "닉네임",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = colorResource(R.color.gray700)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = nicknameTextField,
                onValueChange = {
                    if (nicknameTextField.length < 8) {
                        nicknameTextField = it
                    }
                },
                placeholder = {
                    androidx.compose.material.Text(
                        text = "8글자 이내 닉네임을 입력해주세요",
                        fontSize = 16.sp,
                        color = colorResource(R.color.gray500),
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.None,
                        lineHeight = 24.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
//                        .border(width = 1.dp, color = colorResource(R.color.gray02), shape = RoundedCornerShape(12.dp))
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.primary_color),
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = colorResource(R.color.primary_color),
                    unfocusedContainerColor = colorResource(R.color.gray50),
                    focusedContainerColor = Color.White

                ),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = colorResource(R.color.gray800),
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.None,
                    lineHeight = 24.sp
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_warning),
                        contentDescription = null,
                        tint = colorResource(R.color.red)
                    )
                    Text(
                        text = "한글자 이상 입력해주세요",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 19.6.sp,
                        color = colorResource(R.color.red)
                    )
                }
                Text(
                    text = "${nicknameTextField.length}/8",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    color = colorResource(R.color.gray500)
                    )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable {

                    },
                color = colorResource(R.color.blue300)
            ) {
                Box() {
                    Text(
                        text = "완료",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 19.6.sp,
                        color = colorResource(R.color.gray_white),
                        modifier = Modifier.align(Alignment.Center)
                            .padding(top = 14.dp, bottom = 14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}