package com.sooum.android.ui

import android.os.Build
import android.widget.Space
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sooum.android.R
import com.sooum.android.User
import com.sooum.android.domain.model.TagFeedDataModel
import com.sooum.android.ui.common.PostNav
import com.sooum.android.ui.viewmodel.TagViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TagListScreen(navController: NavController, tagId: String) {
    val tagViewModel: TagViewModel = hiltViewModel()

    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tagViewModel.getTagSummary(tagId, onResult = {
            isFavorite = it
        })
        tagViewModel.loadTagFeed(tagId)
//        tagViewModel.getTagFeedList(tagId, User.userInfo.latitude, User.userInfo.longitude, null)
    }

    val lazyTagFeed = tagViewModel.lazyTagFeed.collectAsState(initial = null).value?.collectAsLazyPagingItems()

    var isRefreshing by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            lazyTagFeed?.refresh()
        }
    )

    LaunchedEffect(lazyTagFeed?.loadState?.refresh) {
        if (lazyTagFeed?.loadState?.refresh !is LoadState.Loading) {
            isRefreshing = false
        }
    }

    val tagScrollState = rememberLazyListState()

    val showMoveToTopButton by remember {
        derivedStateOf {
            tagScrollState.firstVisibleItemIndex > 0 || tagScrollState.firstVisibleItemScrollOffset > 0
        }
    }

    val coroutineScope = rememberCoroutineScope()

    var isClickable by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 18.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = colorResource(R.color.gray_black),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        navController.popBackStack()
                    }
            )
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "#${tagViewModel.tagSummary?.content}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 19.6.sp,
                    color = colorResource(R.color.gray_black)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "카드 갯수 ${tagViewModel.tagSummary?.cardCnt}개",
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    lineHeight = 16.8.sp,
                    color = colorResource(R.color.gray500)
                )
            }
            Icon(
                painter = if (isFavorite) {
                    painterResource(R.drawable.ic_star_filled)
                } else {
                    painterResource(R.drawable.ic_star)
                },
                contentDescription = null,
                tint = if (isFavorite) {
                    colorResource(R.color.blue300)
                } else {
                    colorResource(R.color.gray_black)
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = isClickable
                    ) {
                        isClickable = false
                        if (isFavorite) {
                            tagViewModel.deleteTagFavorite(tagId, onItemClick = {
                                if (it == 204) {
                                    isFavorite = false
                                }
                                else {
                                    isFavorite = true
                                }
                            })
                        }
                        else {
                            tagViewModel.postTagFavorite(tagId, onItemClick = {
                                if (it == 201) {
                                    isFavorite = true
                                }
                                else {
                                    isFavorite = false
                                }
                            })
                        }

                        coroutineScope.launch {
                            kotlinx.coroutines.delay(1000)
                            isClickable = true
                        }
                    }
            )
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (lazyTagFeed != null) {
                if (lazyTagFeed.itemCount == 0) {
                    if (tagViewModel.tagSummary?.cardCnt == 0) {
                        Box(modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center) {
                            EmptyText()
                        }
                    }
                    else {
                        Box(modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center) {
                            BlockText()
                        }
                    }
                }
                else {
                    LazyColumn(
                        state = tagScrollState,
                        modifier = Modifier.pullRefresh(pullRefreshState)
                    ) {
                        items(lazyTagFeed.itemCount) { index ->
                            TagContentCard(lazyTagFeed[index]!!, index, onItemClick = { cardId ->
                                navController.navigate("${PostNav.Detail.screenRoute}/${cardId}")
                            })
                        }
                    }

                    if (showMoveToTopButton) {
                        Box(modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 60.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    tagScrollState.animateScrollToItem(0)
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
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TagContentCard(item: TagFeedDataModel.Embedded.TagFeedCardDto, index: Int, onItemClick: (String) -> Unit) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color.Black.copy(alpha = 0f), Color.Black.copy(alpha = 0.6f)),
        startY = 0f,
        endY = 60f // 그라데이션의 높이를 60dp로 설정
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1 / 0.9f)
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onItemClick(item.id)
            },
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
//                    PungTime("14 : 00 : 00")
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
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 24.sp,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00000000), // 투명한 검정
                            Color(0x99000000)  // 약간 불투명한 검정
                        )
                    ))
                    .align(Alignment.BottomCenter)

            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 26.dp, bottom = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_clock),
                        contentDescription = null,
                        tint = colorResource(R.color.gray_white),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatTimeDifference(item.createdAt),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.8.sp,
                        color = colorResource(R.color.gray_white)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (item.distance != null) {
                        Icon(
                            painter = painterResource(R.drawable.ic_location),
                            contentDescription = null,
                            tint = colorResource(R.color.gray_white),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatDistanceInKm(item.distance!!),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 16.8.sp,
                            color = colorResource(R.color.gray_white)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Icon(
                        painter = if (item.isLiked) {
                            painterResource(R.drawable.ic_heart_filled)
                        } else {
                            painterResource(R.drawable.ic_heart)
                        },
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = if (item.isLiked) {
                            colorResource(R.color.blue300)
                        } else {
                            colorResource(R.color.gray_white)
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.likeCnt.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.8.sp,
                        color = if (item.isLiked) {
                            colorResource(R.color.blue300)
                        } else {
                            colorResource(R.color.gray_white)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = if (item.isCommentWritten) {
                            painterResource(R.drawable.ic_comment_filled)
                        } else {
                            painterResource(R.drawable.ic_comment)
                        },
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = if (item.isCommentWritten) {
                            colorResource(R.color.blue300)
                        } else {
                            colorResource(R.color.gray_white)
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.commentCnt.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.8.sp,
                        color = if (item.isCommentWritten) {
                            colorResource(R.color.blue300)
                        } else {
                            colorResource(R.color.gray_white)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BlockText() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "조회할 수 있는 카드가 없어요",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 24.sp,
            color = colorResource(R.color.gray_black)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "차단된 사용자의 카드는",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 19.6.sp,
            color = colorResource(R.color.gray500)
        )
        Text(
            text = "확인할 수 없어요",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 19.6.sp,
            color = colorResource(R.color.gray500)
        )
    }
}

@Composable
fun EmptyText() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "등록된 카드가 없어요",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 24.sp,
            color = colorResource(R.color.gray_black)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "해당 태그를 사용한\n카드가 없어요",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 19.6.sp,
            color = colorResource(R.color.gray500)
        )
    }
}