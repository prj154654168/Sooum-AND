package com.sooum.android.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sooum.android.R
import com.sooum.android.domain.model.FollowingDataModel
import com.sooum.android.ui.common.SooumNav
import com.sooum.android.ui.theme.Gray3
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.viewmodel.FollowingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FollowingScreen(navController: NavHostController) {

    BackHandler {
        navController.navigate(SooumNav.Profile.screenRoute) {
            popUpTo(SooumNav.Profile.screenRoute) { inclusive = true }
        }
    }
    val viewModel: FollowingViewModel = hiltViewModel()

    var isRefreshing by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            coroutineScope.launch {
                viewModel.getFollowing()
                isRefreshing = false
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 11.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = colorResource(R.color.gray_black),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp)
                    .clickable { navController.popBackStack() }
            )
            Text(
                text = "팔로잉 (${viewModel.followingList.value.size})",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = colorResource(R.color.gray_black),
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                items(viewModel.followingList.value) { index ->
                    FollowingItem(index, viewModel)
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

@Composable
fun FollowingItem(item: FollowingDataModel.FollowingInfo, viewModel: FollowingViewModel) {

    var isFollow by remember { mutableStateOf(item.isFollowing) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 20.dp, top = 14.dp, bottom = 14.dp)
        ) {
            if (item.backgroundImgUrl == null) {
                Image(
                    painter = painterResource(R.drawable.ic_sooum_logo),
                    contentDescription = null,
                    modifier = Modifier.size(46.dp)
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.backgroundImgUrl.href)
                        .build(),
                    contentDescription = "카드 이미지",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = item.nickname,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = colorResource(R.color.gray700)
            )
        }
        if (!isFollow) {
            Surface(
                shape = RoundedCornerShape(71.dp),
                color = colorResource(R.color.blue300),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp)
                    .clickable {
                        viewModel.postFollow(item.id.toLong())
                        isFollow = !isFollow
                    }
            ) {
                Text(
                    text = "팔로우",
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    lineHeight = 16.8.sp,
                    color = Color.White,
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 4.5.dp,
                        bottom = 4.5.dp
                    )
                )
            }
        } else {
            Text(
                text = "팔로우 취소",
                color = Gray3,
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable {
                        viewModel.deleteFollow(item.id.toLong())
                        isFollow = !isFollow
                    }
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp)
            )
        }

    }
}