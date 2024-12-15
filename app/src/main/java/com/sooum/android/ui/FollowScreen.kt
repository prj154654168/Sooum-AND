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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sooum.android.R
import com.sooum.android.domain.model.FollowerDataModel
import com.sooum.android.ui.common.SooumNav
import com.sooum.android.ui.viewmodel.FollowerViewModel

@Composable
fun FollowScreen(navController: NavHostController) {
    val viewModel: FollowerViewModel = hiltViewModel()
    BackHandler {
        navController.navigate(SooumNav.Profile.screenRoute) {
            popUpTo(SooumNav.Profile.screenRoute) { inclusive = true }
        }
    }
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
                text = "팔로워 (${viewModel.followerList.value.size})",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = colorResource(R.color.gray_black),
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(viewModel.followerList.value) { index ->
                FollowItem(index, viewModel)
            }
        }
    }
}

@Composable
fun FollowItem(item: FollowerDataModel.FollowerInfo, viewModel: FollowerViewModel) {

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
        Surface(
            shape = RoundedCornerShape(71.dp),
            color = if (!isFollow) colorResource(R.color.blue300) else colorResource(R.color.gray200),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp)
                .clickable {
                    isFollow = if (!isFollow) {
                        viewModel.postFollow(item.id.toLong())
                        !isFollow
                    } else {
                        viewModel.deleteFollow(item.id.toLong())
                        !isFollow
                    }
                }
        ) {
            Text(
                text = if (!isFollow) "팔로우" else "팔로잉",
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.8.sp,
                color = if (!isFollow) Color.White else Color.Gray,
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 4.5.dp,
                    bottom = 4.5.dp
                )
            )
        }
    }
}