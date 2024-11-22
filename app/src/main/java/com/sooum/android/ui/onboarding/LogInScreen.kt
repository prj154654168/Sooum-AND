package com.sooum.android.ui.onboarding

import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.ui.common.LogInNav
import com.sooum.android.ui.common.SoonumNav
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.viewmodel.LogInViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LogInScreen(navController: NavHostController) {
    val android_id = Settings.Secure.getString(
        LocalContext.current.getContentResolver(),
        Settings.Secure.ANDROID_ID
    )
    val viewModel: LogInViewModel = viewModel()
    val context = LocalContext.current
    viewModel.login(android_id, context)

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
                if (viewModel.login == 1) {
                    navController.navigate(SoonumNav.Home.screenRoute) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        } // 백 스택 비우기
                        launchSingleTop = true // 중복된 화면 생성 방지
                    }
                } else {
                    navController.navigate(LogInNav.Agree.screenRoute) {
                    }
                }
            }) {
            Text(text = "숨 시작하기")
        }
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp),
            text = "기존의 계정이 있으신가요?",
            textDecoration = TextDecoration.Underline
        )
    }

}

