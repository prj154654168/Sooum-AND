package com.sooum.android.ui.onboarding

import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.ui.common.LogInNav
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.viewmodel.MainViewModel
import kotlin.system.exitProcess


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LogInScreen(navController: NavHostController, mainViewModel: MainViewModel, status: String?, dateTime : String?) {

    var showDialog by remember { mutableStateOf(false) }
    val android_id = Settings.Secure.getString(
        LocalContext.current.getContentResolver(),
        Settings.Secure.ANDROID_ID
    )

    // 임시 탈퇴용 로그인 함수 재호출
    LaunchedEffect(Unit) {
        mainViewModel.login(android_id, {
            if (mainViewModel.login == 3 || mainViewModel.login == 4) {
                showDialog = true
            }
        })
    }

    LaunchedEffect(Unit) {
        if (mainViewModel.login == 3 || mainViewModel.login == 4) {
            showDialog = true
        }
    }
    if (showDialog) {
        LoginDialog(mainViewModel.login, mainViewModel.date) {
            exitProcess(0)
            showDialog = false
        }
    }

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
                mainViewModel.login(android_id, {
//                    mainViewModel.fetchUnreadNotificationCount()
                })
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

@Composable
fun LoginDialog(
    mode: Int,
    date: String,
    showDialog: () -> Unit,
) {
    Dialog(onDismissRequest = {
    }) {
        Card(
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 24.dp,
                    bottom = 14.dp,
                    start = 14.dp,
                    end = 14.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (mode == 3) {
                    Text(
                        text = "기존 정지된 계정으로",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.black)
                    )
                    Text(
                        text = "가입이 불가능 합니다.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.black)
                    )
                    Text(
                        text = "- 해당 계정은 정지된 이력이 있는 탈퇴 계정입니다.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(R.color.gray01),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = "- 새로운 계정 생성은 $date 이후 가능합니다.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(R.color.gray01),
                        modifier = Modifier.align(Alignment.Start)
                    )
                } else {
                    Text(
                        text = "최근 탈퇴한 이력이 있습니다.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.black)
                    )
                    Spacer(modifier = Modifier.height(22.dp))
                    Text(
                        text = "- 탈퇴 시점으로 부터 7일 경과 후 새로운 계정 생성이 가능합니다.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(R.color.gray01),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = "- 새로운 계정 생성은 $date 이후 가능합니다.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(R.color.gray01),
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
                Spacer(modifier = Modifier.height(22.dp))
                Button(
                    onClick = {
                        showDialog()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primary_color)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "확인",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
