package com.sooum.android.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sooum.android.R
import com.sooum.android.User
import com.sooum.android.domain.model.SortedByDistanceDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.SortedByPopularityDataModel
import com.sooum.android.enums.DistanceEnum
import com.sooum.android.enums.HomeSelectEnum
import com.sooum.android.ui.common.PostNav
import com.sooum.android.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavHostController) {
    var isVisible by remember { mutableStateOf(true) }

    val homeViewModel: HomeViewModel = hiltViewModel()

    var latitude = User.userInfo.latitude
    var longitude = User.userInfo.longitude

    Log.d("HomeScreen", "${latitude}, ${longitude}")

    var openLocationDialog by remember { mutableStateOf(false) }
    var openSystemLocationDialog by remember { mutableStateOf(false) }

    val latestScrollState = rememberLazyListState()
    val popularityScrollState = rememberLazyListState()
    val distanceScrollState = rememberLazyListState()

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

    var latestPreviousIndex by remember { mutableStateOf(0) }
    var popularityPreviousIndex by remember { mutableStateOf(0) }
    var distancePreviousIndex by remember { mutableStateOf(0) }

    var selected by remember { mutableStateOf(HomeSelectEnum.LATEST) }
    var distance by remember { mutableStateOf(DistanceEnum.UNDER_1) }

    var distanceCardList by remember {
        mutableStateOf<List<SortedByDistanceDataModel.Embedded.DistanceFeedCard>>(
            emptyList()
        )
    }

    LaunchedEffect(latestScrollState) {
        snapshotFlow { latestScrollState.firstVisibleItemIndex }
            .collect { currentIndex ->
                isVisible = currentIndex <= latestPreviousIndex
                latestPreviousIndex = currentIndex
            }
    }
    LaunchedEffect(popularityScrollState) {
        snapshotFlow { popularityScrollState.firstVisibleItemIndex }
            .collect { currentIndex ->
                isVisible = currentIndex <= popularityPreviousIndex
                popularityPreviousIndex = currentIndex
            }
    }
    LaunchedEffect(distanceScrollState) {
        snapshotFlow { distanceScrollState.firstVisibleItemIndex }
            .collect { currentIndex ->
                isVisible = currentIndex <= distancePreviousIndex
                distancePreviousIndex = currentIndex
            }
    }

    var initLatest by remember { mutableStateOf(false) }
    var initPopularity by remember { mutableStateOf(false) }
    var initDistance1 by remember { mutableStateOf(false) }
    var initDistance5 by remember { mutableStateOf(false) }
    var initDistance10 by remember { mutableStateOf(false) }
    var initDistance20 by remember { mutableStateOf(false) }
    var initDistance50 by remember { mutableStateOf(false) }

    LaunchedEffect(selected) {
        when (selected) {
            HomeSelectEnum.LATEST -> {
                if (homeViewModel.latestCardList.isEmpty()) {
                    homeViewModel.fetchLatestCardList(latitude, longitude) {}
                }
            }

            HomeSelectEnum.POPULARITY -> {
                if (homeViewModel.popularityCardList.isEmpty()) {
                    homeViewModel.fetchPopularityCardList(latitude, longitude) {}
                }
            }

            HomeSelectEnum.DISTANCE -> {
//                if (latitude != null && longitude != null) {
//                    homeViewModel.apply {
//                        fetchDistance1CardList(latitude!!, longitude!!) {}
//                        fetchDistance5CardList(latitude!!, longitude!!) {}
//                        fetchDistance10CardList(latitude!!, longitude!!) {}
//                        fetchDistance20CardList(latitude!!, longitude!!) {}
//                        fetchDistance50CardList(latitude!!, longitude!!) {}
//                    }
//                }
            }
        }
//        homeViewModel.apply {
//            fetchLatestCardList(latitude, longitude) {}
//            fetchPopularityCardList(latitude, longitude) {}
//
//            if (latitude != null && longitude != null) {
//                fetchDistance1CardList(latitude!!, longitude!!) {}
//                fetchDistance5CardList(latitude!!, longitude!!) {}
//                fetchDistance10CardList(latitude!!, longitude!!) {}
//                fetchDistance20CardList(latitude!!, longitude!!) {}
//                fetchDistance50CardList(latitude!!, longitude!!) {}
//            }
//        }
    }

    LaunchedEffect(distance) {
        if (latitude != null && longitude != null) {
            when (distance) {
                DistanceEnum.UNDER_1 -> {
                    if (homeViewModel.distance1CardList.isEmpty()) {
                        homeViewModel.fetchDistance1CardList(latitude!!, longitude!!) {}
                    }
                }

                DistanceEnum.UNDER_5 -> {
                    if (homeViewModel.distance5CardList.isEmpty()) {
                        homeViewModel.fetchDistance5CardList(latitude!!, longitude!!) {}
                    }
                }

                DistanceEnum.UNDER_10 -> {
                    if (homeViewModel.distance10CardList.isEmpty()) {
                        homeViewModel.fetchDistance10CardList(latitude!!, longitude!!) {}
                    }
                }

                DistanceEnum.UNDER_20 -> {
                    if (homeViewModel.distance20CardList.isEmpty()) {
                        homeViewModel.fetchDistance20CardList(latitude!!, longitude!!) {}
                    }
                }

                DistanceEnum.UNDER_50 -> {
                    if (homeViewModel.distance50CardList.isEmpty()) {
                        homeViewModel.fetchDistance50CardList(latitude!!, longitude!!) {}
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp)
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            AnimatedVisibility(
                visible = isVisible
            ) {
                Column {
                    HomeSelect(
                        selected = selected,
                        onSelectedChange = { newSelectedEnum ->
                            selected = newSelectedEnum
                            if (selected == HomeSelectEnum.DISTANCE && latitude == null && longitude == null) {
                                openLocationDialog = true
                            }
                        }
                    )
                    Divider(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                    )

                    if (selected == HomeSelectEnum.DISTANCE) {
                        LocationFilter(distance, onDistanceChange = { newDistance ->
                            distance = newDistance
                            when (distance) {
                                DistanceEnum.UNDER_1 -> {
                                    distanceCardList = homeViewModel.distance1CardList
                                }

                                DistanceEnum.UNDER_5 -> {
                                    distanceCardList = homeViewModel.distance5CardList
                                }

                                DistanceEnum.UNDER_10 -> {
                                    distanceCardList = homeViewModel.distance10CardList
                                }

                                DistanceEnum.UNDER_20 -> {
                                    distanceCardList = homeViewModel.distance20CardList
                                }

                                DistanceEnum.UNDER_50 -> {
                                    distanceCardList = homeViewModel.distance50CardList
                                }
                            }
                        })
                    }
                }
            }

            when (selected) {
                HomeSelectEnum.LATEST -> {
                    LatestFeedList(
                        latestScrollState,
                        homeViewModel.latestCardList,
                        showMoveToTopButtonForLatest,
                        navController,
                        homeViewModel
                    )
                }

                HomeSelectEnum.POPULARITY -> {
                    PopularityFeedList(
                        popularityScrollState,
                        homeViewModel.popularityCardList,
                        showMoveToTopButtonForPopularity,
                        homeViewModel,
                        navController
                    )
                }

                HomeSelectEnum.DISTANCE -> {
                    when (distance) {
                        DistanceEnum.UNDER_1 -> {
                            DistanceFeedList(
                                distanceScrollState,
                                homeViewModel.distance1CardList,
                                showMoveToTopButtonForDistance,
                                homeViewModel,
                                distance,
                                navController
                            )
                        }

                        DistanceEnum.UNDER_5 -> {
                            DistanceFeedList(
                                distanceScrollState,
                                homeViewModel.distance5CardList,
                                showMoveToTopButtonForDistance,
                                homeViewModel,
                                distance,
                                navController
                            )
                        }

                        DistanceEnum.UNDER_10 -> {
                            DistanceFeedList(
                                distanceScrollState,
                                homeViewModel.distance10CardList,
                                showMoveToTopButtonForDistance,
                                homeViewModel,
                                distance,
                                navController
                            )
                        }

                        DistanceEnum.UNDER_20 -> {
                            DistanceFeedList(
                                distanceScrollState,
                                homeViewModel.distance20CardList,
                                showMoveToTopButtonForDistance,
                                homeViewModel,
                                distance,
                                navController
                            )
                        }

                        DistanceEnum.UNDER_50 -> {
                            DistanceFeedList(
                                distanceScrollState,
                                homeViewModel.distance50CardList,
                                showMoveToTopButtonForDistance,
                                homeViewModel,
                                distance,
                                navController
                            )
                        }
                    }
//                    DistanceFeedList(distanceScrollState, distanceCardList, showMoveToTopButtonForDistance, homeViewModel, distance, navController)
                }
            }

            if (openLocationDialog) {
                LocationDialog(openLocationDialog = { isOpen ->
                    openLocationDialog = isOpen
                },
                    onLocationResulted = { isGrant ->
                        openSystemLocationDialog = isGrant
                    }
                )
            }
            if (openSystemLocationDialog) {
                val context = LocalContext.current

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    GetUserLocation { location ->
                        latitude = location?.latitude
                        longitude = location?.longitude
                        homeViewModel.apply {
                            if (latitude != null && longitude != null) {
                                fetchDistance1CardList(latitude!!, longitude!!) {}
                                fetchDistance5CardList(latitude!!, longitude!!) {}
                                fetchDistance10CardList(latitude!!, longitude!!) {}
                                fetchDistance20CardList(latitude!!, longitude!!) {}
                                fetchDistance50CardList(latitude!!, longitude!!) {}
                            }
                        }
                    }
                } else {
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
    scrollState: LazyListState,
    latestCardList: List<SortedByLatestDataModel.Embedded.LatestFeedCard>,
    showMoveToTopButton: Boolean,
    navController: NavHostController,
    homeViewModel: HomeViewModel,
) {
//   기존 lazyColumn 때 사용했던 pullRefresh 로직, 임시 보관
    val coroutineScope = rememberCoroutineScope()

    var isRefreshing by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            homeViewModel.fetchLatestCardList(
                User.userInfo.latitude,
                User.userInfo.longitude,
                onFetchFinished = {
                    isRefreshing = false
                })
        }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (latestCardList.isEmpty()) {
            ReplaceHomeList()
        } else {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.pullRefresh(pullRefreshState)
            ) {
                items(latestCardList) { item ->
                    Log.d("123", item.toString())
                    LatestContentCard(item, navController)
                }
            }
            if (showMoveToTopButton) {
                Box(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
                    .clickable() {
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
    scrollState: LazyListState,
    popularityCardList: List<SortedByPopularityDataModel.Embedded.PopularFeedCard>,
    showMoveToTopButton: Boolean,
    homeViewModel: HomeViewModel,
    navController: NavController,
) {
    val coroutineScope = rememberCoroutineScope()

    var isRefreshing by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            homeViewModel.fetchPopularityCardList(
                User.userInfo.latitude,
                User.userInfo.longitude,
                onFetchFinished = {
                    isRefreshing = false
                })
        }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (popularityCardList.isEmpty()) {
            ReplaceHomeList()
        } else {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.pullRefresh(pullRefreshState)
            ) {
                items(popularityCardList) { item ->
                    PopularityContentCard(item, navController)
                }
            }
            if (showMoveToTopButton) {
                Box(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
                    .clickable() {
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
fun DistanceFeedList(
    scrollState: LazyListState,
    distanceCardList: List<SortedByDistanceDataModel.Embedded.DistanceFeedCard>,
    showMoveToTopButton: Boolean,
    homeViewModel: HomeViewModel,
    distance: DistanceEnum,
    navController: NavController,
) {
    val coroutineScope = rememberCoroutineScope()

    var isRefreshing by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            if (User.userInfo.latitude != null && User.userInfo.longitude != null) {
                when (distance) {
                    DistanceEnum.UNDER_1 -> {
                        homeViewModel.fetchDistance1CardList(
                            User.userInfo.latitude!!,
                            User.userInfo.longitude!!,
                            onFetchFinished = {
                                isRefreshing = false
                            })
                    }

                    DistanceEnum.UNDER_5 -> {
                        homeViewModel.fetchDistance5CardList(
                            User.userInfo.latitude!!,
                            User.userInfo.longitude!!,
                            onFetchFinished = {
                                isRefreshing = false
                            })
                    }

                    DistanceEnum.UNDER_10 -> {
                        homeViewModel.fetchDistance10CardList(
                            User.userInfo.latitude!!,
                            User.userInfo.longitude!!,
                            onFetchFinished = {
                                isRefreshing = false
                            })
                    }

                    DistanceEnum.UNDER_20 -> {
                        homeViewModel.fetchDistance20CardList(
                            User.userInfo.latitude!!,
                            User.userInfo.longitude!!,
                            onFetchFinished = {
                                isRefreshing = false
                            })
                    }

                    DistanceEnum.UNDER_50 -> {
                        homeViewModel.fetchDistance50CardList(
                            User.userInfo.latitude!!,
                            User.userInfo.longitude!!,
                            onFetchFinished = {
                                isRefreshing = false
                            })
                    }
                }
            }
        }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (distanceCardList.isEmpty()) {
            ReplaceHomeList()
        } else {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.pullRefresh(pullRefreshState)
            ) {
                items(distanceCardList) { item ->
                    DistanceContentCard(item, navController)
                }
            }
            if (showMoveToTopButton) {
                Box(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
                    .clickable() {
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
@Composable
private fun RefreshIndicator(
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
        if (item.isLiked) {
            InfoElement(
                painter = painterResource(R.drawable.ic_heart_filled),
                description = "좋아요",
                count = "${item.likeCnt}",
                isTrue = true
            )
        } else {
            InfoElement(
                painter = painterResource(R.drawable.ic_heart),
                description = "좋아요",
                count = "${item.likeCnt}",
                isTrue = false
            )
        }
        if (item.isCommentWritten) {
            InfoElement(
                painter = painterResource(R.drawable.ic_comment_filled),
                description = "댓글",
                count = "${item.commentCnt}",
                isTrue = true
            )
        } else {
            InfoElement(
                painter = painterResource(R.drawable.ic_comment),
                description = "댓글",
                count = "${item.commentCnt}",
                isTrue = false
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PopularityCardInfo(item: SortedByPopularityDataModel.Embedded.PopularFeedCard) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (item.isLiked) {
            InfoElement(
                painter = painterResource(R.drawable.ic_heart_filled),
                description = "좋아요",
                count = "${item.likeCnt}", isTrue = true
            )
        } else {
            InfoElement(
                painter = painterResource(R.drawable.ic_heart),
                description = "좋아요",
                count = "${item.likeCnt}",
                isTrue = false
            )
        }
        if (item.isCommentWritten) {
            InfoElement(
                painter = painterResource(R.drawable.ic_comment_filled),
                description = "댓글",
                count = "${item.commentCnt}",
                isTrue = true
            )
        } else {
            InfoElement(
                painter = painterResource(R.drawable.ic_comment),
                description = "댓글",
                count = "${item.commentCnt}",
                isTrue = false
            )
        }
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
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DistanceCardInfo(item: SortedByDistanceDataModel.Embedded.DistanceFeedCard) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoElement(
            painter = painterResource(R.drawable.ic_location),
            description = "위치",
            count = formatDistanceInKm(item.distance),
            isTrue = false
        )
        InfoElement(
            painter = painterResource(R.drawable.ic_clock),
            description = "시간",
            count = formatTimeDifference(item.createdAt),
            isTrue = false
        )
        if (item.isLiked) {
            InfoElement(
                painter = painterResource(R.drawable.ic_heart_filled),
                description = "좋아요",
                count = "${item.likeCnt}",
                isTrue = true
            )
        } else {
            InfoElement(
                painter = painterResource(R.drawable.ic_heart),
                description = "좋아요",
                count = "${item.likeCnt}",
                isTrue = false
            )
        }
        if (item.isCommentWritten) {
            InfoElement(
                painter = painterResource(R.drawable.ic_comment_filled),
                description = "댓글",
                count = "${item.commentCnt}",
                isTrue = true
            )
        } else {
            InfoElement(
                painter = painterResource(R.drawable.ic_comment),
                description = "댓글",
                count = "${item.commentCnt}",
                isTrue = false
            )
        }
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
        distance < 0.1 -> "${(distance * 1000).toInt()}m 이내" // 0~99m
        distance < 1.0 -> "${(distance * 1000).toInt() / 100 * 100}m" // 100~999m
        distance < 100.0 -> "${distance.toInt()}km" // 1km~100km
        else -> "${(distance / 100).toInt() * 100}km" // 100km 이상
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
            fontWeight = if (selected == HomeSelectEnum.LATEST) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
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
            fontWeight = if (selected == HomeSelectEnum.POPULARITY) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
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
            fontWeight = if (selected == HomeSelectEnum.DISTANCE) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
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
        border = BorderStroke(width = 1.dp, color = colorResource(R.color.gray03))
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