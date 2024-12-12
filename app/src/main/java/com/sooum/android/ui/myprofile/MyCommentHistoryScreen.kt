package com.sooum.android.ui.myprofile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.ui.ImageLoaderForUrl
import com.sooum.android.ui.common.MyProfile
import com.sooum.android.ui.viewmodel.MyCommentHistoryViewModel

@Composable
fun MyCommentHistoryScreen(navController: NavHostController) {

    val viewModel : MyCommentHistoryViewModel = hiltViewModel()


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    tint = colorResource(R.color.gray_black),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            navController.popBackStack()
                        }
                        .padding(15.dp)
                )
                Text(
                    text = "덧글 히스토리",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(15.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3)
                ) {
                    items(viewModel.myCommentCard.value.size) { index ->
                        Box(
                            modifier = Modifier.aspectRatio(1f)
                        ) {
                            ImageLoaderForUrl(viewModel.myCommentCard.value[index].backgroundImgUrl.href)
                            Text(
                                text = viewModel.myCommentCard.value[index].content,
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