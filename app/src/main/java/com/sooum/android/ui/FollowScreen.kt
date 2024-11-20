package com.sooum.android.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sooum.android.R

@Composable
fun FollowScreen() {
    var followCount by remember { mutableStateOf(600) }
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
            )
            Text(
                text = "팔로워 (${followCount})",
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
            items(20) { index ->
                FollowItem()
            }
        }
    }
}

@Composable
fun FollowItem() {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 20.dp, top = 14.dp, bottom = 14.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_sooum_logo),
                contentDescription = null,
                modifier = Modifier.size(46.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "한숨이",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = colorResource(R.color.gray700)
            )
        }
        Surface(
            shape = RoundedCornerShape(71.dp),
            color = colorResource(R.color.blue300),
            modifier = Modifier.align(Alignment.CenterEnd)
                .padding(end = 20.dp)
                .clickable {

                }
        ) {
            Text(
                text = "팔로우",
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.8.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 4.5.dp, bottom = 4.5.dp)
            )
        }
    }
}