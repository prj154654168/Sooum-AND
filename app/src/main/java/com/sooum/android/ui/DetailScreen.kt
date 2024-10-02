package com.sooum.android.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.sooum.android.ImageLoader
import com.sooum.android.InfoElement
import com.sooum.android.PungTime
import com.sooum.android.R
import com.sooum.android.formatDistanceInKm
import com.sooum.android.formatTimeDifference
import com.sooum.android.ui.theme.Gray
import com.sooum.android.ui.theme.Gray3


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen() {
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
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .zIndex(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                    ) {
                        //if (item.isStory) {
                        PungTime("14 : 00 : 00")
                        //}
                    }
                }
                ImageLoader("https://postfiles.pstatic.net/MjAyMTAzMzFfMjgz/MDAxNjE3MTc1ODA0Mzkw.UmGHi_fsjNN1Cl0G59g0wnuU93JhTs4LZL2Z5uJVMJYg.2tT327KrHv1PRdm8wgKgHn0FaOeRGkmXzRh2wpWx_30g.JPEG.shop_marchen/white-rainforest-C04QjCIf2GQ-unsplash.jpg?type=w966")
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

                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoElement(
                            painter = painterResource(R.drawable.ic_location),
                            description = "위치",
                            count = formatDistanceInKm(21.899822120895017),
                            isTrue = false
                        )
                        InfoElement(
                            painter = painterResource(R.drawable.ic_clock),
                            description = "시간",
                            count = formatTimeDifference("2024-09-27T01:41:05.112716"),
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
            items(20) { item ->
                TagItem()
            }
        }


    }

}

@Composable
fun TagItem() {
    Surface(
        modifier = Modifier.padding(end = 10.dp),
        shape = RoundedCornerShape(4.dp),
        color = Gray3
    ) {
        Text(
            text = "#태그2",
            fontSize = 14.sp,
            color = Gray,
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 4.dp)
        )
    }
}

