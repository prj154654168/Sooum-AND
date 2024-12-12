package com.sooum.android.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sooum.android.R
import com.sooum.android.ui.common.PostNav
import com.sooum.android.ui.theme.Gray5
import com.sooum.android.ui.viewmodel.DifProfileViewModel

@Composable
fun DifProfileScreen(navController: NavHostController, memberId: String?) {
    val viewModel: DifProfileViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        memberId?.let {
            viewModel.getDifProfile(it.toLong())
            viewModel.getDifFeedCard(it.toLong())
        }
    }

    var showBlockDialog by remember { mutableStateOf(false) }


    val data = viewModel.difProfile.value

    if (data != null && memberId != null) {
        if (showBlockDialog) {
            BlockProfileDialog(navController, memberId.toLong(), viewModel) {
                showBlockDialog = false
            }
        }
        var isFollow by remember { mutableStateOf(data.isFollowing) }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .padding(start = 20.dp, top = 22.dp, bottom = 22.dp)
                            .align(Alignment.TopStart)
                            .clickable { navController.popBackStack() }
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 12.5.dp, bottom = 12.5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = data.nickname,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(R.color.gray800),
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "TOTAL ${data.totalVisitorCnt} TODAY ${data.currentDayVisitors}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(R.color.gray400),
                            lineHeight = 16.8.sp
                        )
                    }
                    if (!viewModel.isBlock.value) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 25.5.dp, end = 20.dp)
                                .clickable {
                                    showBlockDialog = true
                                },
                            text = "차단하기",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(R.color.gray500)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp)
                ) {
                    if (data.profileImg != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(data.profileImg.href)
                                .build(),
                            contentDescription = "카드 이미지",
                            modifier = Modifier
                                .size(128.dp)
                                .padding(bottom = 22.dp)
                                .align(Alignment.CenterStart)
                                .clip(CircleShape)
                                .aspectRatio(1f),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            modifier = Modifier
                                .padding(bottom = 22.dp)
                                .align(Alignment.CenterStart),
                            painter = painterResource(R.drawable.ic_sooum_logo),
                            contentDescription = null
                        )
                    }
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(48.dp)
                        ) {
                            Text(
                                data.cardCnt,
                                fontSize = 18.sp,
                                lineHeight = 24.48.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorResource(R.color.gray700)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "카드",
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorResource(R.color.gray500)
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(48.dp)
                        ) {
                            Text(
                                data.followingCnt,
                                fontSize = 18.sp,
                                lineHeight = 24.48.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorResource(R.color.gray700)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "팔로잉",
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorResource(R.color.gray500)
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(48.dp)
                        ) {
                            Text(
                                data.followerCnt,
                                fontSize = 18.sp,
                                lineHeight = 24.48.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorResource(R.color.gray700)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "팔로워",
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorResource(R.color.gray500)
                            )
                        }
                    }
                }
                if (viewModel.isBlock.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                colorResource(R.color.primary_color)
                            )
                            .clickable {
                                viewModel.deleteUserBlock(memberId.toLong())
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(top = 14.dp, bottom = 14.dp),
                        ) {
                            Text(
                                text = "차단 해제",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 19.6.sp,
                                color =
                                Color.White
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (!isFollow)
                                    colorResource(R.color.primary_color)
                                else
                                    colorResource(R.color.gray200)
                            )
                            .clickable {
                                if (!isFollow) {
                                    viewModel.postFollow(memberId.toLong())
                                    viewModel.getDifProfile(memberId.toLong())
                                    isFollow = !isFollow
                                } else {
                                    viewModel.deleteFollow(memberId.toLong())
                                    viewModel.getDifProfile(memberId.toLong())
                                    isFollow = !isFollow
                                }
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(top = 14.dp, bottom = 14.dp),
                        ) {
                            if (!isFollow) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_add),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (!isFollow)
                                    "팔로우하기"
                                else
                                    "팔로우 중",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 19.6.sp,
                                color = if (!isFollow)
                                    Color.White
                                else
                                    colorResource(id = R.color.gray700),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))
                if (!viewModel.isBlock.value) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3)
                    ) {
                        items(viewModel.difFeedCard.value) { index ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clickable {
                                        navController.navigate("${PostNav.Detail.screenRoute}/${index.id}")
                                    }
                            ) {
                                ImageLoaderForUrl(index.backgroundImgUrl.href)
                                Text(
                                    text = index.content,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    lineHeight = 21.6.sp,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(10.dp),
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color.White
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        "차단한 계정입니다.",
                        fontSize = 16.sp,
                        color = Gray5,
                        modifier = Modifier
                            .padding(top = 200.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }

}

@Composable
fun BlockProfileDialog(
    navController: NavHostController,
    memberId: Long,
    viewModel: DifProfileViewModel,
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
                    text = "정말 차단하시겠어요?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.black)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "해당 사용자의 카드와 댓글을 볼 수 없어요",
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
                            viewModel.userBlock(memberId)
                            showDialog()
                        },
                        modifier = Modifier
                            .width(130.dp)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primary_color)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "확인",
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