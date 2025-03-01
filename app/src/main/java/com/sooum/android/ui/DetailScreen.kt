package com.sooum.android.ui

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sooum.android.R
import com.sooum.android.User
import com.sooum.android.domain.model.DetailCardLikeCommentCountDataModel
import com.sooum.android.domain.model.DetailCommentCardDataModel
import com.sooum.android.domain.model.Tag
import com.sooum.android.ui.common.PostNav
import com.sooum.android.ui.common.SooumNav
import com.sooum.android.ui.common.TagNav
import com.sooum.android.ui.theme.Gray1
import com.sooum.android.ui.theme.Gray100
import com.sooum.android.ui.theme.Gray3
import com.sooum.android.ui.theme.Gray300
import com.sooum.android.ui.theme.Gray500
import com.sooum.android.ui.theme.GrayWhite
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.viewmodel.DetailViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DetailScreen(
    navController: NavHostController,
    cardId: String?,
    viewModel: DetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    var lastRefreshTime by remember { mutableStateOf(0L) }
    val latitude = User.userInfo.latitude
    val longitude = User.userInfo.longitude
    LaunchedEffect(Unit) {
        // 서버 호출 (예시로 delay로 가정)
        val currentTime = System.currentTimeMillis()
        lastRefreshTime = currentTime // 초기 로딩 시간 기록
        cardId?.let {
            Log.e("latitude", latitude.toString())
            Log.e("latitude", longitude.toString())
            viewModel.getFeedCard(latitude!!, longitude!!, it.toLong())
            viewModel.getDetailCardLikeCommentCount(it.toLong())
            viewModel.getDetailCommentCard(it.toLong(), latitude, longitude)
        }
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
    Log.d("DetailScreen", "${data?.storyExpirationTime}")
    val comment = viewModel.detailCommentCardDataModel
    var count = viewModel.detailCardLikeCommentCountDataModel//TODO 화면이 계속 리컴포징 돼서 깜빡거림...
    val scrollState = rememberScrollState()
    var isRefreshing by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastRefreshTime >= 1000L) { // 0.5초 간격 제한
                lastRefreshTime = currentTime
                isRefreshing = true
                coroutineScope.launch {
                    cardId?.let {
                        viewModel.getFeedCard(latitude!!, longitude!!, it.toLong())
                        viewModel.getDetailCardLikeCommentCount(it.toLong())
                    }
                    isRefreshing = false
                }
            }
        }
    )
    //  val targetCardId = SooumApplication().getVariable("targetCardId")

    // 뒤로가기 처리
    BackHandler {
        navController.popBackStack()
//        var flag = 0
//        navController.backQueue.forEach { backStackEntry ->
//            Log.d(
//                "BackStack",
//                "Destination: ${backStackEntry.destination.route}"
//            )
//        }
//        navController.backQueue.find { it.destination.route == MyProfile.MyCommentHistory.screenRoute }
//            ?.let {
//                flag = 1
//                navController.navigate(MyProfile.MyCommentHistory.screenRoute) {
//                    popUpTo(MyProfile.MyCommentHistory.screenRoute) {
//                        inclusive = true
//                    }
//                    launchSingleTop = true
//                }
//            }
//        Log.d("BackStack2", "2")
//        // 백스택 팝
//        if (flag == 0) {
//            navController.navigate(SooumNav.Home.screenRoute) {
//                popUpTo(navController.graph.id) {
//                    inclusive = true
//                }
//                launchSingleTop = true
//            }
//        }
//        if (targetCardId != "") {
//            SooumApplication().removeVariable("targetCardId")
//            navController.navigate("main") {
//                popUpTo(0) { inclusive = true } // 그래프의 최상단 루트로 설정
//                launchSingleTop = true
//            }
//        } else {
//
//        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
            },
            navigationIcon = {
                IconButton(onClick = {
                    val previousBackStackEntry = navController.previousBackStackEntry

                    // 나중 백스택 처리용으로 남겨둠
                    previousBackStackEntry?.destination?.route?.let { previousRoute ->
                        Log.e("Navigation", "이전 화면: $previousRoute\n 카드 값 : $cardId")
                        when (previousRoute) {
                            "메인홈" -> {
                                navController.popBackStack()
                            }

                            "프로필" -> {
                                navController.popBackStack()
                            }

                            "덧글 히스토리" -> {
                                navController.popBackStack()
                            }

                           else -> {
                               navController.popBackStack()
                           }
                        }
                    }

//                    var flag = 0
//                    navController.backQueue.forEach { backStackEntry ->
//                        Log.d(
//                            "BackStack",
//                            "Destination: ${backStackEntry.destination.route}"
//                        )
//                    }
//                    navController.backQueue.find { it.destination.route == MyProfile.MyCommentHistory.screenRoute }
//                        ?.let {
//                            flag = 1
//                            navController.navigate(MyProfile.MyCommentHistory.screenRoute) {
//                                popUpTo(MyProfile.MyCommentHistory.screenRoute) {
//                                    inclusive = true
//                                }
//                                launchSingleTop = true
//                            }
//                        }
//                    Log.d("BackStack2", "2")
//                    // 백스택 팝
//                    if (flag == 0) {
//                        navController.navigate(SooumNav.Home.screenRoute) {
//                            popUpTo(navController.graph.id) {
//                                inclusive = true
//                            }
//                            launchSingleTop = true
//                        }
//                    }
//                    if (targetCardId != "") {
//                        SooumApplication().removeVariable("targetCardId")
//                        navController.navigate(SooumNav.Home.screenRoute) {
//                            popUpTo(0) { inclusive = true } // 그래프의 최상단 루트로 설정
//                            launchSingleTop = true
//                        }
//                    } else {
//
//                    }

                }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "home",
                        colorFilter = ColorFilter.tint(colorResource(R.color.black))
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    navController.navigate(SooumNav.Home.screenRoute) {
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
    })
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(it)
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
                            .background(Color(0xFFF8F8F8))
                    ) {
                        if (data != null) {
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
                                            tint = GrayWhite
                                        )
                                    } else {
                                        Icon(
                                            modifier = Modifier
                                                .padding(end = 16.dp)
                                                .clickable { showBottomSheet = true },
                                            painter = painterResource(R.drawable.ic_detail_kebab),
                                            contentDescription = "케밥 더보기 버튼",
                                            tint = GrayWhite
                                        )
                                    }
                                }
                            }
                            ImageLoader(data.backgroundImgUrl.href)
                            if (data.previousCardId != null) {//상위 카드가 있을때
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
                                    onClick = {
//                                        var flag = 0
//                                        navController.backQueue.forEach { backStackEntry ->
//                                            Log.d(
//                                                "BackStack",
//                                                "Destination: ${backStackEntry.destination.route}"
//                                            )
//                                        }
//                                        navController.backQueue.find { it.destination.route == MyProfile.MyCommentHistory.screenRoute }
//                                            ?.let {
//                                                flag = 1
//                                                navController.navigate("${PostNav.Detail.screenRoute}/${data.previousCardId}")
//                                            }
//                                        Log.d("BackStack2", "2")
//                                        // 백스택 팝
//                                        if (flag == 0) {
//                                            navController.popBackStack()
//                                        }
                                        navController.popBackStack()
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                    ) {
                                        if (!data.isPreviousCardDelete) {
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
                                    fontFamily = if (data.font == "SCHOOL_SAFE_CHALKBOARD_ERASER") {
                                        FontFamily(
                                            Font(R.font.handwrite)
                                        )
                                    } else {
                                        FontFamily.Default
                                    }
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0x00000000), // 투명한 검정
                                                Color(0x99000000)  // 약간 불투명한 검정
                                            )
                                        )
                                    )
                                    .align(Alignment.BottomCenter)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(start = 26.dp, end = 26.dp, bottom = 24.dp)
                            ) {
                                Row(modifier = Modifier.align(Alignment.BottomStart)) {
                                    if (data.member.profileImgUrl == null) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_sooum_logo),
                                            contentDescription = "앱 로고",
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable {
                                                    if (data.isOwnCard) {
                                                        navController.navigate(SooumNav.Profile.screenRoute)
                                                    } else {
                                                        navController.navigate("${PostNav.DifProfile.screenRoute}/${data.member.id}")
                                                    }
                                                }
                                        )
                                    } else {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(data.member.profileImgUrl.href)
                                                .build(),
                                            contentDescription = "카드 이미지",
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable {
                                                    if (data.isOwnCard) {
                                                        navController.navigate(SooumNav.Profile.screenRoute)
                                                    } else {
                                                        navController.navigate("${PostNav.DifProfile.screenRoute}/${data.member.id}")
                                                    }
                                                }
                                                .clip(CircleShape)
                                                .aspectRatio(1f),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Text(
                                        text = data.member.nickname,
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight(600),
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .padding(start = 8.dp)
                                    )
                                }//프로필
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .height(32.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
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
                        } else {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .background(Color(0xFFF8F8F8))
                            ) {
                                Icon(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    painter = painterResource(id = R.drawable.ic_delete),
                                    contentDescription = null,
                                    tint = Gray300
                                )
                                Text(
                                    text = "이 글을 삭제되었어요",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Gray500
                                )
                            }
                        }
                    }
                }
                if (data == null || data.tags.isEmpty()) {
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
                            TagItem(item, onClick = { tagId ->
                                navController.navigate("${TagNav.TagList.screenRoute}/${tagId}")
                            })
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
                Divider(
                    color = Gray100,        // 선의 색상
                    thickness = 2.dp           // 선의 두께
                )
                Row(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 10.dp)
                        .align(Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (count != null) {
                        DetailLike(count, viewModel, cardId, data != null)
                        Icon(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .width(24.dp)
                                .height(24.dp)
                                .clickable {
                                    if (data != null) {
                                        navController.navigate("addCommentCard/${cardId}/${data.storyExpirationTime}")
                                    }
                                },
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

                if (comment != null) {
                    if (comment.embedded.commentCardsInfoList.size == 1) {
                        DeatilCommentItem(
                            comment.embedded.commentCardsInfoList[0],
                            navController,
                            Modifier
                                .size(240.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp, bottom = 10.dp)
                        )
                    } else {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .padding(top = 10.dp)
                        ) {
                            items(comment.embedded.commentCardsInfoList.size) { item ->
                                if (item == 0) {
                                    DeatilCommentItem(
                                        comment.embedded.commentCardsInfoList[item],
                                        navController,
                                        Modifier
                                            .aspectRatio(1 / 0.9f)
                                            .padding(start = 20.dp, bottom = 10.dp)
                                    )
                                } else {
                                    DeatilCommentItem(
                                        comment.embedded.commentCardsInfoList[item],
                                        navController,
                                        Modifier
                                            .aspectRatio(1 / 0.9f)
                                            .padding(start = 8.dp, bottom = 10.dp)
                                    )
                                }

                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .height(240.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            "댓글이 아직 없어요",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFB4B4B4),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                }
            }
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = Color.Black
            )
        }
    }
}

@Composable
fun DetailLike(
    count: DetailCardLikeCommentCountDataModel,
    viewModel: DetailViewModel,
    cardId: String?,
    isData: Boolean,
) {
    // 상태 추적을 위해 count의 cardLikeCnt 값을 mutableStateOf로 관리
    var likeState by remember { mutableStateOf(count.isLiked) }
    var likeCount by remember { mutableStateOf(count.cardLikeCnt) }

    if (isData) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                Log.e("cardId", cardId.toString())
                if (likeState) {
                    cardId?.let {
                        viewModel.likeOff(it.toLong())
                        likeCount -= 1
                    }
                } else {
                    cardId?.let {
                        viewModel.likeOn(it.toLong())
                        likeCount += 1
                    }
                }
                likeState = !likeState
            })
        {
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
                text = likeCount.toString(),
                fontSize = 14.sp,
                color = if (likeState) Primary else Color.Black
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeatilCommentItem(
    item: DetailCommentCardDataModel.CommentCardsInfo,
    navHostController: NavHostController,
    modifier: Modifier,
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color.Black.copy(alpha = 0f), Color.Black.copy(alpha = 0.6f)),
        startY = 0f,
        endY = 60f // 그라데이션의 높이를 60dp로 설정
    )
    Card(
        modifier = modifier,
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
                    fontFamily = if (item.font == "SCHOOL_SAFE_CHALKBOARD_ERASER") {
                        FontFamily(
                            Font(R.font.handwrite)
                        )
                    } else {
                        FontFamily.Default
                    }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x00000000), // 투명한 검정
                                Color(0x99000000)  // 약간 불투명한 검정
                            )
                        )
                    )
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
fun TagItem(item: Tag, onClick: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .padding(end = 10.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick(item.id)
            },
        shape = RoundedCornerShape(4.dp),
        color = Gray3
    ) {
        Text(
            text = "#${item.content}",
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
                            navController.navigate(SooumNav.Home.screenRoute) {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
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
                    text = "해당 사용자의 모든 카드를 모두 볼 수 없어요",
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
                            navController.navigate(SooumNav.Home.screenRoute) {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
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
