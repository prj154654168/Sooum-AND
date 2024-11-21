package com.sooum.android.ui

import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sooum.android.ui.common.LogInNav
import com.sooum.android.ui.theme.Primary


@Composable
fun LogInScreen(navController: NavHostController) {
    val android_id = Settings.Secure.getString(
        LocalContext.current.getContentResolver(),
        Settings.Secure.ANDROID_ID
    )
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(top = 462.dp, start = 20.dp),
            text = "당신의 소중한 이야기를", fontSize = 22.sp, color = Primary
        )
        Text(
            modifier = Modifier.padding(start = 20.dp),
            text = "익명의 친구들에게 들려주세요",
            fontSize = 22.sp,
            color = Primary
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            onClick = { navController.navigate(LogInNav.Agree.screenRoute) }) {
            Text(text = "숨 시작하기")
        }
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "기존의 계정이 있으신가요?",
            textDecoration = TextDecoration.Underline
        )
    }

}

