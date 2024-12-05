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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.sooum.android.R
import com.sooum.android.ui.viewmodel.TagViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TagListScreen(navController: NavController, tagId: String) {
    val tagViewModel: TagViewModel = hiltViewModel()

    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tagViewModel.getTagSummary(tagId)
    }
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
                modifier = Modifier.align(Alignment.CenterStart)
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
                    ) {
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
                    }
            )
        }
        LazyColumn {
            items(10) {
                TagContentCard()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TagContentCard() {
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
            ImageLoader("https://cdn.speconomy.com/news/photo/201611/20161102_1_bodyimg_73550.jpg")
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
                    text = "최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수최대글자수",
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
                    .background(gradientBrush)
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
                        text = "30분전",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.8.sp,
                        color = colorResource(R.color.gray_white)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_location),
                        contentDescription = null,
                        tint = colorResource(R.color.gray_white),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "1km",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.8.sp,
                        color = colorResource(R.color.gray_white)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_heart),
                        contentDescription = null,
                        tint = colorResource(R.color.gray_white),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "24",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.8.sp,
                        color = colorResource(R.color.gray_white)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_comment),
                        contentDescription = null,
                        tint = colorResource(R.color.gray_white),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "06",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.8.sp,
                        color = colorResource(R.color.gray_white)
                    )
                }
            }
        }
    }
}