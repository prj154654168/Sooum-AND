package com.sooum.android.ui.onboarding

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.ui.common.LogInNav
import com.sooum.android.ui.theme.Primary


@Composable
fun LogInScreen(navController: NavHostController) {

    Column(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.log_in),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier.padding(top = 10.dp, start = 20.dp),
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
            onClick = {
                navController.navigate(LogInNav.Agree.screenRoute) {
                }
            }) {
            Text(text = "숨 시작하기")
        }
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp),
            text = "기존 계정이 있으신가요?",
            textDecoration = TextDecoration.Underline
        )
    }

}

