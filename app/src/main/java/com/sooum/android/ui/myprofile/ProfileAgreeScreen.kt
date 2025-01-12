package com.sooum.android.ui.myprofile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.SooumApplication
import com.sooum.android.ui.common.MyProfile

@Composable
fun ProfileAgreeScreen(navController: NavHostController) {
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
                    text = "이용약관 및 개인정보 처리 방침",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(10.dp)
                )
            }
            SettingRow("개인정보처리방침") {
                SooumApplication().saveVariable(
                    "notionUrl",
                    "https://mewing-space-6d3.notion.site/44e378c9d11d45159859492434b6b128"
                )
                navController.navigate(MyProfile.NotionPage.screenRoute)
            }
            SettingRow("서비스 이용약관") {
                SooumApplication().saveVariable(
                    "notionUrl",
                    "https://mewing-space-6d3.notion.site/3f92380d536a4b569921d2809ed147ef"
                )
                navController.navigate(MyProfile.NotionPage.screenRoute)
            }
            SettingRow("위치정보 이용약관") {
                SooumApplication().saveVariable(
                    "notionUrl",
                    "https://mewing-space-6d3.notion.site/45d151f68ba74b23b24483ad8b2662b4"
                )
                navController.navigate(MyProfile.NotionPage.screenRoute)
            }

        }
    }

}