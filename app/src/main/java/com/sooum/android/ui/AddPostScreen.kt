package com.sooum.android.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.ViewTreeObserver
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.google.accompanist.flowlayout.FlowRow
import com.sooum.android.R
import com.sooum.android.User
import com.sooum.android.Utils
import com.sooum.android.domain.model.PostCommentCardRequestDataModel
import com.sooum.android.enums.FontEnum
import com.sooum.android.enums.ImgTypeEnum
import com.sooum.android.ui.common.SooumNav
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.viewmodel.AddPostViewModel
import java.io.ByteArrayOutputStream

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddPostScreen(
    navController: NavHostController,
    cardId: String? = null,
    storyExpirationTime: String? = null
) {
    val addPostViewModel: AddPostViewModel = hiltViewModel()
    val context = LocalContext.current

    val parentCardId = if (cardId == "null") null else cardId?.toLong()
    val parentStoryExpirationTime = if (storyExpirationTime == "null") null else storyExpirationTime

    val tagTextFieldFocusManager = LocalFocusManager.current
    var isTagTextFieldFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val animatedOffset by animateDpAsState(
        targetValue = if (isTagTextFieldFocused) -250.dp else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )

    var showDialog by remember { mutableStateOf(false) }

    //기본 이미지 : DEFAULT, 내 사진 : USER
    var imgType by remember { mutableStateOf(ImgTypeEnum.DEFAULT) }

    //스위치 상태
    var storyChecked by remember { mutableStateOf(false) }  //백엔드와 동일
    var distanceChecked by remember { mutableStateOf(false) }   //백엔드와 반대
    var privacyChecked by remember { mutableStateOf(false) }    //백엔드와 반대

    //고딕체 : PRETENDARD, 손글씨체 : SCHOOL_SAFE_CHALKBOARD_ERASER,
    var fontType by remember { mutableStateOf(FontEnum.PRETENDARD) }

    var selectedImage by remember { mutableStateOf(0) }

    var selectedImageForGallery by remember { mutableStateOf<Bitmap?>(null) }

    //본문
    var content by remember { mutableStateOf("") }

    //태그 텍스트 필드
    var tagTextField by remember { mutableStateOf("") }

    //태그 리스트
    val tagList by remember { mutableStateOf(mutableListOf<String>()) }

    LaunchedEffect(Unit) {
        addPostViewModel.getDefaultImageList()
    }

    var selectedImageBitmap: Bitmap? by remember { mutableStateOf(null) }

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
                    addPostViewModel.getImageUrl(byteArray)
                }

            } else {
                Log.d("AddPostScreen", "ImageCropping error: ${result.error}")
            }
        }

    BottomSheetScaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (storyChecked) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(37.dp),
                                color = colorResource(R.color.blue200),
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        end = 10.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    ),
                                    text = "시간제한 카드",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = colorResource(R.color.gray800)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            tint = colorResource(R.color.gray800),
                            contentDescription = "뒤로가기",
                        )
                    }
                },
                actions = {
                    Text(
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                enabled = if (content.isEmpty()) false else true
                            ) {
                                showDialog = true
                            },
                        text = "작성하기",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (content.isEmpty()) colorResource(R.color.gray700) else colorResource(
                            R.color.blue300
                        )
                    )
                }
            )
        },
        sheetContainerColor = Color.White,
        sheetContent = {
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
            ) {
                Row {
                    androidx.compose.material3.Text(
                        text = "기본이미지",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (imgType == ImgTypeEnum.DEFAULT) {
                            Color.Black
                        } else {
                            colorResource(R.color.gray03)
                        },
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            imgType = ImgTypeEnum.DEFAULT
                        }
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    androidx.compose.material3.Text(
                        text = "내 사진",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (imgType == ImgTypeEnum.USER) {
                            Color.Black
                        } else {
                            colorResource(R.color.gray03)
                        },
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            imgType = ImgTypeEnum.USER
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (imgType == ImgTypeEnum.DEFAULT) {
                        Box(
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                selectedImage = 0
                                addPostViewModel.refreshDefaultImageList()
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                androidx.compose.material3.Text(
                                    text = "이미지 변경",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorResource(R.color.gray03)
                                )
                                Icon(
                                    painter = painterResource(R.drawable.ic_refresh),
                                    contentDescription = "refresh",
                                    tint = colorResource(R.color.gray03),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    } else {
                        if (selectedImageBitmap != null) {
                            Text(
                                text = "사진 변경",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorResource(R.color.gray03),
                                modifier = Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    val cropOptions = CropImageContractOptions(
                                        null,
                                        Utils.cropOption
                                    )

                                    imageCropLauncher.launch(cropOptions)
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (imgType == ImgTypeEnum.DEFAULT) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            content = {
                                items(addPostViewModel.defaultImageList.size) { imageIndex ->
                                    Surface(
                                        border = if (selectedImage == imageIndex) {
                                            BorderStroke(
                                                width = 2.dp,
                                                color = colorResource(R.color.primary_color)
                                            )
                                        } else null,
                                        modifier = Modifier.clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) {
                                            selectedImage = imageIndex
                                            addPostViewModel.selectedImageForDefault =
                                                addPostViewModel.defaultImageList[imageIndex].url.href
                                            addPostViewModel.selectedImageName =
                                                addPostViewModel.defaultImageList[imageIndex].imgName
                                        }
                                    ) {
                                        AsyncImage(
                                            model = addPostViewModel.defaultImageList[imageIndex].url.href, // 이미지 URL
                                            contentDescription = "Sample Image", // 접근성 설명
                                            modifier = Modifier.aspectRatio(1f),
                                            contentScale = ContentScale.Crop // 원하는 Modifier 추가
                                        )
                                    }
                                }
                            }
                        )
                    }
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2f)
                    ) {
                        Card(
                            modifier = Modifier
                                .width(120.dp)
                                .height(108.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.gray04)),
                            onClick = {
                                val cropOptions = CropImageContractOptions(
                                    null,
                                    Utils.cropOption
                                )

                                imageCropLauncher.launch(cropOptions)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedImageBitmap != null) {
                                    AsyncImage(
                                        model = selectedImageBitmap,
                                        contentDescription = "Sample Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_add),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.Center),
                                        tint = colorResource(R.color.gray500)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "글씨체",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.gray03)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        color = if (fontType == FontEnum.PRETENDARD) {
                            colorResource(R.color.primary_color)
                        } else {
                            colorResource(R.color.gray03)
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        onClick = {
                            fontType = FontEnum.PRETENDARD
                        }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.Text(
                                text = "고딕체",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (fontType == FontEnum.PRETENDARD) {
                                    Color.White
                                } else {
                                    colorResource(R.color.gray01)
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        color = if (fontType == FontEnum.SCHOOL_SAFE_CHALKBOARD_ERASER) {
                            colorResource(R.color.primary_color)
                        } else {
                            colorResource(R.color.gray03)
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        onClick = {
                            fontType = FontEnum.SCHOOL_SAFE_CHALKBOARD_ERASER
                        }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.Text(
                                text = "손글씨체",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (fontType == FontEnum.SCHOOL_SAFE_CHALKBOARD_ERASER) {
                                    Color.White
                                } else {
                                    colorResource(R.color.gray01)
                                },
                                fontFamily = FontFamily(
                                    Font(R.font.handwrite)
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(48.dp))
                if (cardId == null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            androidx.compose.material3.Text(
                                text = "시간 제한",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            androidx.compose.material3.Text(
                                text = "태그를 사용할 수 없고, 24시간 뒤 모든 카드가 삭제돼요",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorResource(R.color.gray03)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = storyChecked,
                            onCheckedChange = {
                                storyChecked = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                uncheckedThumbColor = Color.White,
                                checkedTrackColor = colorResource(R.color.primary_color),
                                uncheckedTrackColor = colorResource(R.color.gray03),
                                uncheckedBorderColor = Color.Transparent,
                                checkedBorderColor = Color.Transparent
                            ),
                            thumbContent = {
                                Canvas(modifier = Modifier.size(20.dp)) {
                                    drawCircle(color = Color.White)
                                }
                            },
                            modifier = Modifier
                                .width(40.dp)
                                .height(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        androidx.compose.material3.Text(
                            text = "거리 공유 제한",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        androidx.compose.material3.Text(
                            text = "다른 사람이 거리 정보를 알 수 없어요",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(R.color.gray03)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = distanceChecked,
                        onCheckedChange = {
                            distanceChecked = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            uncheckedThumbColor = Color.White,
                            checkedTrackColor = colorResource(R.color.primary_color),
                            uncheckedTrackColor = colorResource(R.color.gray03),
                            uncheckedBorderColor = Color.Transparent,
                            checkedBorderColor = Color.Transparent
                        ),
                        thumbContent = {
                            Canvas(modifier = Modifier.size(20.dp)) {
                                drawCircle(color = Color.White)
                            }
                        },
                        modifier = Modifier
                            .width(40.dp)
                            .height(24.dp)
                    )
                }
                if (cardId == null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Text(
                            text = "나만보기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = privacyChecked,
                            onCheckedChange = {
                                privacyChecked = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                uncheckedThumbColor = Color.White,
                                checkedTrackColor = colorResource(R.color.primary_color),
                                uncheckedTrackColor = colorResource(R.color.gray03),
                                uncheckedBorderColor = Color.Transparent,
                                checkedBorderColor = Color.Transparent
                            ),
                            thumbContent = {
                                Canvas(modifier = Modifier.size(20.dp)) {
                                    drawCircle(color = Color.White)
                                }
                            },
                            modifier = Modifier
                                .width(40.dp)
                                .height(24.dp)
                        )
                    }
                }
            }
        },
        sheetPeekHeight = 280.dp
    ) { innerPadding ->
        // Main Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = animatedOffset)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    tagTextFieldFocusManager.clearFocus()
                    keyboardController?.hide()
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()

                        .padding(start = 20.dp, end = 20.dp)
                ) {
                    ContentCard(
                        imgType,
                        addPostViewModel.selectedImageForDefault,
                        selectedImageForGallery,
                        content,
                        onContentChanged = { content = it },
                        fontEnum = fontType
                    )
                    Spacer(
                        modifier = Modifier.height(
                            if (parentStoryExpirationTime != null) 30.dp
                            else 12.dp
                        )
                    )
                }
                if (cardId == null) {
                    if (!storyChecked) {
                        if (tagList.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                tagList.forEachIndexed { index, tag ->
                                    Spacer(
                                        modifier = if (index == 0) {
                                            Modifier.width(20.dp)
                                        } else {
                                            Modifier.width(12.dp)
                                        }
                                    )
                                    Surface(
                                        modifier = Modifier.clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            tagList.remove(tag)
                                        },
                                        color = colorResource(R.color.gray05),
                                        shape = RoundedCornerShape(4.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(
                                                start = 16.dp,
                                                end = 16.dp,
                                                top = 4.dp,
                                                bottom = 4.dp
                                            ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_tag_cancel),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = colorResource(R.color.gray400)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                androidx.compose.material3.Text(
                                                    text = "#${tag}",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = colorResource(R.color.gray02)
                                                )
                                            }
                                        }
                                    }
                                    if (index == tagList.lastIndex) {
                                        Spacer(modifier = Modifier.width(20.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp)
                        ) {
                            androidx.compose.material3.OutlinedTextField(
                                value = tagTextField,
                                onValueChange = {
                                    tagTextField = it
                                    if (isCompleteHangul(tagTextField)) {
                                        addPostViewModel.relatedTagList.clear()
                                        addPostViewModel.getRelatedTag(
                                            tagTextField,
                                            5
                                        )
                                    }
                                },
                                placeholder = {
                                    Text(
                                        text = "#태그를 입력해주세요!",
                                        fontSize = 16.sp,
                                        color = colorResource(R.color.gray02),
                                        fontWeight = FontWeight.Medium,
                                        textDecoration = TextDecoration.None
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        isTagTextFieldFocused = focusState.isFocused
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorResource(R.color.primary_color),
                                    unfocusedBorderColor = colorResource(R.color.gray02),
                                    cursorColor = colorResource(R.color.primary_color)
                                ),
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    color = colorResource(R.color.gray02),
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = TextDecoration.None
                                ),
                                trailingIcon = {
                                    if (tagTextField.isNotBlank()) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_add),
                                            contentDescription = "Add Post",
                                            tint = colorResource(R.color.gray02),
                                            modifier = Modifier.clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                tagList.add(0, tagTextField)
                                                tagTextField = ""
                                            }
                                        )
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (tagTextField.isNotBlank()) {
                                            tagList.add(0, tagTextField)
                                            tagTextField = ""
                                        }
                                        keyboardController?.hide()
                                    }
                                ),
                            )
                            Spacer(modifier = Modifier.height(13.dp))
                            if (addPostViewModel.relatedTagList.isNotEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "#관련태그",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        mainAxisSpacing = 8.dp,
                                        crossAxisSpacing = 8.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        addPostViewModel.relatedTagList.forEach { tagHint ->
                                            TagHintChip(
                                                tagHint = tagHint.content,
                                                tagHint.count
                                            ) { tag ->
                                                tagList.add(0, tag)
                                                tagTextField = ""
                                                addPostViewModel.relatedTagList.clear()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (parentStoryExpirationTime == null) {
                        if (tagList.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                tagList.forEachIndexed { index, tag ->
                                    Spacer(
                                        modifier = if (index == 0) {
                                            Modifier.width(20.dp)
                                        } else {
                                            Modifier.width(12.dp)
                                        }
                                    )
                                    Surface(
                                        modifier = Modifier.clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            tagList.remove(tag)
                                        },
                                        color = colorResource(R.color.gray05),
                                        shape = RoundedCornerShape(4.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(
                                                start = 16.dp,
                                                end = 16.dp,
                                                top = 4.dp,
                                                bottom = 4.dp
                                            ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_tag_cancel),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = colorResource(R.color.gray400)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                androidx.compose.material3.Text(
                                                    text = "#${tag}",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = colorResource(R.color.gray02)
                                                )
                                            }
                                        }
                                    }
                                    if (index == tagList.lastIndex) {
                                        Spacer(modifier = Modifier.width(20.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp)
                        ) {
                            androidx.compose.material3.OutlinedTextField(
                                value = tagTextField,
                                onValueChange = {
                                    tagTextField = it
                                    if (isCompleteHangul(tagTextField)) {
                                        addPostViewModel.relatedTagList.clear()
                                        addPostViewModel.getRelatedTag(
                                            tagTextField,
                                            5
                                        )
                                    }
                                },
                                placeholder = {
                                    Text(
                                        text = "#태그를 입력해주세요!",
                                        fontSize = 16.sp,
                                        color = colorResource(R.color.gray02),
                                        fontWeight = FontWeight.Medium,
                                        textDecoration = TextDecoration.None
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorResource(R.color.primary_color),
                                    unfocusedBorderColor = colorResource(R.color.gray02),
                                    cursorColor = colorResource(R.color.primary_color)
                                ),
                                textStyle = TextStyle(
                                    fontSize = 16.sp,
                                    color = colorResource(R.color.gray02),
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = TextDecoration.None
                                ),
                                trailingIcon = {
                                    if (tagTextField.isNotBlank()) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_add),
                                            contentDescription = "Add Post",
                                            tint = colorResource(R.color.gray02),
                                            modifier = Modifier.clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                tagList.add(0, tagTextField)
                                                tagTextField = ""
                                            }

                                        )
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (tagTextField.isNotBlank()) {
                                            tagList.add(0, tagTextField)
                                            tagTextField = ""
                                        }
                                        keyboardController?.hide()
                                    }
                                ),
                            )
                            Spacer(modifier = Modifier.height(13.dp))
                            if (addPostViewModel.relatedTagList.isNotEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "#관련태그",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        mainAxisSpacing = 8.dp,
                                        crossAxisSpacing = 8.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        addPostViewModel.relatedTagList.forEach { tagHint ->
                                            TagHintChip(
                                                tagHint = tagHint.content,
                                                tagHint.count
                                            ) { tag ->
                                                tagList.add(0, tag)
                                                tagTextField = ""
                                                addPostViewModel.relatedTagList.clear()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (calculateRemainingTime(parentStoryExpirationTime) != "시간이 이미 지났습니다.") {
                                    PungTime(calculateRemainingTime(parentStoryExpirationTime))
                                }
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "이후에 카드가 삭제될 예정이에요",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 19.6.sp,
                                    color = colorResource(R.color.gray700)
                                )
                            }
                        }
                    }
                }
            }
        }
        if (showDialog) {
            ConfirmDialog { confirm ->
                if (confirm) {
                    if (parentCardId == null) {
                        addPostViewModel.postFeedCard(
                            !distanceChecked,
                            if (!distanceChecked) User.userInfo.latitude
                            else null,
                            if (!distanceChecked) User.userInfo.longitude
                            else null,
                            !privacyChecked,
                            storyChecked,
                            content,
                            fontType,
                            imgType,
                            if (imgType == ImgTypeEnum.DEFAULT) addPostViewModel.selectedImageName
                            else addPostViewModel.userImageUrl!!,
                            if (!storyChecked) tagList
                            else null,
                            onStatusChanged = {
                                if (it == 201) {
                                    navController.navigate(SooumNav.Home.screenRoute)
                                }
                            }
                        )
                    } else {
                        addPostViewModel.postCommentCard(cardId = parentCardId,
                            commentCardRequest = PostCommentCardRequestDataModel(
                                isDistanceShared = !distanceChecked,
                                latitude = if (!distanceChecked) User.userInfo.latitude else null,
                                longitude = if (!distanceChecked) User.userInfo.longitude else null,
                                content = content,
                                font = fontType,
                                imgType = imgType,
                                imgName = if (imgType == ImgTypeEnum.DEFAULT) addPostViewModel.selectedImageName else addPostViewModel.userImageUrl!!,
                                commentTags = if (parentStoryExpirationTime == null) tagList else null
                            ),
                            onStatusChanged = {
                                if (it == 201) {
                                    navController.navigate(SooumNav.Home.screenRoute)
                                }
                            }
                        )
                    }
                }
                showDialog = false
            }
        }
    }
}

@Composable
fun TagHintChip(tagHint: String, count: Int, onTagClick: (String) -> Unit) {
    Row(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            onTagClick(tagHint)
        }
    ) {
        Box(
            modifier = Modifier
                .border(BorderStroke(1.dp, Color.Gray), shape = RoundedCornerShape(4.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            ) {
                Text(
                    text = "#$tagHint",
                    color = colorResource(R.color.gray02),
                    fontSize = 14.sp
                )
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = if (count < 10) "0$count" else "$count",
                    color = Primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.padding(end = 10.dp))
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentCard(
    imgType: ImgTypeEnum,
    selectedImageForDefault: String?,
    selectedImageForGallery: Bitmap?,
    content: String,
    onContentChanged: (String) -> Unit,
    fontEnum: FontEnum
) {
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
            ) {
                if (imgType == ImgTypeEnum.DEFAULT) ImageLoaderForUrl(selectedImageForDefault)
                else ImageLoaderForBitmap(selectedImageForGallery)
            }
            BasicTextField(
                value = content,
                onValueChange = {
                    if (it.length <= 1000) {
                        onContentChanged(it)
                    }
                },
                modifier = Modifier
                    .padding(40.dp)
                    .align(Alignment.Center),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = if (fontEnum == FontEnum.PRETENDARD) {
                        16.sp
                    } else {
                        18.sp
                    },
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 24.sp,
                    textDecoration = null,
                    fontFamily = if (fontEnum == FontEnum.PRETENDARD) {
                        FontFamily.Default
                    } else {
                        FontFamily(
                            Font(R.font.handwrite)
                        )
                    },
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
            ) {
                // 기본 텍스트가 없으면 빈칸으로 표시
                if (content.isEmpty()) {
                    Text(
                        text = "입력하세요",
                        color = Color.White,
                        fontSize = if (fontEnum == FontEnum.PRETENDARD) {
                            16.sp
                        } else {
                            18.sp
                        },
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = if (fontEnum == FontEnum.PRETENDARD) {
                            FontFamily.Default
                        } else {
                            FontFamily(
                                Font(R.font.handwrite)
                            )
                        }
                    )
                } else {
                    it()
                }
            }
            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                text = "${content.length}/1000자",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ConfirmDialog(
    onButtonClick: (Boolean) -> Unit,
) {
    Dialog(onDismissRequest = {

    }) {
        Card(
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 22.dp,
                    bottom = 14.dp,
                    start = 14.dp,
                    end = 14.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.Text(
                    text = "카드를 작성할까요?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.gray800),
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.material3.Text(
                    text = "추가한 카드는 수정할 수 없어요",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.gray600),
                    lineHeight = 19.6.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onButtonClick(false)
                        },
                        modifier = Modifier
                            .width(130.dp)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.gray03)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "취소",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                    }
                    Button(
                        onClick = {
                            onButtonClick(true)
                        },
                        modifier = Modifier
                            .width(130.dp)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primary_color)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "카드추가",
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

// 완전한 한글 or 영어 음절 여부 확인 함수
private fun isCompleteHangul(text: String): Boolean {
    var isSyllable = false

    if (text.isEmpty()) isSyllable = false
    else {
        for (char in text) {
            if (char in '\uAC00'..'\uD7A3' || (char in 'A'..'Z') || (char in 'a'..'z')) {
                isSyllable = true
            } else {
                isSyllable = false
                break
            }
        }
    }

    return isSyllable
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val keyboardState = remember { mutableStateOf(false) }
    val view = LocalView.current
    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            keyboardState.value =
                ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.ime())
                    ?: true
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
    return keyboardState
}

@Composable
fun ImageLoaderForUrl(url: String?) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .build(),
        contentDescription = "url 형식 이미지",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ImageLoaderForBitmap(bitmap: Bitmap?) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(bitmap)
            .build(),
        contentDescription = "bitmap 형식 이미지",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}