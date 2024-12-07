package com.sooum.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sooum.android.R
import com.sooum.android.domain.model.FavoriteTagDataModel
import com.sooum.android.ui.common.PostNav
import com.sooum.android.ui.common.TagNav
import com.sooum.android.ui.viewmodel.TagViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@Composable
fun TagScreen(navController: NavController) {
    val tagViewModel: TagViewModel = hiltViewModel()

    val scrollState = rememberScrollState()

    var tagTextField by remember { mutableStateOf("") }
    var bookmarkTag: List<String?> by remember { mutableStateOf(listOf(null)) }

    //임시 주석
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
//    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        tagViewModel.getRecommendTagList()
        tagViewModel.getFavoriteTag(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp)
            .verticalScroll(scrollState)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = tagTextField,
            onValueChange = {
                tagTextField = it
            },
            placeholder = {
                Text(
                    text = "태그 키워드를 입력해주세요",
                    fontSize = 14.sp,
                    color = colorResource(R.color.gray500),
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.None,
                    lineHeight = 19.6.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                // 임시 주석
                .onFocusChanged { isFocused = it.isFocused },
//                .focusRequester(focusRequester),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.primary_color),
                unfocusedBorderColor = Color.Transparent,
                cursorColor = colorResource(R.color.primary_color),
                unfocusedContainerColor = colorResource(R.color.gray50),
                focusedContainerColor = Color.White

            ),
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = colorResource(R.color.gray800),
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.None,
                lineHeight = 24.sp
            ),
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = colorResource(R.color.gray400),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        //클릭 이벤트
                    }

                )
            },
            singleLine = true
        )
        //임시 주석
        if (!isFocused) {
            if (tagViewModel.favoriteTagList.isNotEmpty()) {
                Text(
                    text = "내가 즐겨찾기한 태그",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.gray_black),
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 16.dp)
                )
                tagViewModel.favoriteTagList.forEach { favoriteTag ->
                    BookmarkTagCardList(favoriteTag,
                        onItemClick = { tagId ->
                            navController.navigate("${TagNav.TagList.screenRoute}/${tagId}")
                        },
                        onCardCLick = { cardId ->
                            navController.navigate("${PostNav.Detail.screenRoute}/${cardId}")
                        })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            Text(
                text = "추천태그",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.gray_black),
                lineHeight = 24.sp,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 16.dp)
            )
//        for (i in 0..tagViewModel.recommendTagList.size) {
//            TagCard()
//        }
            tagViewModel.recommendTagList.forEach { tag ->
                TagCard(
                    tag.tagId,
                    tag.tagContent,
                    tag.tagUsageCnt,
                    tag.links.tagFeed.href,
                    onItemClick = {
                        navController.navigate("${TagNav.TagList.screenRoute}/${tag.tagId}")
                    })
            }
        }
    }
}

@Composable
fun BookmarkTagCardList(
    favoriteTag: FavoriteTagDataModel.Embedded.FavoriteTag,
    onItemClick: (String) -> Unit,
    onCardCLick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, color = colorResource(R.color.gray200))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 16.dp, top = 20.dp, bottom = 15.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "#${favoriteTag.tagContent}",
                        fontSize = 12.sp,
                        color = colorResource(R.color.gray800),
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.8.sp
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "+${favoriteTag.tagUsageCnt}",
                        fontSize = 12.sp,
                        color = colorResource(R.color.gray500),
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.8.sp
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
//                            임시 주석
                            onItemClick(favoriteTag.id)
                        }
                ) {
                    Text(
                        text = "더보기",
                        fontSize = 12.sp,
                        color = colorResource(R.color.blue300),
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.8.sp
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_next),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = colorResource(R.color.blue300)
                    )
                }
            }
            if (favoriteTag.previewCards.isNotEmpty()) {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    favoriteTag.previewCards.forEachIndexed { index, previewCard ->
                        if (index == 0) {
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        BookmarkTagCard(previewCard, onItemClick = { cardId ->
                            onCardCLick(cardId)
                        })
                        if (index == 4) {
                            Spacer(modifier = Modifier.width(16.dp))
                        } else {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
//            if (tagViewModel.favoriteTagList[index].previewCards.isNotEmpty()) {
//                Row {
//                    for (cardIndex in 0..tagViewModel.favoriteTagList[index].previewCards.size) {
//                        if (cardIndex == 0) {
//                            Spacer(modifier = Modifier.width(16.dp))
//                        }
//                        BookmarkTagCard()
//                        if (cardIndex == 4) {
//                            Spacer(modifier = Modifier.width(16.dp))
//                        }
//                        else {
//                            Spacer(modifier = Modifier.width(8.dp))
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//            }
        }
    }
}

@Composable
fun BookmarkTagCard(
    previewCard: FavoriteTagDataModel.Embedded.FavoriteTag.PreviewCard,
    onItemClick: (String) -> Unit
) {
    val hazeState = remember { HazeState() }

    Box(
        modifier = Modifier
            .size(160.dp)
            .clip(RoundedCornerShape(20.dp))
//            .haze(hazeState)
//            .background(
//                brush = Brush.verticalGradient(
//                    colors =
//                )
//            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onItemClick(previewCard.id)
            }
    ) {
        ImageLoaderForUrl(previewCard.backgroundImgUrl.href)
        Box(
            modifier = Modifier
                .width(120.dp)
                .heightIn(max = 120.dp)
                .align(Alignment.Center)
//                .hazeChild(
//                    state = hazeState
//                )
                .background(
                    Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Text(
                text = previewCard.content,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 16.8.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                textAlign = TextAlign.Center,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TagCard(
    tagId: String,
    tagContent: String,
    tagCount: String,
    tagLink: String,
    onItemClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color = colorResource(R.color.gray200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = "#${tagContent}",
                    fontSize = 12.sp,
                    color = colorResource(R.color.gray800),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.8.sp
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "+${tagCount}",
                    fontSize = 12.sp,
                    color = colorResource(R.color.gray500),
                    fontWeight = FontWeight.Normal,
                    lineHeight = 16.8.sp
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onItemClick(tagId)
                    }
            ) {
                Text(
                    text = "더보기",
                    fontSize = 12.sp,
                    color = colorResource(R.color.blue300),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.8.sp
                )
                Icon(
                    painter = painterResource(R.drawable.ic_next),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = colorResource(R.color.blue300)
                )
            }
        }
    }
}
