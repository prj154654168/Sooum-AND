package com.sooum.android.ui.myprofile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.domain.model.NoticeDataModel
import com.sooum.android.ui.theme.Gray1
import com.sooum.android.ui.theme.Gray3
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.viewmodel.NoticeViewModel

@Composable
fun NoticeScreen(navController: NavHostController) {

    val noticeViewModel: NoticeViewModel = hiltViewModel()

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
                    text = "공지사항",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(15.dp)
                )
            }
            LazyColumn {
                items(noticeViewModel.noticeList.value) { notice ->
                    NoticeItem(notice)
                }
            }
        }

    }

}

@Composable
fun NoticeItem(notice: NoticeDataModel.NoticeDto) {
    Column() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(modifier = Modifier.align(Alignment.BottomStart)) {
                if (notice.noticeType == "MAINTENANCE") {
                    Text("공지사항", fontSize = 14.sp, color = Primary)
                } else {
                    Text(
                        "점검안내",
                        fontSize = 14.sp,
                        color = Primary,
                    )
                }
                Text(notice.title, fontSize = 14.sp, modifier = Modifier.padding(start = 6.dp))
            }
            Icon(
                painter = painterResource(R.drawable.ic_next),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }
        Text(
            notice.noticeDate,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 10.dp, start = 20.dp), color = Gray1
        )
        Divider(
            color = Gray3,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
    }
}