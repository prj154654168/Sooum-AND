package com.sooum.android.ui.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.sooum.android.SooumApplication
import com.sooum.android.domain.model.MemberInfo
import com.sooum.android.domain.model.Policy
import com.sooum.android.domain.model.signUpModel
import com.sooum.android.ui.common.LogInNav
import com.sooum.android.ui.theme.Gray5
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.viewmodel.AgreeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgreeScreen(navController: NavHostController) {
    val viewModel: AgreeViewModel = viewModel()

    var firstChecked by remember { mutableStateOf(false) }
    var secondChecked by remember { mutableStateOf(false) }
    var thirdChecked by remember { mutableStateOf(false) }
    val allChecked by remember { derivedStateOf { firstChecked && secondChecked && thirdChecked } }
    val context = LocalContext.current

    // "모두 동의합니다" 선택 시 다른 항목도 체크되도록 처리
    LaunchedEffect(allChecked) {
        if (allChecked) {
            firstChecked = true
            secondChecked = true
            thirdChecked = true
        }
        if (!allChecked) {
            firstChecked = false
            secondChecked = false
            thirdChecked = false
        }
    }
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
                    text = "숨을 시작하기 위해서는", fontSize = 22.sp,
                )
                Text(text = "약관 동의가 필요해요", fontSize = 22.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .clickable {
                            //allChecked = !allChecked
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = allChecked,
                        onCheckedChange = { isChecked ->
                            // 전체 선택 클릭 시 모든 체크박스를 업데이트
                            firstChecked = isChecked
                            secondChecked = isChecked
                            thirdChecked = isChecked
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Primary,
                            uncheckedColor = Gray5
                        )

                    )
                    Text(text = "약관 전체 동의", color = if (allChecked) Primary else Gray5)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 개별 약관 동의 항목 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { firstChecked = !firstChecked },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = firstChecked,
                        onCheckedChange = { firstChecked = it }, colors = CheckboxDefaults.colors(
                            checkedColor = Primary,
                            uncheckedColor = Gray5
                        )
                    )
                    Text(" [필수] 서비스 이용 약관", color = if (firstChecked) Primary else Gray5)
                }


                // 개별 약관 동의 항목 2
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { secondChecked = !secondChecked },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = secondChecked,
                        onCheckedChange = { secondChecked = it }, colors = CheckboxDefaults.colors(
                            checkedColor = Primary,
                            uncheckedColor = Gray5
                        )
                    )
                    Text(" [필수] 위치정보 이용 약관", color = if (secondChecked) Primary else Gray5)
                }

                // 개별 약관 동의 항목 2
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { thirdChecked = !thirdChecked },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = thirdChecked,
                        onCheckedChange = { thirdChecked = it }, colors = CheckboxDefaults.colors(
                            checkedColor = Primary,
                            uncheckedColor = Gray5
                        )
                    )
                    Text(" [필수] 개인정보 처리 방침", color = if (thirdChecked) Primary else Gray5)
                }


            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.BottomCenter),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                onClick = {
                    viewModel.signUp(
                        signUpModel(
                            memberInfo = MemberInfo(
                                "ANDROID",
                                SooumApplication().getVariable("encryptedDeviceId")
                                    .toString(),
                                SooumApplication().getVariable("fcmToken"),
                                false
                            ),
                            policy = Policy(firstChecked, secondChecked, thirdChecked)
                        )
                    )

                    navController.navigate(LogInNav.NickName.screenRoute)
                }) {
                Text(text = "확인")
            }
        }

    }

}
