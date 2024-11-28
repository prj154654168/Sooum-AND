package com.sooum.android.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.User
import com.sooum.android.domain.model.DetailCardLikeCommentCountDataModel
import com.sooum.android.domain.model.DetailCommentCardDataModel
import com.sooum.android.domain.model.Tag
import com.sooum.android.ui.common.PostNav
import com.sooum.android.ui.common.SoonumNav
import com.sooum.android.ui.theme.Gray1
import com.sooum.android.ui.theme.Gray3
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.viewmodel.DetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DetailScreen(
    navController: NavHostController,
    cardId: String?,
    viewModel: DetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    var latitude = User.userInfo.latitude
    var longitude = User.userInfo.longitude
    cardId?.let {
        Log.e("latitude", latitude.toString())
        Log.e("latitude", longitude.toString())
        viewModel.getFeedCard(latitude!!, longitude!!, it.toLong())
        viewModel.getDetailCardLikeCommentCount(it.toLong())
        viewModel.getDetailCommentCard(it.toLong(), latitude!!, longitude!!)
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    if (showBlockDialog) {
        cardId?.let {
            BlockDialog(navController, it.toLong(), viewModel) {
                showBlockDialog = false
            }
        }
    }
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        cardId?.let { DeleteDialog(navController, it.toLong(), viewModel) { showDialog = false } }
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color.Black.copy(alpha = 0f), Color.Black.copy(alpha = 0.6f)),
        startY = 0f,
        endY = 60f // 그라데이션의 높이를 60dp로 설정
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            // Sheet content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 66.dp)//네비게이션 크기만큼
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(66.dp)
                        .clickable {
                            scope
                                .launch { sheetState.hide() }
                                .invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            showBlockDialog = true
                        }
                ) {
                    Text(
                        text = "차단하기",
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(66.dp)
                        .clickable {
                            scope
                                .launch { sheetState.hide() }
                                .invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            navController.navigate("${PostNav.Report.screenRoute}/${cardId}")
                        }
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "신고하기",
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )

                }
            }
        }
    } //bottom sheet
    val data = viewModel.feedCardDataModel
    val comment = viewModel.detailCommentCardDataModel
    var count = viewModel.detailCardLikeCommentCountDataModel//TODO 화면이 계속 리컴포징 돼서 깜빡거림...

    var isRefreshing by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            coroutineScope.launch {
                delay(1000)
                cardId?.let {
                    viewModel.getFeedCard(latitude!!, longitude!!, it.toLong())
                    viewModel.getDetailCardLikeCommentCount(it.toLong())
                }
                isRefreshing = false
            }

        }
    )

    if (data != null) {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile_logo),
                            contentDescription = "앱 로고",
                            modifier = Modifier
                                .width(32.dp)
                                .height(32.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = data.member.nickname,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(SoonumNav.Home.screenRoute) {
                            // 모든 Back Stack을 비우고 "destination_screen"으로 이동
                            popUpTo(navController.graph.id) {
                                inclusive = true // "startDestinationId"까지 포함하여 모든 화면을 제거
                            }
                            launchSingleTop = true // 이미 존재하는 화면은 새로 시작하지 않음
                        }
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_home),
                            contentDescription = "home",
                            colorFilter = ColorFilter.tint(colorResource(R.color.black))
                        )
                    }
                },
                modifier = Modifier.padding(
                    horizontal = 4.dp,
                    vertical = 2.dp
                )
            )
        }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {

                Column(
                    modifier = Modifier
                        .padding(it)
                        .verticalScroll(scrollState)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1 / 0.9f)
                            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp, top = 10.dp),
                        shape = RoundedCornerShape(40.dp),
                        onClick = { }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(0.25f)
                                    .align(Alignment.TopCenter)
                                    .zIndex(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                ) {
                                    if (data.storyExpirationTime != null) {
                                        if (calculateRemainingTime(data.storyExpirationTime) != "시간이 이미 지났습니다.") {
                                            PungTime(calculateRemainingTime(data.storyExpirationTime))
                                        }
                                    }
                                }

                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(0.25f)
                                    .align(Alignment.TopEnd)
                                    .zIndex(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                ) {
                                    if (data.isOwnCard) {
                                        Icon(
                                            modifier = Modifier
                                                .padding(end = 16.dp)
                                                .clickable { showDialog = true },
                                            painter = painterResource(R.drawable.ic_detail_delete),
                                            contentDescription = "케밥 더보기 버튼",
                                        )
                                    } else {
                                        Icon(
                                            modifier = Modifier
                                                .padding(end = 16.dp)
                                                .clickable { showBottomSheet = true },
                                            painter = painterResource(R.drawable.ic_detail_kebab),
                                            contentDescription = "케밥 더보기 버튼",
                                        )
                                    }
                                }
                            }

                            ImageLoader(data.backgroundImgUrl.href)
                            if (data.previousCardId != null) {
                                Card(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(
                                            start = 20.dp,
                                            top = 20.dp
                                        )
                                        .height(50.dp)
                                        .width(50.dp)
                                        .border(
                                            BorderStroke(2.dp, Color.White), // 흰색 테두리
                                            shape = RoundedCornerShape(16.dp) // 테두리 모양을 Card의 shape에 맞춤
                                        ),
                                    shape = RoundedCornerShape(40.dp),
                                    onClick = { navController.popBackStack() }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                    ) {
                                        if (!data.isParentDeleted) {
                                            ImageLoader(data.previousCardImgLink!!.href.toString())
                                            Text(
                                                "전글",
                                                color = Color.White,
                                                modifier = Modifier.align(
                                                    Alignment.Center
                                                ),
                                                fontSize = 14.sp
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Gray3)
                                            )
                                            Text(
                                                "삭제됨",
                                                color = Color.White,
                                                modifier = Modifier.align(
                                                    Alignment.Center
                                                ),
                                                fontSize = 14.sp
                                            )
                                        }

                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        Color.Black.copy(alpha = 0.7f),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .fillMaxWidth(0.75f)
                                    .align(Alignment.Center)
                                    .padding(4.dp)
                            ) {

                                Text(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 14.dp,
                                            bottom = 14.dp
                                        ),
                                    text = data.content,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 28.8.sp,
                                )

                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .background(gradientBrush)
                                    .align(Alignment.BottomCenter)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 26.dp, bottom = 24.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (data.distance != 0.0) {
                                        InfoElement(
                                            painter = painterResource(R.drawable.ic_location),
                                            description = "위치",
                                            count = formatDistanceInKm(data.distance),
                                            isTrue = false
                                        )
                                    }

                                    InfoElement(
                                        painter = painterResource(R.drawable.ic_clock),
                                        description = "시간",
                                        count = formatTimeDifference(data.createdAt),
                                        isTrue = false
                                    )
                                }

                            }
                        }
                    }
                    if (data.tags.isEmpty()) {
                        // 태그가 없을 때 기본 패딩 추가
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, bottom = 10.dp)
                                .height(30.dp) // 원하는 패딩 크기 설정
                        )
                    } else {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, bottom = 10.dp),
                        ) {
                            items(data.tags) { item ->
                                TagItem(item)
                            }
                        }
                    }

//            Box(
//                //color = Color.Black,
//                modifier = Modifier
//                    .height(50.dp)
//                    .fillMaxWidth()
//                    .padding(10.dp)
//            ){
//                Row(modifier = Modifier.background(Color.Black)){}
//            }
                    Row(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .align(Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (count != null) {
                            DetailLike(count, viewModel, cardId)
                            Icon(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .width(24.dp)
                                    .height(24.dp),
                                painter = painterResource(R.drawable.ic_detail_comment),
                                contentDescription = "댓글",
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = count.commentCnt.toString(),
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                    }
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        if (comment != null) {
                            items(comment.embedded.commentCardsInfoList) { item ->
                                DeatilCommentItem(item, navController)
                            }
                        }
                    }
                }
                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentColor = Primary
                )
            }

        }

    }

}

@Composable
fun DetailLike(
    count: DetailCardLikeCommentCountDataModel,
    viewModel: DetailViewModel,
    cardId: String?,
) {
    var likeState by remember { mutableStateOf(count.isLiked) }

    Row(modifier = Modifier.clickable {
        Log.e(
            "cardId",
            cardId.toString()
        )
        if (likeState) {
            cardId?.let { viewModel.likeOff(it.toLong()) }
        } else {
            cardId?.let { viewModel.likeOn(it.toLong()) }
        }
        likeState = !likeState

    }) {
        Icon(
            modifier = Modifier
                .width(24.dp)
                .height(24.dp),
            painter = if (likeState) painterResource(R.drawable.ic_heart_filled) else painterResource(
                R.drawable.ic_detail_heart
            ),
            contentDescription = "좋아요",
            tint = if (likeState) Primary else Color.Black
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = count.cardLikeCnt.toString(),
            fontSize = 14.sp,
            color = if (likeState) Primary else Color.Black
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeatilCommentItem(
    item: DetailCommentCardDataModel.CommentCardsInfo,
    navHostController: NavHostController,
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color.Black.copy(alpha = 0f), Color.Black.copy(alpha = 0.6f)),
        startY = 0f,
        endY = 60f // 그라데이션의 높이를 60dp로 설정
    )
    Card(
        modifier = Modifier
            .aspectRatio(1 / 0.9f)
            .padding(start = 10.dp, bottom = 10.dp),
        shape = RoundedCornerShape(40.dp),
        onClick = { navHostController.navigate("${PostNav.Detail.screenRoute}/${item.id}") }
    ) {
        Box(
            modifier = Modifier
        ) {
            ImageLoader(item.backgroundImgUrl.href)
            Box(
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .align(Alignment.Center)
                    .padding(4.dp)
            ) {

                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
                    text = item.content,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 28.8.sp,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(gradientBrush)
                    .align(Alignment.BottomCenter)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 26.dp, bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoElement(
                        painter = painterResource(R.drawable.ic_clock),
                        description = "시간",
                        count = formatTimeDifference(item.createdAt),
                        isTrue = false
                    )
                    if (item.distance != null) {
                        InfoElement(
                            painter = painterResource(R.drawable.ic_location),
                            description = "위치",
                            count = formatDistanceInKm(item.distance),
                            isTrue = false
                        )
                    }
                    InfoElement(
                        painter = painterResource(R.drawable.ic_heart),
                        description = "좋아요",
                        count = item.likeCnt.toString(),
                        isTrue = false
                    )
                    InfoElement(
                        painter = painterResource(R.drawable.ic_comment),
                        description = "댓글",
                        count = item.commentCnt.toString(),
                        isTrue = false
                    )
                }
            }
        }
    }
}

@Composable
fun TagItem(item: Tag) {
    Surface(
        modifier = Modifier.padding(end = 10.dp),
        shape = RoundedCornerShape(4.dp),
        color = Gray3
    ) {
        Text(
            text = item.content,
            fontSize = 14.sp,
            color = Gray1,
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 4.dp)
        )
    }
}

@Composable
fun DeleteDialog(
    navController: NavHostController,
    cardId: Long,
    viewModel: DetailViewModel,
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
                Text(
                    text = "카드를 삭제할까요?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.black)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "삭제된 카드는 복구할 수 없어요",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.gray01)
                )
                Spacer(modifier = Modifier.height(22.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            showDialog()
                        },
                        modifier = Modifier
                            .width(130.dp)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.gray03)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "취소",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.deleteCard(cardId)
                            navController.popBackStack()//TODO 추후 삭제화면 보이게 해야함.
                        },
                        modifier = Modifier
                            .width(130.dp)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primary_color)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "삭제하기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BlockDialog(
    navController: NavHostController,
    cardId: Long,
    viewModel: DetailViewModel,
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
                Text(
                    text = "해당 사용자를 차단할까요?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.black)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "해당 사용자의 모든 카들르 모두 볼수 없어요",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.gray01)
                )
                Spacer(modifier = Modifier.height(22.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            showDialog()
                        },
                        modifier = Modifier
                            .width(130.dp)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.gray03)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "취소",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.userBlocks()
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .width(130.dp)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primary_color)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "차단하기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}