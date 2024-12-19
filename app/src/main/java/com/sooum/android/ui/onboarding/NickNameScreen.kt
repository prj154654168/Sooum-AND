package com.sooum.android.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.SooumApplication
import com.sooum.android.ui.common.LogInNav
import com.sooum.android.ui.theme.Gray1
import com.sooum.android.ui.theme.Gray50
import com.sooum.android.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NickNameScreen(navController: NavHostController) {

    val adjectives = listOf<String>( "공부하는", "생각하는", "사랑하는", "노래하는",
        "요리하는", "운동하는", "여행하는", "대화하는",
        "청소하는", "정리하는", "그리는", "사진하는",
        "연구하는", "설계하는", "개발하는", "관리하는",
        "발표하는", "수업하는", "교육하는", "상담하는",
        "치료하는", "분석하는", "조사하는", "기록하는",
        "편집하는", "제작하는", "수리하는", "판매하는",
        "구매하는", "투자하는", "기획하는", "운영하는",
        "지원하는", "협력하는", "참여하는", "소통하는",
        "개선하는", "실천하는", "실험하는", "탐구하는",
        "수집하는", "배달하는", "전달하는", "연결하는",
        "조정하는", "선택하는", "결정하는", "준비하는",
        "확인하는", "수업하는", "연습하는", "발표하는",
        "기록하는", "정리하는", "대처하는", "해결하는",
        "조율하는", "탐색하는", "분석하는", "실천하는"
    )
    val nouns = listOf("강아지", "고양이", "기린", "토끼",
        "사자", "호랑이", "악어", "코끼리",
        "판다", "부엉이", "까치", "앵무새",
        "여우", "오리", "수달", "다람쥐",
        "펭귄", "참새", "갈매기", "도마뱀",
        "우산", "책상", "가방", "의자",
        "시계", "안경", "컵", "접시",
        "전화기", "자전거", "냉장고", "라디오",
        "바나나", "케이크", "모자", "열쇠",
        "지도", "구두", "텀블러", "바구니",
        "공책", "거울", "청소기", "햄스터",
        "낙타", "두더지", "돌고래", "문어",
        "미어캣", "오소리", "다슬기", "해파리",
        "원숭이", "홍학", "물개", "바다표",
        "코뿔소", "물소", "개구리", "거북이"
    )

    var text by remember {
        mutableStateOf("${adjectives.random()} ${nouns.random()}")
    }
    val context = LocalContext.current
    Scaffold(topBar = {
        TopAppBar(title = {}, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기",
                )
            }
        })
    }) {
        it
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 50.dp),
                    text = "반가워요!", fontSize = 22.sp,
                )
                Text(text = "당신을 어떻게 부르면 될까요?", fontSize = 22.sp)
                Text(
                    text = "닉네임은 추후 변경 가능해요",
                    fontSize = 14.sp,
                    color = Gray1,
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                )

                BasicTextField(
                    value = text,
                    onValueChange = { newText ->
                        // 텍스트 길이 제한
                        if (newText.length <= 8) {
                            text = newText
                        }
                    },
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start
                    ),
                    cursorBrush = SolidColor(Color.Black),
                    decorationBox = { innerTextField ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(color = Gray50, shape = RoundedCornerShape(20.dp))
                                .padding(all = 16.dp)
                            , contentAlignment = Alignment.CenterStart
                        ) {
                            innerTextField()  // TextField의 텍스트를 표시
                            IconButton(
                                onClick = { text = "" },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear text",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (text.isEmpty()) {
                        Row() {
                            Image(
                                painter = painterResource(id = R.drawable.ic_warning),
                                contentDescription = "warning",
                                modifier = Modifier.padding(end = 5.dp)
                            )
                            Text("한글자 이상 입력해주세요", color = Color.Red, fontSize = 14.sp)
                        }
                    }

                    Text(
                        "${text.length}/8",
                        color = Gray1,
                        fontSize = 14.sp,
                        modifier = Modifier.align(
                            Alignment.TopEnd
                        )
                    )
                }

            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.BottomCenter),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                onClick = {
                    SooumApplication().saveVariable("nickName", text)
                    navController.navigate(LogInNav.LogInProfile.screenRoute)
                }) {
                Text(text = "확인")
            }
        }
    }
}

@Composable
fun CustomBasicTextField(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String = "Enter text...",
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = Gray50, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
        ) {
            // BasicTextField
            BasicTextField(
                value = text,
                onValueChange = { newText ->
                    // 텍스트 길이 제한
                    if (newText.length <= 8) {
                        onTextChange(newText)
                    }
                },
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth(),
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start
                ),
                cursorBrush = SolidColor(Color.Black),
                decorationBox = { innerTextField ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically)
                    ) {
                        if (text.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = TextStyle(color = Color.Gray, fontSize = 16.sp),
                                modifier = Modifier
                                    .padding(top = 10.dp, bottom = 10.dp)
                                    .align(Alignment.CenterStart)
                            )
                        }
                        innerTextField()  // TextField의 텍스트를 표시
                    }
                }
            )

            // Clear (X) Icon Button
            if (text.isNotEmpty()) {
                IconButton(onClick = { onTextChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear text",
                        tint = Color.Gray
                    )
                }
            }
        }
    }

}
