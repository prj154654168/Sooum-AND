package com.sooum.android.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sooum.android.R
import com.sooum.android.enums.ReportTypeEnum
import com.sooum.android.ui.theme.Gray1
import com.sooum.android.ui.theme.Gray4
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.viewmodel.ReportViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavHostController,
    cardId: String,
    reportViewModel: ReportViewModel = hiltViewModel(),
) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var reportTypeEnum: ReportTypeEnum = ReportTypeEnum.OTHER

    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        ReportDialog(navController) { showDialog = false }
    }
    var showDialog2 by remember { mutableStateOf(false) }
    if (showDialog2) {
        ReportDialog2(navController) { showDialog2 = false }
    }
    var isReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        // 2초 동안 대기
        delay(2000)
        isReady = true
    }
//    LaunchedEffect(reportViewModel.httpCode) {
//        if (reportViewModel.httpCode == 200) {
//            showDialog = true
//        }
//        if (reportViewModel.httpCode == 400) {
//            showDialog2 = true
//        }
//    }
    if (reportViewModel.isLoading == 1) {
        if (reportViewModel.httpCode == 201) {
            showDialog = true
        }
        if (reportViewModel.httpCode == 400) {
            showDialog2 = true
        }
        reportViewModel.isLoading = 0
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    modifier = Modifier.padding(start = 20.dp),
                    text = "신고하기",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "뒤로가기",
                    )
                }
            })
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(20.dp)
        ) {
            Column() {
                Text(
                    text = "신고 사유 선택",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 28.dp)
                )
                ReportButton(
                    onOptionSelected = {
                        selectedOption = 0
                        reportTypeEnum = ReportTypeEnum.DEFAMATION_AND_ABUSE
                    },
                    selectedOption,
                    0,
                    "비방 및 욕설",
                    "욕설을 사용하여 타인에게 모욕감을 주는 경우"
                )
                ReportButton(
                    onOptionSelected = {
                        selectedOption = 1
                        reportTypeEnum = ReportTypeEnum.PRIVACY_VIOLATION
                    },
                    selectedOption,
                    1,
                    "개인정보 침해",
                    "법적으로 중요한 타인의 개인정보를 게제"
                )
                ReportButton(
                    onOptionSelected = {
                        selectedOption = 2
                        reportTypeEnum = ReportTypeEnum.INAPPROPRIATE_ADVERTISING
                    },
                    selectedOption,
                    2,
                    "부적절한 홍보 및 바이럴",
                    "부적절한 스팸 홍보 행위"
                )
                ReportButton(
                    onOptionSelected = {
                        selectedOption = 3
                        reportTypeEnum = ReportTypeEnum.PORNOGRAPHY
                    },
                    selectedOption,
                    3,
                    "음란물",
                    "음란한 행위와 관련된 부적절한 행동"
                )
                ReportButton(
                    onOptionSelected = {
                        selectedOption = 4
                        reportTypeEnum = ReportTypeEnum.IMPERSONATION_AND_FRAUD
                    },
                    selectedOption,
                    4,
                    "사칭 및 사기",
                    "사칭으로 타인의 권리를 침해하는 경우"
                )
                ReportButton(
                    onOptionSelected = {
                        selectedOption = 5
                        reportTypeEnum = ReportTypeEnum.OTHER
                    },
                    selectedOption,
                    5,
                    "기타",
                    "해당하는 신고항목이 없는 경우"
                )


            }

            Button(
                enabled = selectedOption != null,
                onClick = {
                    if (isReady) {
                        reportViewModel.reportUser(cardId.toLong(), reportTypeEnum)

                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.BottomCenter),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Text(text = "신고하기", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

}


@Composable
fun ReportButton(
    onOptionSelected: () -> Unit,
    selectedOption: Int?,
    index: Int,
    title: String,
    content: String,

    ) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Gray4, RoundedCornerShape(12.dp))
            .padding(bottom = 10.dp)
            .clip(
                shape = RoundedCornerShape(
                    size = 12.dp,
                ),
            ),
    )
    {
        Column() {
            Row() {
                RadioButton(
                    selected = selectedOption == index,
                    onClick = { onOptionSelected() },
                    modifier = Modifier,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Primary, // 선택되었을 때의 색상
                    )
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Text(
                text = content,
                fontSize = 12.sp,
                color = Gray1,
                modifier = Modifier.padding(start = 45.dp)
            )
        }

    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    )
}

@Composable
fun ReportDialog(
    navController: NavHostController,
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
                    text = "신고가 접수 되었어요",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.black)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "신고 내용을 확인한 후 조치할 예정이에요",
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
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
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

@Composable
fun ReportDialog2(
    navController: NavHostController,
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
                    text = "신고가 처리 중이에요",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.black)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "이전에 신고가 접수되어 처리 중이에요",
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
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
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