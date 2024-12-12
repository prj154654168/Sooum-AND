package com.sooum.android.ui.myprofile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.sooum.android.R
import com.sooum.android.Utils
import com.sooum.android.ui.common.SoonumNav
import com.sooum.android.ui.viewmodel.LogInProfileViewModel
import java.io.ByteArrayOutputStream


@Composable
fun ModifyProfileScreen(navController: NavHostController) {
    var nicknameTextField by remember { mutableStateOf("") }

    val viewModel: LogInProfileViewModel = viewModel()
    val context = LocalContext.current
    var selectedImageBitmap: Bitmap? by remember { mutableStateOf(null) }
    var selectedImageForGallery by remember { mutableStateOf<Bitmap?>(null) }
    val imageCropLauncher =
        rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if (result.isSuccessful) {
                result.uriContent?.let {
                    //getBitmap method is deprecated in Android SDK 29 or above so we need to do this check here
                    selectedImageBitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images
                            .Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder
                            .createSource(context.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }

                    selectedImageForGallery = selectedImageBitmap

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    selectedImageBitmap?.compress(
                        Bitmap.CompressFormat.JPEG,
                        100,
                        byteArrayOutputStream
                    )
                    val byteArray = byteArrayOutputStream.toByteArray()
                    viewModel.imgByteArray = byteArray
                }

            } else {
                Log.d("AddPostScreen", "ImageCropping error: ${result.error}")
            }
        }


    if (viewModel.isLoading == 1) {
        navController.navigate(SoonumNav.Profile.screenRoute) {
            popUpTo(navController.graph.id) {
                inclusive = true
            } // 백 스택 비우기
            launchSingleTop = true // 중복된 화면 생성 방지
        }
        viewModel.isLoading = 2
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
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
                        .padding(20.dp)
                )
                Text(
                    text = "프로필 수정",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(42.dp))
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.align(Alignment.Center)) {
                    if (selectedImageBitmap == null) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_sooum_logo),
                            contentDescription = "Background Image",
                            modifier = Modifier
                                .clickable {
                                    val cropOptions = CropImageContractOptions(
                                        null,
                                        Utils.cropOption
                                    )
                                    imageCropLauncher.launch(cropOptions)
                                }
                                .align(Alignment.Center),
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(selectedImageForGallery)
                                .build(),
                            contentDescription = "카드 이미지",
                            modifier = Modifier
                                .clickable {
                                    val cropOptions = CropImageContractOptions(
                                        null,
                                        Utils.cropOption
                                    )
                                    imageCropLauncher.launch(cropOptions)
                                }
                                .clip(CircleShape)
                                .size(128.dp)
                                .aspectRatio(1f),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.ic_picture_button),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "닉네임",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = colorResource(R.color.gray700)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = nicknameTextField,
                onValueChange = {
                    if (it.length <= 8) {
                        nicknameTextField = it
                    }
                },
                placeholder = {
                    androidx.compose.material.Text(
                        text = "8글자 이내 닉네임을 입력해주세요",
                        fontSize = 16.sp,
                        color = colorResource(R.color.gray500),
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.None,
                        lineHeight = 24.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
//                        .border(width = 1.dp, color = colorResource(R.color.gray02), shape = RoundedCornerShape(12.dp))
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.primary_color),
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = colorResource(R.color.primary_color),
                    unfocusedContainerColor = colorResource(R.color.gray50),
                    focusedContainerColor = Color.White
                ),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = colorResource(R.color.gray800),
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.None,
                    lineHeight = 24.sp
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (nicknameTextField.isEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_warning),
                            contentDescription = null,
                            tint = colorResource(R.color.red)
                        )
                        Text(
                            text = "한글자 이상 입력해주세요",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 19.6.sp,
                            color = colorResource(R.color.red)
                        )
                    }
                }
                Text(
                    text = "${nicknameTextField.length}/8",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    color = colorResource(R.color.gray500)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable {
                        if (nicknameTextField.isNotEmpty()) {
                            viewModel.profiles(
                                nicknameTextField,
                                if (selectedImageBitmap == null) 2 else 1
                            )
                        }

                    },
                color = if (nicknameTextField.isNotEmpty()) colorResource(R.color.blue300) else colorResource(
                    id = R.color.gray500
                )
            ) {
                Box() {
                    Text(
                        text = "완료",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 19.6.sp,
                        color = if (nicknameTextField.isNotEmpty()) colorResource(R.color.gray_white) else colorResource(
                            id = R.color.gray_black
                        ),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 14.dp, bottom = 14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}