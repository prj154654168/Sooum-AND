package com.sooum.android.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sooum.android.R
import com.sooum.android.domain.model.NotificationDataModel
import com.sooum.android.enums.NotificationTypeEnum
import com.sooum.android.enums.TabEnum
import com.sooum.android.ui.common.PostNav
import com.sooum.android.ui.viewmodel.NotificationViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationScreen(navController: NavController) {
    val notificationViewModel: NotificationViewModel = hiltViewModel()
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
        TabLayout(pagerState, notificationViewModel, navController)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabLayout(
    pagerState: PagerState,
    notificationViewModel: NotificationViewModel,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val tabList = listOf(TabEnum.ALL, TabEnum.REPLY, TabEnum.LIKE)
    val selectedIndex = pagerState.currentPage

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TabRow(
            selectedTabIndex = selectedIndex,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = colorResource(R.color.blue300),
                    height = 1.dp,
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
                        .padding(top = 7.dp, bottom = 11.dp)
                        .clickable(
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            },
                            indication = null, // 리플 효과 제거
                            interactionSource = remember { MutableInteractionSource() }
                        )
                ) {
                    Text(
                        text = when (page) {
                            TabEnum.ALL -> "전체"
                            TabEnum.REPLY -> "답카드"
                            TabEnum.LIKE -> "공감"
                        },
                        color = if (selected) colorResource(R.color.blue300)
                        else colorResource(R.color.gray400),
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.Medium
                        else FontWeight.Normal,
                        lineHeight = 19.6.sp
                    )
                }
            }
        }
        HorizontalPager(
            state = pagerState
        ) { index ->
            when (tabList[index]) {
                TabEnum.ALL -> {
                    AllScreen(notificationViewModel, navController)
                }
                TabEnum.REPLY -> {
                    ReplyScreen(notificationViewModel, navController)
                }
                TabEnum.LIKE -> {
                    LikeScreen(notificationViewModel, navController)
                }
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AllScreen(notificationViewModel: NotificationViewModel, navController: NavController) {
    val lazyAllUnread = notificationViewModel.allUnreadNotificationList.collectAsLazyPagingItems()
    val lazyAllRead = notificationViewModel.allReadNotificationList.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "읽지 않음 (${notificationViewModel.allUnreadCount.value}개)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 19.6.sp,
            color = colorResource(R.color.gray_black),
            modifier = Modifier.padding(top = 16.dp, start = 20.dp, end = 20.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (lazyAllUnread.itemCount == 0 && lazyAllRead.itemCount == 0) {
            NotExistNotification(notificationViewModel)
        } else {
            LazyColumn {
                items(lazyAllUnread.itemCount) { index ->
                    val notificationItem = lazyAllUnread[index]
                    notificationItem?.let {
                        if (notificationItem is NotificationDataModel.BlockedNotification || notificationItem is NotificationDataModel.DeletedNotification) {
                            WarningNotificationElement(notificationViewModel, notificationItem, false)
                        } else {
                            CardNotificationElement(notificationViewModel, navController, notificationItem, false)
                        }
                    }
                }
                if (lazyAllUnread.itemCount != 0 && lazyAllRead.itemCount != 0) {
                    item {
                        Divider(color = colorResource(R.color.gray100), thickness = 4.dp)
                    }
                }
                items(lazyAllRead.itemCount) { index ->
                    val notificationItem = lazyAllRead[index]
                    notificationItem?.let {
                        if (notificationItem is NotificationDataModel.BlockedNotification || notificationItem is NotificationDataModel.DeletedNotification) {
                            WarningNotificationElement(notificationViewModel, notificationItem, true)
                        } else {
                            CardNotificationElement(notificationViewModel, navController, notificationItem, true)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReplyScreen(notificationViewModel: NotificationViewModel, navController: NavController) {
    val lazyCardUnread = notificationViewModel.cardUnreadNotificationList.collectAsLazyPagingItems()
    val lazyCardRead = notificationViewModel.cardReadNotificationList.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "읽지 않음 (${notificationViewModel.cardUnreadCount.value}개)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 19.6.sp,
            color = colorResource(R.color.gray_black),
            modifier = Modifier.padding(top = 16.dp, start = 20.dp, end = 20.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (lazyCardUnread.itemCount == 0 && lazyCardRead.itemCount == 0) {
            NotExistNotification(notificationViewModel)
        } else {
            LazyColumn {
                items(lazyCardUnread.itemCount) { index ->
                    val notificationItem = lazyCardUnread[index]
                    notificationItem?.let {
                        if (notificationItem is NotificationDataModel.BlockedNotification || notificationItem is NotificationDataModel.DeletedNotification) {
                            WarningNotificationElement(notificationViewModel, notificationItem, false)
                        } else {
                            CardNotificationElement(notificationViewModel, navController, notificationItem, false)
                        }
                    }
                }
                if (lazyCardUnread.itemCount != 0 && lazyCardRead.itemCount != 0) {
                    item {
                        Divider(color = colorResource(R.color.gray100), thickness = 4.dp)
                    }
                }
                items(lazyCardRead.itemCount) { index ->
                    val notificationItem = lazyCardRead[index]
                    notificationItem?.let {
                        if (notificationItem is NotificationDataModel.BlockedNotification || notificationItem is NotificationDataModel.DeletedNotification) {
                            WarningNotificationElement(notificationViewModel, notificationItem, true)
                        } else {
                            CardNotificationElement(notificationViewModel, navController, notificationItem, true)
                        }
                    }
                }
            }
        }
    }
}

inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit
): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LikeScreen(notificationViewModel: NotificationViewModel, navController: NavController) {
    val lazyLikeUnread = notificationViewModel.likeUnreadNotificationList.collectAsLazyPagingItems()
    val lazyLikeRead = notificationViewModel.likeReadNotificationList.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "읽지 않음 (${notificationViewModel.likeUnreadCount.value}개)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 19.6.sp,
            color = colorResource(R.color.gray_black),
            modifier = Modifier.padding(top = 16.dp, start = 20.dp, end = 20.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (lazyLikeUnread.itemCount == 0 && lazyLikeRead.itemCount == 0) {
            NotExistNotification(notificationViewModel)
        } else {
            LazyColumn {
                items(lazyLikeUnread.itemCount) { index ->
                    val notificationItem = lazyLikeUnread[index]
                    notificationItem?.let {
                        if (notificationItem is NotificationDataModel.BlockedNotification || notificationItem is NotificationDataModel.DeletedNotification) {
                            WarningNotificationElement(notificationViewModel, notificationItem, false)
                        } else {
                            CardNotificationElement(notificationViewModel, navController, notificationItem, false)
                        }
                    }
                }
                if (lazyLikeUnread.itemCount != 0 && lazyLikeRead.itemCount != 0) {
                    item {
                        Divider(color = colorResource(R.color.gray100), thickness = 4.dp)
                    }
                }
                items(lazyLikeRead.itemCount) { index ->
                    val notificationItem = lazyLikeRead[index]
                    notificationItem?.let {
                        if (notificationItem is NotificationDataModel.BlockedNotification || notificationItem is NotificationDataModel.DeletedNotification) {
                            WarningNotificationElement(notificationViewModel, notificationItem, true)
                        } else {
                            CardNotificationElement(notificationViewModel, navController, notificationItem, true)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CardNotificationElement(notificationViewModel: NotificationViewModel, navController: NavController, notificationItem: NotificationDataModel, isRead: Boolean) {
    Log.d("123", notificationItem.toString())
    val nickname = if (notificationItem is NotificationDataModel.FeedLikeNotification) {
        notificationItem.nickName
    } else if (notificationItem is NotificationDataModel.CommentLikeNotification) {
        notificationItem.nickName
    } else if (notificationItem is NotificationDataModel.CommentWriteNotification) {
        notificationItem.nickName
    } else {
        ""
    }

    val targetCardId = if (notificationItem is NotificationDataModel.FeedLikeNotification) {
        notificationItem.targetCardId
    } else if (notificationItem is NotificationDataModel.CommentLikeNotification) {
        notificationItem.targetCardId
    } else if (notificationItem is NotificationDataModel.CommentWriteNotification) {
        notificationItem.targetCardId
    } else {
        ""
    }

    val content = if (notificationItem is NotificationDataModel.FeedLikeNotification) {
        notificationItem.content
    } else if (notificationItem is NotificationDataModel.CommentLikeNotification) {
        notificationItem.content
    } else if (notificationItem is NotificationDataModel.CommentWriteNotification) {
        notificationItem.content
    } else {
        ""
    }

    val createTime = if (notificationItem is NotificationDataModel.FeedLikeNotification) {
        notificationItem.createTime
    } else if (notificationItem is NotificationDataModel.CommentLikeNotification) {
        notificationItem.createTime
    } else if (notificationItem is NotificationDataModel.CommentWriteNotification) {
        notificationItem.createTime
    } else {
        ""
    }

    val font = if (notificationItem is NotificationDataModel.FeedLikeNotification) {
        notificationItem.font
    } else if (notificationItem is NotificationDataModel.CommentLikeNotification) {
        notificationItem.font
    } else if (notificationItem is NotificationDataModel.CommentWriteNotification) {
        notificationItem.font
    } else {
        ""
    }

    val fontSize = if (notificationItem is NotificationDataModel.FeedLikeNotification) {
        notificationItem.fontSize
    } else if (notificationItem is NotificationDataModel.CommentLikeNotification) {
        notificationItem.fontSize
    } else if (notificationItem is NotificationDataModel.CommentWriteNotification) {
        notificationItem.fontSize
    } else {
        ""
    }

    val imgUrl = if (notificationItem is NotificationDataModel.FeedLikeNotification) {
        notificationItem.imgUrl
    } else if (notificationItem is NotificationDataModel.CommentLikeNotification) {
        notificationItem.imgUrl
    } else if (notificationItem is NotificationDataModel.CommentWriteNotification) {
        notificationItem.imgUrl
    } else {
        ""
    }
    //리스트 뜨는거 확인
    //근데 계속 새로고침 해야되나..
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isRead) {
                    notificationViewModel.handleNotificationRead(notificationItem.notificationId)
                }
                navController.navigate("${PostNav.Detail.screenRoute}/${targetCardId}")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            ImageLoaderForUrl(imgUrl)
            Text(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .align(Alignment.Center),
                text = content,
                fontSize = 3.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 5.4.sp,
                color = colorResource(R.color.gray_white),
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = if (notificationItem.notificationType == NotificationTypeEnum.COMMENT_WRITE) {
                "${nickname}님이 답카드를 작성하였습니다."
            } else if (notificationItem.notificationType == NotificationTypeEnum.COMMENT_LIKE) {
                "${nickname}님이 카드에 공감하였습니다."
            } else if (notificationItem.notificationType == NotificationTypeEnum.FEED_LIKE) {
                "${nickname}님이 카드에 공감하였습니다."
            } else {
                "${nickname}님이 카드에 공감하였습니다."
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.8.sp,
            color = colorResource(R.color.gray700)
        )
        Spacer(modifier = Modifier.weight(1f))
        Row {
            Text(
                text = formatTimeDifference(createTime),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.8.sp,
                color = colorResource(R.color.gray400)
            )
            if (!isRead) {
                Box(
                    modifier = Modifier
                        .size(6.dp) // 크기 설정
                        .clip(CircleShape) // 동그라미 모양으로 클리핑
                        .background(colorResource(R.color.red)) // 배경색 설정
                )
            }
        }
    }
}

@Composable
fun WarningNotificationElement(notificationViewModel: NotificationViewModel, notificationItem: NotificationDataModel, isRead: Boolean) {
    val warningItem = if (notificationItem.notificationType == NotificationTypeEnum.BLOCKED) {
        notificationItem as NotificationDataModel.BlockedNotification
    } else {
        notificationItem as NotificationDataModel.BlockedNotification
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 18.dp, start = 20.dp, end = 20.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                notificationViewModel.handleNotificationRead(warningItem.notificationId)
                notificationViewModel.removeWaringNotification(warningItem.notificationId)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (warningItem.notificationType == NotificationTypeEnum.BLOCKED) {
                "[정지]  "
            } else if (warningItem.notificationType == NotificationTypeEnum.DELETED) {
                "[삭제]  "
            } else {
                ""
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.8.sp,
            color = colorResource(R.color.red)

        )
        Text(
            text = if (warningItem.notificationType == NotificationTypeEnum.BLOCKED) {
                convertToKoreanDate(warningItem.blockExpirationDateTime!!)
            } else if (warningItem.notificationType == NotificationTypeEnum.DELETED) {
                "신고로 인해 카드가 삭제 처리 되었습니다."
            } else {
                ""
            },
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
            if (!isRead) {
                Box(
                    modifier = Modifier
                        .size(6.dp) // 크기 설정
                        .clip(CircleShape) // 동그라미 모양으로 클리핑
                        .background(colorResource(R.color.red)) // 배경색 설정
                )
            }
        }
    }
}

@Composable
fun NotExistNotification(notificationViewModel: NotificationViewModel) {
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

fun convertToKoreanDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

    return try {
        val date = inputFormat.parse(inputDate)
        if (date != null) {
            outputFormat.format(date)
        } else {
            "날짜 변환 실패"
        }
    } catch (e: Exception) {
        "잘못된 날짜 형식"
    }
}