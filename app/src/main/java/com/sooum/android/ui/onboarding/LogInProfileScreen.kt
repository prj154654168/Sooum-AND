package com.sooum.android.ui.onboarding

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.sooum.android.R
import com.sooum.android.SooumApplication
import com.sooum.android.ui.common.SoonumNav
import com.sooum.android.ui.theme.Gray3
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.viewmodel.LogInProfileViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogInProfileScreen(navController: NavHostController) {
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
                    //viewModel.getImageUrl(byteArray)
                }

            } else {
                Log.d("AddPostScreen", "ImageCropping error: ${result.error}")
            }
        }



    Scaffold(topBar = {
        TopAppBar(title = {}, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "뒤로가기",
                )
            }
        })
    }) {
        it
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .height(500.dp)
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 50.dp),
                    text = "당신을 표현하는 사진을", fontSize = 22.sp,
                )
                Text(text = "프로필로 등록해볼까요?", fontSize = 22.sp)
                Text(
                    text = "프로필 사진은 추후 변경이 가능해요",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                )
                if (selectedImageBitmap == null) {
                    Image(
                        painter = painterResource(id = R.drawable.sooum_logo),
                        contentDescription = "Background Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(top = 30.dp)
                            .clickable {
                                val cropOptions = CropImageContractOptions(
                                    null,
                                    CropImageOptions(imageSourceIncludeCamera = false)
                                )
                                imageCropLauncher.launch(cropOptions)
                            },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(selectedImageForGallery)
                            .build(),
                        contentDescription = "카드 이미지",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                val cropOptions = CropImageContractOptions(
                                    null,
                                    CropImageOptions(imageSourceIncludeCamera = false)
                                )
                                imageCropLauncher.launch(cropOptions)
                            }
                            .height(100.dp),
                        contentScale = ContentScale.Crop
                    )
                }

            }
            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    onClick = {
                        viewModel.profiles(
                            SooumApplication().getVariable("nickName").toString()
                        )
                        navController.navigate(SoonumNav.Home.screenRoute) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            } // 백 스택 비우기
                            launchSingleTop = true // 중복된 화면 생성 방지
                        }
                    }) {
                    Text(text = "확인")
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Gray3),
                    onClick = { }) {
                    Text(text = "다음에 변경하기")
                }
            }

        }
    }
}