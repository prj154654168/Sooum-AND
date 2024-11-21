package com.sooum.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sooum.android.ui.common.LogInNav
import com.sooum.android.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgreeScreen(navController: NavHostController) {
    var allChecked by remember { mutableStateOf(false) }
    var firstChecked by remember { mutableStateOf(false) }
    var secondChecked by remember { mutableStateOf(false) }

    // "모두 동의합니다" 선택 시 다른 항목도 체크되도록 처리
    LaunchedEffect(allChecked) {
        if (allChecked) {
            firstChecked = true
            secondChecked = true
        }
    }
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
                text = "숨을 시작하기 위해서는", fontSize = 22.sp,
            )
            Text(text = "약관 동의가 필요해요", fontSize = 22.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { allChecked = !allChecked },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = allChecked,
                    onCheckedChange = { allChecked = it }
                )
                Text("약관 전체 동의")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 개별 약관 동의 항목 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { firstChecked = !firstChecked },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = firstChecked,
                    onCheckedChange = { firstChecked = it }
                )
                Text(" [필수] 서비스 이용 약관")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 개별 약관 동의 항목 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { secondChecked = !secondChecked },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = secondChecked,
                    onCheckedChange = { secondChecked = it }
                )
                Text(" [필수] 위치정보 이용 약관")
            }

            // 개별 약관 동의 항목 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { secondChecked = !secondChecked },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = secondChecked,
                    onCheckedChange = { secondChecked = it }
                )
                Text(" [필수] 개인정보 처리 방침")
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                onClick = { navController.navigate(LogInNav.NickName.screenRoute) }) {
                Text(text = "확인")
            }
        }


    }

}