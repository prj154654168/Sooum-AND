package com.sooum.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.sooum.android.R
import com.sooum.android.ui.theme.Primary

@Composable
fun AddPostScreen() {
    var content by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    Column(Modifier.padding(20.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1 / 0.9f)
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(40.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ImageLoader("https://search.pstatic.net/sunny/?src=https%3A%2F%2Fus.123rf.com%2F450wm%2Fvantuz%2Fvantuz1506%2Fvantuz150600251%2F41134623-%25EB%2593%25B1%25EB%25B6%2588%25EC%259D%2580-%25EC%2596%25B4%25EB%2591%2590%25EC%259A%25B4-%25EB%25B0%25B0%25EA%25B2%25BD%25EC%259D%2584-%25EC%25A1%25B0%25EB%25AA%2585%25ED%2595%259C%25EB%258B%25A4-%25EB%25B2%25A1%25ED%2584%25B0-%25EC%259D%25B4%25EB%25AF%25B8%25EC%25A7%2580%25EC%259E%2585%25EB%258B%2588%25EB%258B%25A4-.jpg%3Fver%3D6&type=sc960_832")
                BasicTextField(
                    value = content,
                    onValueChange = {
                        if (it.length <= 1000) {
                            content = it
                        }
                    },
                    modifier = Modifier
                        .padding(40.dp)
                        .align(Alignment.Center)

                        .verticalScroll(scrollState),
                    textStyle = TextStyle(color = Color.White),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                ) {
                    // 기본 텍스트가 없으면 빈칸으로 표시
                    if (content.isEmpty()) {
                        Text(
                            text = "입력하세요...",
                            color = Color.White,
                            style = TextStyle(color = Color.White)
                        )
                    } else {
                        it()
                    }
                }

                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    text = "${content.length} / 1000",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
        var tagText by remember { mutableStateOf("") }
        var tags by remember { mutableStateOf(listOf<String>()) }
        val tagHint: List<Pair<String, Int>> = listOf(
            "UX기획" to 6,
            "UX리서처" to 4,
            "UIUX를 공부하는 효율적인 방법" to 3,
            "UI디자인이에요" to 2,
            "UI가독성" to 1
        )
        if (tags.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                items(tags) { tag ->
                    TagChip(tag = tag) {
                        tags = tags.filterNot { it == tag }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = tagText,
                onValueChange = { tagText = it },
                placeholder = { Text(text = "태그를 입력해주세요",fontSize = 16.sp) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Primary
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .padding(end = 8.dp),
                trailingIcon = {
                    if (tagText.isNotBlank()) {
                        IconButton(onClick = {
                            tags = tags + tagText
                            tagText = ""
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = "Add Tag"
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(24.dp))
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (tagText.isNotBlank()) {
                            tags = tags + tagText
                            tagText = ""
                        }
                    }
                )
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tagText != "") {
            Text("#관련 태그")
            FlowRow(
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tagHint.forEach { tagHint ->
                    TagHintChip(tagHint = tagHint.first, tagHint.second)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TagChip(tag: String, onDelete: () -> Unit) {
    Chip(
        onClick = {},
        colors = ChipDefaults.chipColors(backgroundColor = Color.LightGray)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(id = R.drawable.ic_tag_cancel), // 삭제 아이콘 리소스
                contentDescription = "Delete",
                modifier = Modifier
                    .clickable(onClick = onDelete) // 클릭 시 onDelete 호출
                    .padding(end = 4.dp) // 아이콘과 텍스트 사이의 여백
            )
            Text(
                text = tag,
                fontSize = 14.sp
            )
        }

    }

}

@Composable
fun TagHintChip(tagHint: String, count: Int) {
    Row(){
        Box(
            modifier = Modifier
                .padding(4.dp)
                .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row() {
                Text(
                    text = "#$tagHint",
                    color = Color.Black
                )
                Text(
                    modifier = Modifier.padding(start = 1.dp),
                    text = if (count < 10) "0$count" else "$count",
                    color = Primary
                )
            }
        }
        Spacer(modifier = Modifier.padding(end = 10.dp))
    }

}