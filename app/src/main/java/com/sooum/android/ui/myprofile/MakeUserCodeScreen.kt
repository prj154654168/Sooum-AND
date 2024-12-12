package com.sooum.android.ui.myprofile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.viewmodel.MakeUserCodeViewModel

@Composable
fun MakeUserCodeScreen(navController: NavHostController) {
    val viewModel: MakeUserCodeViewModel = hiltViewModel()
    val context = LocalContext.current // 클립보드와 Toast를 위한 Context
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
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
                    text = "계정 이관 코드 발급",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(15.dp)
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 149.dp)
                    .height(200.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF9F9F9),
                        shape = RoundedCornerShape(22.dp)
                    )

            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 34.dp, start = 20.dp, end = 20.dp)
                ) {
                    Text(
                        "계정을 다른 기기로 이관하기 위한",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        "코드를 발급합니다.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Box(
                        modifier = Modifier
                            .padding(20.dp)
                            .height(64.dp) // 원하는 크기로 설정
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFFFFF), // 배경 색상
                                shape = RoundedCornerShape(12.dp) // 모서리 반지름
                            )
                            .border(2.dp, Primary, RoundedCornerShape(12.dp)) // 빨간색 테두리, 두께 2dp

                            .clickable {
                                // 텍스트를 클립보드에 복사
                                clipboardManager.setText(AnnotatedString(viewModel.code.value))

                                // Toast 표시
                                Toast
                                    .makeText(context, "코드가 복사되었습니다", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    ) {
                        Text(
                            text = viewModel.code.value,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(vertical = 20.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            Text(
                text = "발급된 코드는 24시간만 유효합니다.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 28.dp)
            )
            Text(
                text = "코드가 유출되면 타인이 해당 계정을",
                fontSize = 12.sp,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )
            Text(
                text = "가져갈 수 있으니 주의하세요 ",
                fontSize = 12.sp,
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Button(
            onClick = {
                viewModel.patchCode()
            },
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.BottomCenter),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primary_color)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "코드 재발급하기")
        }
    }
}