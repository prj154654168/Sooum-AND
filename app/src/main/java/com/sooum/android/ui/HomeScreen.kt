package com.sooum.android.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sooum.android.R
import com.sooum.android.User
import com.sooum.android.domain.model.SortedByDistanceDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.SortedByPopularityDataModel
import com.sooum.android.enums.DistanceEnum
import com.sooum.android.enums.HomeSelectEnum
import com.sooum.android.ui.common.NotificationNav
import com.sooum.android.ui.common.PostNav
import com.sooum.android.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.system.exitProcess

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavHostController) {

    // 뒤로가기 2번 눌렀을 때 앱 종료
    BackPressExitHandler()

    // 화면에서 스크롤 위/아래에 따라 헤더(필터 등)를 보이거나 숨김
    var isVisible by remember { mutableStateOf(true) }

    val homeViewModel: HomeViewModel = hiltViewModel()

    // 위치/거리 관련 State
    var latitude = User.userInfo.latitude
    var longitude = User.userInfo.longitude

    Log.d("HomeScreen", "${latitude}, ${longitude}")

    // 다이얼로그 state
    var openLocationDialog by remember { mutableStateOf(false) }
    var openSystemLocationDialog by remember { mutableStateOf(false) }

    // 각각의 리스트(LazyColumn) 상태 (최신/인기/거리)
    val latestScrollState = rememberLazyListState()
    val popularityScrollState = rememberLazyListState()
    val distanceScrollState = rememberLazyListState()

    // 스크롤 위치가 특정 조건 이상이면 "맨 위로" 버튼 보이기
    val showMoveToTopButtonForLatest by remember {
        derivedStateOf {
            latestScrollState.firstVisibleItemIndex > 0 || latestScrollState.firstVisibleItemScrollOffset > 0
        }
    }
    val showMoveToTopButtonForPopularity by remember {
        derivedStateOf {
            popularityScrollState.firstVisibleItemIndex > 0 || popularityScrollState.firstVisibleItemScrollOffset > 0
        }
    }
    val showMoveToTopButtonForDistance by remember {
        derivedStateOf {
            distanceScrollState.firstVisibleItemIndex > 0 || distanceScrollState.firstVisibleItemScrollOffset > 0
        }
    }

    // 이전 인덱스 추적 -> 스크롤 아래로 내리면 헤더 숨김, 위로 올리면 헤더 보임
    var latestPreviousIndex by remember { mutableStateOf(0) }
    var popularityPreviousIndex by remember { mutableStateOf(0) }
    var distancePreviousIndex by remember { mutableStateOf(0) }

    // 거리 필터 값
    var distance by remember { mutableStateOf(DistanceEnum.UNDER_1) }

    // 코루틴 스코프 (Pager 이동 시 애니메이션을 위해)
    val coroutineScope = rememberCoroutineScope()

    // 페이지 수: 최신(0), 인기(1), 거리(2) 총 3개
    val pagerState = rememberPagerState(
        initialPage = HomeSelectEnum.LATEST.ordinal,
        pageCount = { 3 }
    )

    // 스크롤 상태별 헤더 표시/숨김 로직
    LaunchedEffect(latestScrollState) {
        snapshotFlow { latestScrollState.firstVisibleItemIndex }
            .collect { currentIndex ->
                isVisible = (currentIndex <= latestPreviousIndex)
                latestPreviousIndex = currentIndex
            }
    }
    LaunchedEffect(popularityScrollState) {
        snapshotFlow { popularityScrollState.firstVisibleItemIndex }
            .collect { currentIndex ->
                isVisible = (currentIndex <= popularityPreviousIndex)
                popularityPreviousIndex = currentIndex
            }
    }
    LaunchedEffect(distanceScrollState) {
        snapshotFlow { distanceScrollState.firstVisibleItemIndex }
            .collect { currentIndex ->
                isVisible = (currentIndex <= distancePreviousIndex)
                distancePreviousIndex = currentIndex
            }
    }

    // Pager가 이동 완료될 때마다 툴바(헤더) 보이기
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect {
                // 페이지가 바뀌었으니 툴바 다시 보이기
                isVisible = true
            }
    }


    LaunchedEffect(Unit) {
        homeViewModel.fetchUnreadNotificationCount()
    }

    val context = LocalContext.current

    // pagerState.currentPage가 바뀔 때마다 실행
    LaunchedEffect(pagerState.currentPage) {
        // "인기순" 탭에 진입 시, 데이터가 없다면 최초 fetch
        if (pagerState.currentPage == HomeSelectEnum.POPULARITY.ordinal &&
            homeViewModel.popularityCardList.isEmpty()
        ) {
            homeViewModel.fetchPopularityCardList(latitude, longitude) {
                // fetch 완료 콜백
            }
        }

        // "거리순" 탭으로 이동 시, 아직 위치값이 없다면 위치 다이얼로그 오픈
        if (pagerState.currentPage == HomeSelectEnum.DISTANCE.ordinal &&
            (latitude == null || longitude == null)
        ) {
            openLocationDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .padding(top = 64.dp)
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().
                padding(top = 15.dp, bottom = 15.dp, start = 20.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = null
                )
                Image(painter = if (homeViewModel.unreadNotificationCount.value == 0) painterResource(R.drawable.ic_alarm)
                    else painterResource(R.drawable.ic_alarm_2),
                    contentDescription = null,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        navController.navigate(NotificationNav.Notification.screenRoute)
                    })
            }
            // 상단 탭 + 거리 필터 (isVisible 상태에 따라 보이거나 숨김)
            AnimatedVisibility(
                visible = isVisible
            ) {
                Column {
                    // 최신 / 인기 / 거리 탭 UI
                    HomeSelect(
                        selected = HomeSelectEnum.values()[pagerState.currentPage],
                        onSelectedChange = { newSelectedEnum ->
                            coroutineScope.launch {
                                // animateScrollToPage -> scrollToPage 로 변경
                                pagerState.scrollToPage(newSelectedEnum.ordinal)
                            }
                        }
                    )

                    Divider(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                    )

                    // "거리 순"일 때만 거리 필터 표시
                    if (pagerState.currentPage == HomeSelectEnum.DISTANCE.ordinal) {
                        LocationFilter(
                            distance = distance,
                            onDistanceChange = { newDistance ->
                                distance = newDistance
                            }
                        )
                    }
                }
            }

            // pageIndex에 따라 3가지 화면(최신 / 인기 / 거리)을 보여줌
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                when (pageIndex) {
                    HomeSelectEnum.LATEST.ordinal -> {
                        LatestFeedList(
                            navController = navController,
                            homeViewModel = homeViewModel,
                            scrollState = latestScrollState,
                            showMoveToTopButton = showMoveToTopButtonForLatest
                        )
                    }

                    HomeSelectEnum.POPULARITY.ordinal -> {
                        PopularityFeedList(
                            navController = navController,
                            homeViewModel = homeViewModel,
                            scrollState = popularityScrollState,
                            showMoveToTopButton = showMoveToTopButtonForPopularity
                        )
                    }

                    HomeSelectEnum.DISTANCE.ordinal -> {
                        DistanceFeedList(
                            navController = navController,
                            homeViewModel = homeViewModel,
                            scrollState = distanceScrollState,
                            showMoveToTopButton = showMoveToTopButtonForDistance,
                            distance = distance
                        )
                    }
                }
            }

            // 위치 설정 다이얼로그
            if (openLocationDialog) {
                LocationDialog(
                    openLocationDialog = { isOpen ->
                        openLocationDialog = isOpen
                    },
                    onLocationResulted = { isGrant ->
                        // 권한 허용 시 openSystemLocationDialog = true
                        openSystemLocationDialog = isGrant
                    }
                )
            }

            // 시스템 위치 설정 다이얼로그
            if (openSystemLocationDialog) {
                if (
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    // 권한 재요청
                    GetUserLocation { location ->
                        latitude = location?.latitude
                        longitude = location?.longitude
                    }
                } else {
                    // 사용자가 "다시 묻지 않음"을 체크한 경우, 설정 화면으로 유도
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LatestFeedList(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    scrollState: LazyListState,
    showMoveToTopButton: Boolean
) {
    val coroutineScope = rememberCoroutineScope()

    var isRefreshing by remember { mutableStateOf(false) }

    val lazyLatestFeed = homeViewModel.lazyLatestFeed.collectAsLazyPagingItems()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            lazyLatestFeed.refresh()
        }
    )

    LaunchedEffect(lazyLatestFeed.loadState.refresh) {
        delay(300)
        if (lazyLatestFeed.loadState.refresh !is LoadState.Loading) {
            isRefreshing = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {
        if (lazyLatestFeed.itemCount == 0) {
            ReplaceHomeList()
        } else {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(count = lazyLatestFeed.itemCount,
                    key = lazyLatestFeed.itemKey { it.id }) { index ->
                    val feedItem = lazyLatestFeed[index]
                    feedItem?.let {
                        LatestContentCard(it, navController)
                    }
                }
            }

            if (showMoveToTopButton) {
                Box(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        coroutineScope.launch {
                            scrollState.animateScrollToItem(0)
                        }
                    }
                ) {
                    MoveToTop()
                }
            }
            RefreshIndicator(Modifier.align(Alignment.TopCenter), pullRefreshState, isRefreshing)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PopularityFeedList(
    navController: NavController,
    homeViewModel: HomeViewModel,
    scrollState: LazyListState,
    showMoveToTopButton: Boolean
) {
    val coroutineScope = rememberCoroutineScope()

    var isRefreshing by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            homeViewModel.fetchPopularityCardList(User.userInfo.latitude, User.userInfo.longitude) {
                isRefreshing = false
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {
        if (homeViewModel.popularityCardList.isEmpty()) {
            //스크롤 가능한 영역(VerticalScroll)으로 감싸주기
            //    => 이를 통해 Pull-to-Refresh 제스처를 인식할 수 있게 함
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReplaceHomeList()
            }
        } else {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .pullRefresh(pullRefreshState)
                    .fillMaxSize()
            ) {
                items(homeViewModel.popularityCardList) { item ->
                    PopularityContentCard(item, navController)
                }
            }

            if (showMoveToTopButton) {
                Box(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        coroutineScope.launch {
                            scrollState.animateScrollToItem(0)
                        }
                    }
                ) {
                    MoveToTop()
                }
            }
        }
        RefreshIndicator(Modifier.align(Alignment.TopCenter), pullRefreshState, isRefreshing)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DistanceFeedList(
    navController: NavController,
    homeViewModel: HomeViewModel,
    scrollState: LazyListState,
    showMoveToTopButton: Boolean,
    distance: DistanceEnum,
) {
    val coroutineScope = rememberCoroutineScope()

    // distance 값에 따라 Flow를 한 번만 생성 (재composition 시 재생성 방지)
    val lazyDistanceFeedFlow = remember(distance) {
        homeViewModel.getLazyDistanceFeed(distance)
    }
    val lazyDistanceFeed = lazyDistanceFeedFlow.collectAsLazyPagingItems()

    // Loading 여부를 체크
    var isRefreshing by remember { mutableStateOf(false) }

    // Pull-to-Refresh 상태 관리
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            // 이미 로딩중이면 refresh 호출을 방지
            if (lazyDistanceFeed.loadState.refresh !is LoadState.Loading) {
                isRefreshing = true
                lazyDistanceFeed.refresh()
            }
        }
    )

    // refresh 상태 변화 감지 (Paging3 loadState.refresh)
    LaunchedEffect(lazyDistanceFeed) {
        snapshotFlow { lazyDistanceFeed.loadState.refresh }
            .collect { refreshState ->
                delay(300)
                // 로딩이 종료되면 isRefreshing=false
                if (refreshState !is LoadState.Loading) {
                    isRefreshing = false
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {
        // 데이터가 없는지 확인
        if (lazyDistanceFeed.itemCount == 0) {
            //스크롤 가능한 영역(VerticalScroll)으로 감싸주기
            //    => 이를 통해 Pull-to-Refresh 제스처를 인식할 수 있게 함
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 기존 ReplaceHomeList() 내용
                ReplaceHomeList()
            }
        } else {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(lazyDistanceFeed.itemCount) { index ->
                    val feedItem = lazyDistanceFeed[index]
                    feedItem?.let {
                        DistanceContentCard(it, navController)
                    }
                }
            }
            if (showMoveToTopButton) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 120.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            coroutineScope.launch {
                                scrollState.animateScrollToItem(0)
                            }
                        }
                ) {
                    MoveToTop()
                }
            }
        }

        RefreshIndicator(
            state = pullRefreshState,
            refreshing = isRefreshing,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }


//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        if ((distance == DistanceEnum.UNDER_1 && lazyDistance1Feed.itemCount == 0) ||
//            (distance == DistanceEnum.UNDER_5 && lazyDistance5Feed.itemCount == 0) ||
//            (distance == DistanceEnum.UNDER_10 && lazyDistance10Feed.itemCount == 0) ||
//            (distance == DistanceEnum.UNDER_20 && lazyDistance20Feed.itemCount == 0) ||
//            (distance == DistanceEnum.UNDER_50 && lazyDistance50Feed.itemCount == 0)
//        ) {
//            ReplaceHomeList()
//        } else {
//            LazyColumn(
//                state = scrollState,
//                modifier = Modifier.pullRefresh(pullRefreshState)
//            ) {
//                when (distance) {
//                    DistanceEnum.UNDER_1 -> {
//                        items(lazyDistance1Feed.itemCount) { index ->
//                            val feedItem = lazyDistance1Feed[index]
//                            feedItem?.let {
//                                DistanceContentCard(it, navController)
//                            }
//                        }
//                    }
//
//                    DistanceEnum.UNDER_5 -> {
//                        items(lazyDistance5Feed.itemCount) { index ->
//                            val feedItem = lazyDistance5Feed[index]
//                            feedItem?.let {
//                                DistanceContentCard(it, navController)
//                            }
//                        }
//                    }
//
//                    DistanceEnum.UNDER_10 -> {
//                        items(lazyDistance10Feed.itemCount) { index ->
//                            val feedItem = lazyDistance10Feed[index]
//                            feedItem?.let {
//                                DistanceContentCard(it, navController)
//                            }
//                        }
//                    }
//
//                    DistanceEnum.UNDER_20 -> {
//                        items(lazyDistance20Feed.itemCount) { index ->
//                            val feedItem = lazyDistance20Feed[index]
//                            feedItem?.let {
//                                DistanceContentCard(it, navController)
//                            }
//                        }
//                    }
//
//                    DistanceEnum.UNDER_50 -> {
//                        items(lazyDistance50Feed.itemCount) { index ->
//                            val feedItem = lazyDistance50Feed[index]
//                            feedItem?.let {
//                                DistanceContentCard(it, navController)
//                            }
//                        }
//                    }
//                }
//            }
//            if (showMoveToTopButton) {
//                Box(modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(bottom = 120.dp)
//                    .clickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) {
//                        coroutineScope.launch {
//                            scrollState.animateScrollToItem(0)
//                        }
//                    }
//                ) {
//                    MoveToTop()
//                }
//            }
//            RefreshIndicator(Modifier.align(Alignment.TopCenter), pullRefreshState, isRefreshing)
//        }
//    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RefreshIndicator(
    modifier: Modifier = Modifier,
    state: PullRefreshState,
    refreshing: Boolean,
) {
    Surface(
        modifier = modifier
            .size(40.dp)
            .pullRefreshIndicatorTransform(state, true),
        shape = CircleShape,
    ) {
        if (refreshing) {
            val transition = rememberInfiniteTransition()
            val degree by transition.animateFloat(
                initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1000,
                        easing = LinearEasing
                    )
                ), label = ""
            )
            Image(
                modifier = Modifier
                    .rotate(-degree)
                    .size(40.dp),
                painter = painterResource(R.drawable.ic_refresh_circle),
                contentDescription = "indicator"
            )
        } else {
            Image(
                modifier = Modifier
                    .rotate(-state.progress * 180)
                    .size(40.dp),
                painter = painterResource(R.drawable.ic_refresh_circle),
                contentDescription = "indicator"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LatestContentCard(
    item: SortedByLatestDataModel.Embedded.LatestFeedCard,
    navController: NavHostController,
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color.Black.copy(alpha = 0f), Color.Black.copy(alpha = 0.6f)),
        startY = 0f,
        endY = 60f // 그라데이션의 높이를 60dp로 설정
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1 / 0.9f)
            .padding(start = 20.dp, end = 20.dp, top = 10.dp),
        shape = RoundedCornerShape(40.dp),
        onClick = { navController.navigate("${PostNav.Detail.screenRoute}/${item.id}") }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.25f)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(1f)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    if (item.storyExpirationTime != null) {
                        if (calculateRemainingTime(item.storyExpirationTime) != "시간이 이미 지났습니다.") {
                            PungTime(calculateRemainingTime(item.storyExpirationTime))
                        }
                    }
                }
            }
            ImageLoader(item.backgroundImgUrl.href)
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
                LatestCardInfo(item)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PopularityContentCard(
    item: SortedByPopularityDataModel.Embedded.PopularFeedCard,
    navController: NavController,
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color.Black.copy(alpha = 0f), Color.Black.copy(alpha = 0.6f)),
        startY = 0f,
        endY = 60f // 그라데이션의 높이를 60dp로 설정
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1 / 0.9f)
            .padding(start = 20.dp, end = 20.dp, top = 10.dp),
        shape = RoundedCornerShape(40.dp),
        onClick = {
            navController.navigate("${PostNav.Detail.screenRoute}/${item.id}")
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.25f)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(1f)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    if (item.storyExpiredTime != null) {
                        if (calculateRemainingTime(item.storyExpiredTime) != "시간이 이미 지났습니다.") {
                            PungTime(calculateRemainingTime(item.storyExpiredTime))
                        }
                    }
                }
            }
            ImageLoader(item.backgroundImgUrl.href)
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
                        .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
                    text = item.content,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = if (item.font == "SCHOOL_SAFE_CHALKBOARD_ERASER") {
                        FontFamily(
                            Font(R.font.handwrite)
                        )
                    } else {
                        FontFamily.Default
                    },
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
                PopularityCardInfo(item)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DistanceContentCard(
    item: SortedByDistanceDataModel.Embedded.DistanceFeedCard,
    navController: NavController,
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color.Black.copy(alpha = 0f), Color.Black.copy(alpha = 0.6f)),
        startY = 0f,
        endY = 60f // 그라데이션의 높이를 60dp로 설정
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1 / 0.9f)
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
        shape = RoundedCornerShape(40.dp),
        onClick = {
            navController.navigate("${PostNav.Detail.screenRoute}/${item.id}")
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.25f)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(1f)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    if (item.storyExpirationTime != null) {
                        if (calculateRemainingTime(item.storyExpirationTime) != "시간이 이미 지났습니다.") {
                            PungTime(calculateRemainingTime(item.storyExpirationTime))
                        }
                    }
                }
            }
            ImageLoader(item.backgroundImgUrl.href)
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
                DistanceCardInfo(item)
            }
        }
    }
}

@Composable
fun ImageLoader(url: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .build(),
        contentDescription = "카드 이미지",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateRemainingTime(inputTime: String): String {
    // 입력 시간의 형식 정의
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")

    // 입력 시간을 LocalDateTime으로 파싱
    val targetTime = LocalDateTime.parse(inputTime, formatter)

    // 현재 시간
    val currentTime = LocalDateTime.now()

    // 두 시간 간의 차이 계산
    val duration = Duration.between(currentTime, targetTime)

    return if (duration.isNegative) {
        "시간이 이미 지났습니다."
    } else {
        // 남은 시간 계산
        val days = duration.toDays()
        val hours = parseNumber(duration.toHours() % 24)
        val minutes = parseNumber(duration.toMinutes() % 60)
        val seconds = parseNumber(duration.seconds % 60)
        "$hours : $minutes : $seconds"
    }
}

fun parseNumber(number: Long): String {
    return if (number < 10) "0${number}"
    else number.toString()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PungTime(time: String) {

    var currentTime by remember {
        mutableStateOf(
            LocalTime.parse(
                time,
                DateTimeFormatter.ofPattern("HH : mm : ss")
            )
        )
    }
    val formatter = DateTimeFormatter.ofPattern("HH : mm : ss")

    LaunchedEffect(currentTime) {
        while (currentTime > LocalTime.MIN) { // 00:00:00이 될 때까지 실행
            delay(1000L) // 1초 대기
            currentTime = currentTime.minusSeconds(1) // 1초 감소
        }
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colorResource(R.color.primary_color)
    ) {
        Text(
            text = currentTime.format(formatter),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LatestCardInfo(item: SortedByLatestDataModel.Embedded.LatestFeedCard) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CreatedTimeElement(item.createdAt)
        if (item.distance != null) DistanceElement(item.distance)
        LikeElement(item.isLiked, item.likeCnt)
        CommentElement(item.isCommentWritten, item.commentCnt)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PopularityCardInfo(item: SortedByPopularityDataModel.Embedded.PopularFeedCard) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LikeElement(item.isLiked, item.likeCnt)
        CommentElement(item.isCommentWritten, item.commentCnt)
        CreatedTimeElement(item.createdAt)
        if (item.distance != null) DistanceElement(item.distance)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DistanceCardInfo(item: SortedByDistanceDataModel.Embedded.DistanceFeedCard) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DistanceElement(item.distance)
        CreatedTimeElement(item.createdAt)
        LikeElement(item.isLiked, item.likeCnt)
        CommentElement(item.isCommentWritten, item.commentCnt)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreatedTimeElement(createdTime: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .width(12.dp)
                .height(12.dp),
            painter = painterResource(R.drawable.ic_clock),
            contentDescription = null,
            tint = colorResource(R.color.gray_white)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = formatTimeDifference(createdTime),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.8.sp,
            color = colorResource(R.color.gray_white)
        )
    }
}

@Composable
fun DistanceElement(distance: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .width(12.dp)
                .height(12.dp),
            painter = painterResource(R.drawable.ic_location),
            contentDescription = null,
            tint = colorResource(R.color.gray_white)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = formatDistanceInKm(distance),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.8.sp,
            color = colorResource(R.color.gray_white)
        )
    }
}

@Composable
fun LikeElement(isLiked: Boolean, likeCount: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .width(12.dp)
                .height(12.dp),
            painter = if (isLiked) {
                painterResource(R.drawable.ic_heart_filled)
            } else {
                painterResource(R.drawable.ic_heart)
            },
            contentDescription = null,
            tint = if (isLiked) {
                colorResource(R.color.primary_color)
            } else {
                colorResource(R.color.gray_white)
            }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = likeCount.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.8.sp,
            color = if (isLiked) {
                colorResource(R.color.primary_color)
            } else {
                colorResource(R.color.gray_white)
            }
        )
    }
}

@Composable
fun CommentElement(isCommentWritten: Boolean, commentCount: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .width(12.dp)
                .height(12.dp),
            painter = if (isCommentWritten) {
                painterResource(R.drawable.ic_comment_filled)
            } else {
                painterResource(R.drawable.ic_comment)
            },
            contentDescription = null,
            tint = if (isCommentWritten) {
                colorResource(R.color.primary_color)
            } else {
                colorResource(R.color.gray_white)
            }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = commentCount.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.8.sp,
            color = if (isCommentWritten) {
                colorResource(R.color.primary_color)
            } else {
                colorResource(R.color.gray_white)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTimeDifference(timeString: String): String {
    // 문자열을 LocalDateTime으로 변환
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val eventTime = LocalDateTime.parse(timeString, formatter)
    val currentTime = LocalDateTime.now()

    // 시간 차이 계산
    val minutesDifference = ChronoUnit.MINUTES.between(eventTime, currentTime)
    val hoursDifference = ChronoUnit.HOURS.between(eventTime, currentTime)
    val daysDifference = ChronoUnit.DAYS.between(eventTime, currentTime)

    return when {
        minutesDifference < 5 -> "조금 전"
        minutesDifference < 10 -> "10분 전"
        minutesDifference < 60 -> {
            val roundedMinutes = (minutesDifference / 10) * 10
            "${roundedMinutes}분 전"
        }

        hoursDifference < 24 -> "${hoursDifference}시간 전"
        daysDifference < 10 -> "${daysDifference}일 전"
        daysDifference < 100 -> "${(daysDifference / 10) * 10}일 전"
        daysDifference < 365 -> "${(daysDifference / 100) * 100}일 전"
        else -> "${(daysDifference / 365)}년 전"
    }
}

fun formatDistanceInKm(distance: Double): String {
    return when {
        distance == 0.0 -> "100m 이내" // 0일경우
        distance < 0.1 -> "100m 이내" // 0.1km 미만
        distance < 1.0 -> {
            val roundedDistance = ((distance * 1000) / 100).toInt() * 100 // 100m 단위
            "${roundedDistance}m"
        }

        distance < 100.0 -> {
            // 5km 단위로 반올림
            val roundedDistance = (Math.round(distance / 5) * 5).toInt()
            "${roundedDistance}km"
        }

        else -> {
            val roundedDistance = (distance / 100).toInt() * 100 // 100km 단위
            "${roundedDistance}km"
        }
    }
}

@Composable
fun InfoElement(painter: Painter, description: String, count: String, isTrue: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isTrue) {
            Icon(
                modifier = Modifier
                    .width(12.dp)
                    .height(12.dp),
                painter = painter,
                contentDescription = description,
                tint = colorResource(R.color.primary_color)
            )
        } else {
            Icon(
                modifier = Modifier
                    .width(12.dp)
                    .height(12.dp),
                painter = painter,
                contentDescription = description,
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = count,
            fontSize = 10.sp,
            fontWeight = FontWeight(600),
            lineHeight = 12.sp,
            color = Color.White
        )
    }
}

//목록 선택
@Composable
fun HomeSelect(
    selected: HomeSelectEnum,
    onSelectedChange: (HomeSelectEnum) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp)

    ) {
        Text(
            text = "최신순",
            fontSize = 14.sp,
            color = if (selected == HomeSelectEnum.LATEST) {
                colorResource(R.color.black)
            } else {
                colorResource(R.color.gray01)
            },
            modifier = Modifier.clickable(
                onClick = {
                    onSelectedChange(HomeSelectEnum.LATEST)
//                    navController.navigate("latestFeedList")
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "인기순",
            fontSize = 14.sp,
            color = if (selected == HomeSelectEnum.POPULARITY) {
                colorResource(R.color.black)
            } else {
                colorResource(R.color.gray01)
            },
            modifier = Modifier.clickable(
                onClick = {
                    onSelectedChange(HomeSelectEnum.POPULARITY)
//                    navController.navigate("popularityFeedList")
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "거리순",
            fontSize = 14.sp,
            color = if (selected == HomeSelectEnum.DISTANCE) {
                colorResource(R.color.black)
            } else {
                colorResource(R.color.gray01)
            },
            modifier = Modifier.clickable(
                onClick = {
                    onSelectedChange(HomeSelectEnum.DISTANCE)
//                    navController.navigate("distanceFeedList")
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
            fontWeight = FontWeight.Medium
        )
    }
}

//거리 설정 필터
@Composable
fun LocationFilter(distance: DistanceEnum, onDistanceChange: (DistanceEnum) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.clickable(
                onClick = {
                    onDistanceChange(DistanceEnum.UNDER_1)
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
            shape = RoundedCornerShape(100.dp),
            border = BorderStroke(
                width = 1.dp, color = if (distance == DistanceEnum.UNDER_1) {
                    colorResource(R.color.primary_color)
                } else {
                    colorResource(R.color.gray03)
                }
            ),

            ) {
            Text(
                text = "~1km",
                fontSize = 12.sp,
                color = if (distance == DistanceEnum.UNDER_1) {
                    colorResource(R.color.primary_color)
                } else {
                    colorResource(R.color.gray03)
                },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            )
        }
        Surface(
            modifier = Modifier.clickable(
                onClick = {
                    onDistanceChange(DistanceEnum.UNDER_5)
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
            shape = RoundedCornerShape(100.dp),
            border = BorderStroke(
                width = 1.dp, color = if (distance == DistanceEnum.UNDER_5) {
                    colorResource(R.color.primary_color)
                } else {
                    colorResource(R.color.gray03)
                }
            )
        ) {
            Text(
                text = "1km ~ 5km",
                fontSize = 12.sp,
                color = if (distance == DistanceEnum.UNDER_5) {
                    colorResource(R.color.primary_color)
                } else {
                    colorResource(R.color.gray03)
                },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            )
        }
        Surface(
            modifier = Modifier.clickable(
                onClick = {
                    onDistanceChange(DistanceEnum.UNDER_10)
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
            shape = RoundedCornerShape(100.dp),
            border = BorderStroke(
                width = 1.dp, color = if (distance == DistanceEnum.UNDER_10) {
                    colorResource(R.color.primary_color)
                } else {
                    colorResource(R.color.gray03)
                }
            )
        ) {
            Text(
                text = "5km ~ 10km",
                fontSize = 12.sp,
                color = if (distance == DistanceEnum.UNDER_10) {
                    colorResource(R.color.primary_color)
                } else {
                    colorResource(R.color.gray03)
                },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            )
        }
        Surface(
            modifier = Modifier.clickable(
                onClick = {
                    onDistanceChange(DistanceEnum.UNDER_20)
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
            shape = RoundedCornerShape(100.dp),
            border = BorderStroke(
                width = 1.dp, color = if (distance == DistanceEnum.UNDER_20) {
                    colorResource(R.color.primary_color)
                } else {
                    colorResource(R.color.gray03)
                }
            )
        ) {
            Text(
                text = "10km ~ 20km",
                fontSize = 12.sp,
                color = if (distance == DistanceEnum.UNDER_20) {
                    colorResource(R.color.primary_color)
                } else {
                    colorResource(R.color.gray03)
                },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            )
        }
        Surface(
            modifier = Modifier.clickable(
                onClick = {
                    onDistanceChange(DistanceEnum.UNDER_50)
                },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
            shape = RoundedCornerShape(100.dp),
            border = BorderStroke(
                width = 1.dp, color = if (distance == DistanceEnum.UNDER_50) {
                    colorResource(R.color.primary_color)
                } else {
                    colorResource(R.color.gray03)
                }
            )
        ) {
            Text(
                text = "20km ~ 50km",
                fontSize = 12.sp,
                color = if (distance == DistanceEnum.UNDER_50) {
                    colorResource(R.color.primary_color)
                } else {
                    colorResource(R.color.gray03)
                },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            )
        }
    }
}

@Composable
fun MoveToTop() {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(100.dp),
        border = BorderStroke(width = 1.dp, color = colorResource(R.color.gray03)),
        shadowElevation = 10.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp)
        ) {
            Text(
                text = "맨위로 이동",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.gray01)
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_top),
                contentDescription = "리스트 상단 이동 버튼",
                tint = colorResource(R.color.gray01)
            )
        }
    }
}

@Composable
fun ReplaceHomeList() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "아직 등록된 카드가 없어요",
                fontSize = 16.sp,
                color = colorResource(R.color.black),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                textAlign = TextAlign.Center,
                text = "사소하지만 말 못 한 이야기를\n카드로 만들어 볼까요?",
                fontSize = 14.sp,
                color = colorResource(R.color.gray03),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun LocationDialog(openLocationDialog: (Boolean) -> Unit, onLocationResulted: (Boolean) -> Unit) {
    Dialog(onDismissRequest = {
        openLocationDialog(false)
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
                    text = "위치 정보 사용 설정",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.black)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "위치 확인을 위해 권한 설정이 필요해요",
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
                            openLocationDialog(false)
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
                            openLocationDialog(false)
                            onLocationResulted(true)
                        },
                        modifier = Modifier
                            .width(130.dp)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primary_color)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "설정",
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
fun BackPressExitHandler() {
    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // 뒤로가기 버튼 핸들링
    BackHandler {
        if (backPressedOnce) {
            exitProcess(0)
        } else {
            backPressedOnce = true
            Toast.makeText(context, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

            coroutineScope.launch {
                delay(3000)
                backPressedOnce = false
            }
        }
    }
}