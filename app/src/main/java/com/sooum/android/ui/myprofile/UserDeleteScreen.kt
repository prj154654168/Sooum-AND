package com.sooum.android.ui.myprofile

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.ui.common.LogInNav
import com.sooum.android.ui.viewmodel.UserDeleteViewModel

@Composable
fun UserDeleteScreen(navController: NavHostController) {

    val viewModel: UserDeleteViewModel = hiltViewModel()
    var isChecked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    text = "계정 탈퇴",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(15.dp)
                )
            }

            Text(
                text = "탈퇴하기 전",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 137.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = "몇가지 안내가 있어요",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
            )

            Box(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF9F9F9), // HEX 색상 #F9F9F9
                        shape = RoundedCornerShape(13.dp) // 반지름 13dp
                    )
                    .padding(20.dp)
            ) {
                Column(Modifier.padding(15.dp)) {
                    Text(text = "• 지금까지 작성한 카드와 정보들이 모두 삭제될 예정이에요", color = Color(0xFF6E6E6E))
                    Text(text = "• 재가입은 탈퇴 일자를 기준으로 일주일이후 가능해요", color = Color(0xFF6E6E6E))
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it }, // 상태를 업데이트
                    colors = CheckboxDefaults.colors(
                        disabledCheckedColor = Color.Transparent,
                        checkmarkColor = Color.Black // 체크 표시 색상
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "위 안내사항을 모두 확인했습니다",
                    fontSize = 16.sp,
                    color = if (isChecked) Color.Black else Color(0xFF6D6D6D),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        Button(
            onClick = {
                navController.navigate(LogInNav.LogIn.screenRoute) {
                    viewModel.deleteUser()
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    } // 백 스택 비우기
                    launchSingleTop = true // 중복된 화면 생성 방지
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            enabled = isChecked,
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primary_color)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "탈퇴하기")
        }
    }
}