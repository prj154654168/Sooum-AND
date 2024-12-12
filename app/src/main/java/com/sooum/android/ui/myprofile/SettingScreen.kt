package com.sooum.android.ui.myprofile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.ui.common.MyProfile

@Composable
fun SettingScreen(navController: NavHostController) {
    var isChecked by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
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
                        .padding(10.dp)
                )
                Text(
                    text = "설정",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(10.dp)
                )
            }

            Text(text = "앱 설정", fontSize = 16.sp)
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "알림 설정",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                Switch(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            SettingRow("작성된 덧글 히스토리") {
                navController.navigate(MyProfile.MyCommentHistory.screenRoute)
            }

            Text(text = "계정 설정", fontSize = 16.sp, modifier = Modifier.padding(top = 20.dp))
            SettingRow("계정 이관 코드 발급") {
                navController.navigate(MyProfile.MakeUserCode.screenRoute)
            }
            SettingRow("계정 이관 코드 입력") {
                navController.navigate(MyProfile.EnterUserCode.screenRoute)
            }
            SettingRow("이용약관 및 개인정보 처리 방침") {

            }

            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(MyProfile.UserDelete.screenRoute)
                    }
            ) {
                Text(text = "계정 탈퇴", color = Color.Red, fontSize = 14.sp)

                Icon(
                    painter = painterResource(R.drawable.ic_next),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                )
            }

            Text(text = "고객센터", fontSize = 16.sp, modifier = Modifier.padding(top = 20.dp))

            SettingRow("공지사항") {
                navController.navigate(MyProfile.Notice.screenRoute)
            }
            SettingRow("1:1 문의하기") {

            }
            SettingRow("제안하기") {

            }
        }
    }
}

@Composable
fun SettingRow(text: String, function: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(vertical = 15.dp)
            .fillMaxWidth()
            .clickable {
                function()
            }
    ) {
        Text(
            text = text, color = Color.Gray, fontSize = 14.sp, modifier = Modifier
                .align(Alignment.CenterStart)
        )
        Icon(
            painter = painterResource(R.drawable.ic_next),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
        )
    }
}