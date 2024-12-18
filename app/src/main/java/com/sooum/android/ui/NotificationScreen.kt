package com.sooum.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sooum.android.R
import com.sooum.android.enums.TabEnum
import kotlinx.coroutines.launch

//틀은 구현 완료 했음. 내일 디테일한 부분 마저 봐야지
@Composable
fun NotificationScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = {3})

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 11.dp, bottom = 11.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = colorResource(R.color.gray_black),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        navController.popBackStack()
                    }
            )
            Text(
                text = "덧글 히스토리",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp,
                color = colorResource(R.color.gray_black),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        TabLayout(pagerState)
//        TabRow(selectedTabIndex = selectedTabIndex) {
//            tabs.forEachIndexed { index, title ->
//                Tab(
//                    selected = selectedTabIndex == index,
//                    onClick = { selectedTabIndex = index }
//                ) {
//                    Text()
//                }
//            }
//        }
    }
}

@Composable
fun TabLayout(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()
    val tabList = listOf(TabEnum.ALL, TabEnum.REPLY, TabEnum.LIKE)
    val selectedIndex = pagerState.currentPage

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TabRow(
            selectedTabIndex = selectedIndex,
            indicator = {tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = colorResource(R.color.blue300),
                    height = 1.dp
                )
            }
        ) {
            tabList.forEachIndexed { index, page ->
                val selected = (pagerState.currentPage == index)
                Tab(
                    selected = selected,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    modifier = Modifier
//                        .then(
//                            Modifier.clickable(
//                                interactionSource = remember { MutableInteractionSource() },
//                                indication = null, // Ripple 효과 제거
//                                onClick = {
//                                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
//                                }
//                            )
//                        )
                        .indication(interactionSource = remember { MutableInteractionSource() },
                            indication = null)
                        .padding(top = 7.dp, bottom = 11.dp)
//                        .clickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) {
//
//                        }
                ) {
                    Text(
                        text = when(page) {
                            TabEnum.ALL -> "전체"
                            TabEnum.REPLY -> "답카드"
                            TabEnum.LIKE -> "공감"
                        },
                        color = if (selected) colorResource(R.color.blue300)
                        else colorResource(R.color.gray400),
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.Medium
                        else FontWeight.Normal,
                        lineHeight = 19.6.sp,
//                        modifier = Modifier.clickable {
//                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
//                        }
                    )
                }
            }
        }
        HorizontalPager(
            state = pagerState
        ) { index ->
            when(tabList[index]) {
                TabEnum.ALL -> {
                    AllScreen()
                }
                TabEnum.REPLY -> {
                    ReplyScreen()
                }
                TabEnum.LIKE -> {
                    NotExistNotification()
                }
            }
        }
    }
}

@Composable
fun AllScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp)
    ) {
        Text(
            text = "읽지 않음 (12개)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 19.6.sp,
            color = colorResource(R.color.gray_black),
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            items(2) {
                WarningNotificationElement()
            }
            items(10) {
                CardNotificationElement()
            }
        }
    }
}

@Composable
fun ReplyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp)
    ) {
        Text(
            text = "읽지 않음 (15개)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 19.6.sp,
            color = colorResource(R.color.gray_black),
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            items(15) {
                CardNotificationElement()
            }
        }
    }
}

@Composable
fun CardNotificationElement() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            ImageLoaderForUrl("https://cdn.speconomy.com/news/photo/201611/20161102_1_bodyimg_73550.jpg")
            Text(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .align(Alignment.Center),
                text = "최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 최대글자수 ",
                fontSize = 3.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 5.4.sp,
                color = colorResource(R.color.gray_white),
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = "최대글자수8글자님이 카드에 공감하였습니다.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.8.sp,
            color = colorResource(R.color.gray700)
        )
        Spacer(modifier = Modifier.weight(1f))
        Row {
            Text(
                text = "3분전",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.8.sp,
                color = colorResource(R.color.gray400)
            )
            Box(
                modifier = Modifier
                    .size(6.dp) // 크기 설정
                    .clip(CircleShape) // 동그라미 모양으로 클리핑
                    .background(colorResource(R.color.red)) // 배경색 설정
            )
        }
    }
}

@Composable
fun WarningNotificationElement() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "[정지]  ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.8.sp,
            color = colorResource(R.color.red)

        )
        Text(
            text = "최대글자수8글자님이 카드에 공감하였습니다.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.8.sp,
            color = colorResource(R.color.gray700)
        )
        Spacer(modifier = Modifier.weight(1f))
        Row {
            Text(
                text = "3분전",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.8.sp,
                color = colorResource(R.color.gray400)
            )
            Box(
                modifier = Modifier
                    .size(6.dp) // 크기 설정
                    .clip(CircleShape) // 동그라미 모양으로 클리핑
                    .background(colorResource(R.color.red)) // 배경색 설정
            )
        }
    }
}

@Composable
fun NotExistNotification() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "알림이 아직 없어요",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 24.sp,
            color = colorResource(R.color.gray400),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}