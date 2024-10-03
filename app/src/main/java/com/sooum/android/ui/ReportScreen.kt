package com.sooum.android.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sooum.android.ui.theme.Gray1
import com.sooum.android.ui.theme.Gray4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "신고하기", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "뒤로가기"
                    )
                })
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(20.dp)
        ) {
            Column() {
                Text(
                    text = "신고 사유 선택",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 28.dp)
                )
                ReportButton("비방 및 욕설", "욕설을 사용하여 타인에게 모욕감을 주는 경우")
                ReportButton("개인정보 침해", "법적으로 중요한 타인의 개인정보를 게제")
                ReportButton("부적절한 홍보 및 바이럴", "부적절한 스팸 홍보 행위")
                ReportButton("음란물", "음란한 행위와 관련된 부적절한 행동")
                ReportButton("사칭 및 사기", "사칭으로 타인의 권리를 침해하는 경우")
                ReportButton("기타", "해당하는 신고항목이 없는 경우")


            }
            Button(
                enabled = false,
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Text(text = "신고하기", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ReportButton(title: String, content: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Gray4, RoundedCornerShape(12.dp))
            .padding(bottom = 10.dp)
            .clip(
                shape = RoundedCornerShape(
                    size = 12.dp,
                ),
            ),
    )
    {
        Column() {
            Row() {
                RadioButton(
                    selected = false,
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Text(
                text = content,
                fontSize = 12.sp,
                color = Gray1,
                modifier = Modifier.padding(start = 45.dp)
            )
        }

    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    )
}