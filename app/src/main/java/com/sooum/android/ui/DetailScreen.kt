package com.sooum.android.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.sooum.android.ImageLoader
import com.sooum.android.InfoElement
import com.sooum.android.PungTime
import com.sooum.android.R
import com.sooum.android.formatDistanceInKm
import com.sooum.android.formatTimeDifference
import com.sooum.android.model.Tag
import com.sooum.android.ui.common.PostNav
import com.sooum.android.ui.theme.Gray1
import com.sooum.android.ui.theme.Gray3
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavHostController,
    cardId: String?,
    viewModel: DetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    cardId?.let { viewModel.getFeedCard(it.toLong()) }
    cardId?.let { viewModel.getDetailCardLikeCommentCount(it.toLong()) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
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
                        }


                ) {
                    Text(
                        text = "차단하기",
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center)
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
                            navController.navigate(PostNav.Report.screenRoute)
                        }
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "신고하기",
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )

                }
            }
        }
    } //bottom sheet
    val data = viewModel.feedCardDataModel
    val count = viewModel.detailCardLikeCommentCountDataModel//TODO 화면이 계속 리컴포징 돼서 깜빡거림...
    if (data != null && count != null) {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1 / 0.9f)
                    .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                shape = RoundedCornerShape(40.dp),
                onClick = { }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
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
                            if (data.isStory) {
                                PungTime("14 : 00 : 00")
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
                                //TODO 삭제 버튼
                            } else {
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .clickable { showBottomSheet = true },
                                    painter = painterResource(R.drawable.ic_detail_kebab),
                                    contentDescription = "케밥 더보기 버튼",
                                )
                            }
                        }
                    }

                    ImageLoader(data.backgroundImgUrl.href)
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
                            text = data.content,
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
                        Row(

                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoElement(
                                painter = painterResource(R.drawable.ic_location),
                                description = "위치",
                                count = formatDistanceInKm(data.distance),
                                isTrue = false
                            )
                            InfoElement(
                                painter = painterResource(R.drawable.ic_clock),
                                description = "시간",
                                count = formatTimeDifference(data.createdAt),
                                isTrue = false
                            )
                        }

                    }
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, bottom = 10.dp),
            ) {
                items(data.tags) { item ->
                    TagItem(item)
                }
            }
            Divider(
                color = Gray3,
                modifier = Modifier
                    .height(0.4.dp)
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            Row(
                modifier = Modifier
                    .padding(end = 20.dp)
                    .align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp),
                    painter = painterResource(R.drawable.ic_detail_heart),
                    contentDescription = "좋아요",
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = count.cardLikeCnt.toString(),
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Icon(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .width(24.dp)
                        .height(24.dp),
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
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                items(3) {
                    DeatilCommentItem()
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeatilCommentItem() {
    Card(
        modifier = Modifier
            .aspectRatio(1 / 0.9f)
            .padding(end = 10.dp, bottom = 10.dp),
        shape = RoundedCornerShape(40.dp),
        onClick = { }
    ) {
        Box(
            modifier = Modifier
        ) {
            ImageLoader("https://postfiles.pstatic.net/MjAyMTAzMzFfMjgz/MDAxNjE3MTc1ODA0Mzkw.UmGHi_fsjNN1Cl0G59g0wnuU93JhTs4LZL2Z5uJVMJYg.2tT327KrHv1PRdm8wgKgHn0FaOeRGkmXzRh2wpWx_30g.JPEG.shop_marchen/white-rainforest-C04QjCIf2GQ-unsplash.jpg?type=w966")
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
                    text = "item.content",
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
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoElement(
                        painter = painterResource(R.drawable.ic_clock),
                        description = "시간",
                        count = formatTimeDifference("2024-09-27T01:41:05.112716"),
                        isTrue = false
                    )
                    InfoElement(
                        painter = painterResource(R.drawable.ic_location),
                        description = "위치",
                        count = formatDistanceInKm(21.899822120895017),
                        isTrue = false
                    )
                    InfoElement(
                        painter = painterResource(R.drawable.ic_heart),
                        description = "좋아요",
                        count = "24",
                        isTrue = false
                    )
                    InfoElement(
                        painter = painterResource(R.drawable.ic_comment),
                        description = "댓글",
                        count = "66",
                        isTrue = false
                    )
                }
            }
        }
    }
}

@Composable
fun TagItem(item: Tag) {
    Surface(
        modifier = Modifier.padding(end = 10.dp),
        shape = RoundedCornerShape(4.dp),
        color = Gray3
    ) {
        Text(
            text = item.content,
            fontSize = 14.sp,
            color = Gray1,
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 4.dp)
        )
    }
}

@Composable
fun DeleteDialog() {
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