package com.sooum.android

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sooum.android.Constants.ACCESS_TOKEN
import com.sooum.android.enums.DistanceEnum
import com.sooum.android.enums.HomeSelectEnum
import com.sooum.android.model.SortedByDistanceDataModel
import com.sooum.android.model.SortedByLatestDataModel
import com.sooum.android.model.SortedByPopularityDataModel
import com.sooum.android.ui.common.PostNav
import com.sooum.android.ui.common.SoonumBottomNavigation
import com.sooum.android.ui.common.SoonumNav
import com.sooum.android.ui.common.SoonumNavHost
import com.sooum.android.ui.theme.SoonumTheme
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scrollState = rememberLazyListState()
            var isVisible by remember { mutableStateOf(true) }
            val showFab =
                remember { derivedStateOf { scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 0 } }
            var previousIndex by remember { mutableStateOf(0) }

            val coroutineScope = rememberCoroutineScope()

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            LaunchedEffect(scrollState) {
                snapshotFlow { scrollState.firstVisibleItemIndex }
                    .collect { currentIndex ->
                        isVisible = currentIndex <= previousIndex
                        previousIndex = currentIndex
                    }
            }

            SoonumTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            if (SoonumNav.isMainRoute(currentRoute)) {
                                SoonumBottomNavigation(navController)
                            }
                        },
                        topBar = {
                            TopAppBar(
                                title = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_logo),
                                        contentDescription = "앱 로고",
                                        modifier = Modifier
                                            .width(93.dp)
                                            .height(18.dp)
                                    )
                                },
                                actions = {
                                    IconButton(onClick = {
                                        /* 버튼 클릭 이벤트 */
                                    }) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_alarm),
                                            contentDescription = "Alarm",
                                            colorFilter = ColorFilter.tint(colorResource(R.color.gray01))
                                        )
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        },
                        floatingActionButton = {
                            if (showFab.value) {
                                ExtendedFloatingActionButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            scrollState.animateScrollToItem(0)
                                        }
                                    },
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = colorResource(R.color.gray02),
                                            shape = RoundedCornerShape(100.dp)
                                        ),
                                    shape = RoundedCornerShape(100.dp),
                                    content = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "맨위로 이동",
                                                color = colorResource(R.color.gray01),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Icon(
                                                painter = painterResource(R.drawable.ic_arrow_top),
                                                contentDescription = "맨위로 이동 버튼",
                                                tint = colorResource(R.color.gray01)
                                            )
                                        }
                                    },
                                    containerColor = Color.White,
                                    elevation = FloatingActionButtonDefaults.elevation(10.dp)
                                )
                            }
                        },
                        floatingActionButtonPosition = FabPosition.Center
                    ) { innerPadding ->
                        SoonumNavHost(
                            navController = navController,
                            startDestination = SoonumNav.Home.screenRoute,
                            modifier = Modifier.padding(innerPadding),
                            isVisible = isVisible,
                            scrollState = scrollState
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(isVisible: Boolean, scrollState: LazyListState, navController: NavHostController) {
    var selected by remember { mutableStateOf(HomeSelectEnum.LATEST) }
    var distance by remember { mutableStateOf(DistanceEnum.UNDER_1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // content 파라미터에 TopAppBar 아래의 콘텐츠를 추가
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                Column {
                    HomeSelect(selected, onSelectedChange = { newSelection ->
                        selected = newSelection
                    })
                    Divider(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                    )
                    if (selected != HomeSelectEnum.DISTANCE) {
                        Spacer(Modifier.height(10.dp))
                    }

                    if (selected == HomeSelectEnum.DISTANCE) {
                        LocationFilter(distance, onDistanceChange = { newDistance ->
                            distance = newDistance
                        })
                    }
                }
            }

            ItemList(scrollState, selected, navController)
        }
    }
}

@Composable
fun AddPostScreen() {
    Column {
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")
        Text(text = "AddPost")

    }
}

@Composable
fun TagScreen() {
    Text(text = "Tag")
}

@Composable
fun ProfileScreen() {
    Text(text = "Profile")
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ItemList(
    scrollState: LazyListState,
    selected: HomeSelectEnum,
    navController: NavHostController,
) {
    val coroutineScope = rememberCoroutineScope()
    val retrofitInstance = RetrofitInterface.getInstance().create(CardApi::class.java)
    var latestCardList by remember {
        mutableStateOf<List<SortedByLatestDataModel.Embedded.LatestFeedCardDto>>(
            emptyList()
        )
    }
    var popularityCardList by remember {
        mutableStateOf<List<SortedByPopularityDataModel.Embedded.PopularCard>>(
            emptyList()
        )
    }
    var distanceCardList by remember {
        mutableStateOf<List<SortedByDistanceDataModel.Embedded.DistanceCardDto>>(
            emptyList()
        )
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                //최근순
                val latestResponse =
                    retrofitInstance.getLatestCardList(ACCESS_TOKEN, 37.5170112, 126.9019532)
                if (latestResponse.isSuccessful) {
                    val dataModel = latestResponse.body()
                    if (dataModel != null) {
                        latestCardList = dataModel._embedded.latestFeedCardDtoList
                        Log.d(
                            "MainActivity",
                            "latestSuccess, ${latestCardList.first().backgroundImgUrl.href}"
                        )
                        for (i in latestCardList) {
                            Log.d("Latest", "${i.backgroundImgUrl.href}")
                        }
                        Log.d("Latest", "${latestCardList.first()}")
                    }
                } else {
                    Log.d("MainActivity", "cardList null")
                }
                //인기순
                val popularityResponse =
                    retrofitInstance.getPopularityCardList(ACCESS_TOKEN, 37.5170112, 126.9019532)
                if (popularityResponse.isSuccessful) {
                    val dataModel = popularityResponse.body()
                    if (dataModel != null) {
                        popularityCardList = dataModel._embedded.popularCardRetrieveList
                        Log.d(
                            "MainActivity",
                            "popularitySuccess, ${popularityCardList.first().backgroundImgUrl.href}"
                        )
                    }
                } else {
                    Log.d("MainActivity", "cardList null")
                }
                //거리순
                val distanceResponse = retrofitInstance.getDistanceCardList(
                    ACCESS_TOKEN,
                    37.5170112,
                    126.9019532,
                    DistanceEnum.UNDER_20
                )

                if (distanceResponse.isSuccessful) {
                    val dataModel = distanceResponse.body()
                    Log.d("MainActivity", "${dataModel?.status}")
                    if (dataModel != null) {
                        distanceCardList = dataModel._embedded.distanceCardDtoList
                        Log.d(
                            "MainActivity",
                            "popularitySuccess, ${distanceCardList.first().backgroundImgUrl.href}"
                        )
                    }
                } else {
                    Log.d(
                        "MainActivity",
                        "status code : ${distanceResponse.code()}, error message : ${
                            distanceResponse.errorBody()?.string()
                        }"
                    )
                    Log.d("MainActivity", "cardList null")
                }
            } catch (e: Exception) {
                Log.d("MainActivity", "cardList error")
            }
        }
    }

    LazyColumn(state = scrollState) {
        if (selected == HomeSelectEnum.LATEST) {
            items(latestCardList) { item ->
                LatestContentCard(item, navController)
            }
        } else if (selected == HomeSelectEnum.POPULARITY) {
            items(popularityCardList) { item ->
                PopularityContentCard(item)
            }
        } else {
            items(distanceCardList) { item ->
                DistanceContentCard(item)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LatestContentCard(
    item: SortedByLatestDataModel.Embedded.LatestFeedCardDto,
    navController: NavHostController,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1 / 0.9f)
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
        shape = RoundedCornerShape(40.dp),
        onClick = { navController.navigate(PostNav.Detail.screenRoute) }
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
                    if (item.isStory) {
                        PungTime("14 : 00 : 00")
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
                    .align(Alignment.BottomEnd)
                    .padding(end = 26.dp, bottom = 24.dp)
            ) {
                LatestCardInfo(item)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PopularityContentCard(item: SortedByPopularityDataModel.Embedded.PopularCard) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1 / 0.9f)
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
        shape = RoundedCornerShape(40.dp)
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
                    if (item.isStory) {
                        PungTime("14 : 00 : 00")
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
                    .align(Alignment.BottomEnd)
                    .padding(end = 26.dp, bottom = 24.dp)
            ) {
                PopularityCardInfo(item)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DistanceContentCard(item: SortedByDistanceDataModel.Embedded.DistanceCardDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1 / 0.9f)
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
        shape = RoundedCornerShape(40.dp)
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
                    if (item.isStory) {
                        PungTime("14 : 00 : 00")
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
            .addHeader("Authorization", ACCESS_TOKEN)
            .build(),
        contentDescription = "카드 이미지",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun PungTime(time: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colorResource(R.color.primary_color)
    ) {
        Text(
            text = time,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LatestCardInfo(item: SortedByLatestDataModel.Embedded.LatestFeedCardDto) {
    Row(
        modifier = Modifier
            .shadow(elevation = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoElement(
            painter = painterResource(R.drawable.ic_clock),
            description = "시간",
            count = formatTimeDifference(item.createdAt),
            isTrue = false
        )
        InfoElement(
            painter = painterResource(R.drawable.ic_location),
            description = "위치",
            count = formatDistanceInKm(item.distance),
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
@Composable
fun PopularityCardInfo(item: SortedByPopularityDataModel.Embedded.PopularCard) {
    Row(
        modifier = Modifier
            .shadow(elevation = 4.dp),
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
        InfoElement(
            painter = painterResource(R.drawable.ic_location),
            description = "위치",
            count = formatDistanceInKm(item.distance),
            isTrue = false
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DistanceCardInfo(item: SortedByDistanceDataModel.Embedded.DistanceCardDto) {
    Row(
        modifier = Modifier
            .shadow(elevation = 4.dp),
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
fun HomeSelect(selected: HomeSelectEnum, onSelectedChange: (HomeSelectEnum) -> Unit) {
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