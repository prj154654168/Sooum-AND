package com.sooum.android.ui.myprofile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sooum.android.R
import com.sooum.android.ui.ImageLoaderForUrl
import com.sooum.android.ui.common.MyProfile
import com.sooum.android.ui.viewmodel.MyProfileViewModel

@Composable
fun MyProfileScreen(navController: NavHostController) {
    val myProfileViewModel: MyProfileViewModel = hiltViewModel()
    val data = myProfileViewModel.myProfile.value
    val myFeedCard = myProfileViewModel.myFeedCard.value
    if (data != null) {
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
                    Icon(
                        painter = painterResource(R.drawable.ic_hamburger),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .padding(20.dp)
                            .align(Alignment.TopEnd)
                            .clickable {
                                navController.navigate(MyProfile.Setting.screenRoute)
                            }
                    )
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
                                .clip(CircleShape)
                                .size(128.dp)
                                .aspectRatio(1f)
                                .align(Alignment.Center),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Image(
                            modifier = Modifier
                                .padding(bottom = 22.dp)
                                .align(Alignment.Center),
                            painter = painterResource(R.drawable.ic_sooum_logo),
                            contentDescription = null
                        )
                    }
                }//이미지
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
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
                        modifier = Modifier
                            .width(48.dp)
                            .clickable {
                                navController.navigate(MyProfile.Following.screenRoute)
                            }
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
                        modifier = Modifier
                            .width(48.dp)
                            .clickable {
                                navController.navigate(MyProfile.Follower.screenRoute)
                            }
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
                }//팔로우


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = colorResource(R.color.primary_color))
                        .clickable { navController.navigate(MyProfile.ProfileModify.screenRoute) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 14.dp, bottom = 14.dp),
                    ) {
                        Text(
                            text = "프로필 수정",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 19.6.sp,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(22.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3)
                ) {
                    items(myFeedCard.size) { index ->
                        Box(
                            modifier = Modifier.aspectRatio(1f)
                        ) {
                            ImageLoaderForUrl(myFeedCard[index].backgroundImgUrl.href)
                            Text(
                                text = myFeedCard[index].content,
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
            }
        }
    }

}