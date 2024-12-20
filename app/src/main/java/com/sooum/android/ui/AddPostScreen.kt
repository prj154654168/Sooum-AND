package com.sooum.android.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.ViewTreeObserver
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.BottomSheetScaffold
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
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.sooum.android.domain.model.RelatedTagDataModel
import com.sooum.android.enums.FontEnum
import com.sooum.android.enums.ImgTypeEnum
import com.sooum.android.ui.common.SoonumNav
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.viewmodel.AddPostViewModel
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostScreen(navController: NavHostController, cardId: String? = null) {

    val isFeed = false
    val parentIsStory = false   //이거 값 2개 navigate 할 때 전달 받아야함
    Log.d("123", "${cardId}")
    val parentId = cardId?.toLong()

    val addPostViewModel: AddPostViewModel = hiltViewModel()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
    )

    //기본 이미지 : true, 내 사진 : false
    var imgType by remember { mutableStateOf(ImgTypeEnum.DEFAULT) }

    //스위치 상태
    var storyChecked by remember { mutableStateOf(false) }
    var distanceChecked by remember { mutableStateOf(false) }
    var privacyChecked by remember { mutableStateOf(false) }

    //고딕체 : true, 손글씨체 : false
    var fontType by remember { mutableStateOf(FontEnum.PRETENDARD) }

    var selectedImage by remember { mutableStateOf(0) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
//    var selectedImageForDefault by remember { mutableStateOf<String?>(addPostViewModel.nowImage) }
    var selectedImageForGallery by remember { mutableStateOf<Bitmap?>(null) }

    var content by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    var tagTextField by remember { mutableStateOf("") }

    val tagList by remember { mutableStateOf(mutableListOf<String>()) }

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        addPostViewModel.getDefaultImageList()
    }

    var tagHintList by remember {
        mutableStateOf<List<RelatedTagDataModel.Embedded.RelatedTag>>(
            emptyList()
        )
    }
    tagHintList = addPostViewModel.relatedTagList
    val context = LocalContext.current

    // 이미지 선택 후 반환된 결과를 처리하는 런처
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (selectedImageUri != null && uri == null) {
            //이미 선택된 이미지가 있을 때, 다시 갤러리에 들어가서 사진을 선택 안하면 기존에 selectedImageUri가 null 되는 것을 방지
        } else {
            selectedImageUri = uri
            val bitmap = selectedImageUri?.let { uriToBitmap(context, it) }
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            addPostViewModel.getImageUrl(byteArray)

        }
    }

    //여기입니다
    var selectedImageBitmap: Bitmap? by remember { mutableStateOf(null) }

    //여기입니다
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
                    selectedImageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()
                    addPostViewModel.getImageUrl(byteArray)
                }

            } else {
                Log.d("AddPostScreen", "ImageCropping error: ${result.error}")
            }
        }

    val scrollState2 = rememberScrollState()

    val keyboardState by keyboardAsState()
    Log.d("keyboard", keyboardState.toString())

    LaunchedEffect(keyboardState) {
        if (keyboardState) {
            scaffoldState.bottomSheetState.hide()
        }
        else {
            scaffoldState.bottomSheetState.show()
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
                                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
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
                                if (parentId == null) {
                                    addPostViewModel.postFeedCard(
                                        distanceChecked,
                                        if (distanceChecked) User.userInfo.latitude
                                        else null,
                                        if (distanceChecked) User.userInfo.longitude
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
                                                navController.navigate(SoonumNav.Home.screenRoute)
                                            }
                                        }
                                    )
                                }
                                else {
                                    addPostViewModel.postCommentCard(cardId = parentId,
                                        commentCardRequest = PostCommentCardRequestDataModel(
                                        isDistanceShared = distanceChecked,
                                        latitude = if (distanceChecked) User.userInfo.latitude else null,
                                        longitude = if (distanceChecked) User.userInfo.longitude else null,
                                        content = content,
                                        font = fontType,
                                        imgType = imgType,
                                        imgName = if (imgType == ImgTypeEnum.DEFAULT) addPostViewModel.selectedImageName else addPostViewModel.userImageUrl!!,
                                        commentTags = tagList),
                                        onStatusChanged = {
                                            if (it == 201) {
                                                navController.navigate(SoonumNav.Home.screenRoute)
                                            }
                                        }
                                    )
                                }
                            },
                        text = "작성하기",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (content.isEmpty()) colorResource(R.color.gray700) else  colorResource(R.color.blue300)
                    )
                }
            )
        },
        sheetContainerColor = Color.White,
        scaffoldState = scaffoldState,
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
//                                    galleryLauncher.launch("image/*")
                                    //여기입니다
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
                                            addPostViewModel.selectedImageForDefault = addPostViewModel.defaultImageList[imageIndex].url.href
                                            addPostViewModel.selectedImageName = addPostViewModel.defaultImageList[imageIndex].imgName
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
//                                galleryLauncher.launch("image/*")
                                //여기입니다
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
                                    // 선택된 이미지가 있으면 이미지 표시
                                    AsyncImage(
                                        model = selectedImageBitmap, // 이미지 URL
                                        contentDescription = "Sample Image", // 접근성 설명
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop// 원하는 Modifier 추가
                                    )
                                }
                                else {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_add),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp).align(Alignment.Center),
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
                .imePadding()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(scrollState2)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()

                        .padding(start = 20.dp, end = 20.dp)
                ) {
                    ContentCard(addPostViewModel, imgType, addPostViewModel.selectedImageForDefault, selectedImageForGallery, content, onContentChanged = { content = it }, fontEnum = fontType)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (!storyChecked) {
                    if (!parentIsStory) {
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
                                        addPostViewModel.getRelatedTag(
                                            tagTextField,
                                            5
                                        ) //int값에 뭐가 들어가야되는지 모르겠음
                                        Log.d("tag", "api호출")
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
//                        .border(width = 1.dp, color = colorResource(R.color.gray02), shape = RoundedCornerShape(12.dp))
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
                            if (tagHintList.isNotEmpty()) {
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
                                        tagHintList.forEach { tagHint ->
                                            TagHintChip(tagHint = tagHint.content, tagHint.count) { tag ->
                                                tagList.add(0, tag)
                                                tagTextField = ""
                                                tagHintList = emptyList()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().align(Alignment.Center)
                            ) {
                                Surface(
                                    color = colorResource(R.color.blue300),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 2.5.dp, bottom = 2.5.dp)
                                ) {
                                    Text(
                                        text = "14 : 00 : 00",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colorResource(R.color.gray_white),
                                        lineHeight = 19.6.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "이후에 카드가 삭제될 예정이에요",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colorResource(R.color.gray700),
                                    lineHeight = 19.6.sp
                                )
                            }
                        }
                    }
                }
            }
        }
//        Column(Modifier.padding(20.dp)) {
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .aspectRatio(1 / 0.9f)
//                    .padding(bottom = 10.dp),
//                shape = RoundedCornerShape(40.dp),
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                ) {
//                    ImageLoader("https://search.pstatic.net/sunny/?src=https%3A%2F%2Fus.123rf.com%2F450wm%2Fvantuz%2Fvantuz1506%2Fvantuz150600251%2F41134623-%25EB%2593%25B1%25EB%25B6%2588%25EC%259D%2580-%25EC%2596%25B4%25EB%2591%2590%25EC%259A%25B4-%25EB%25B0%25B0%25EA%25B2%25BD%25EC%259D%2584-%25EC%25A1%25B0%25EB%25AA%2585%25ED%2595%259C%25EB%258B%25A4-%25EB%25B2%25A1%25ED%2584%25B0-%25EC%259D%25B4%25EB%25AF%25B8%25EC%25A7%2580%25EC%259E%2585%25EB%258B%2588%25EB%258B%25A4-.jpg%3Fver%3D6&type=sc960_832")
//                    BasicTextField(
//                        value = content,
//                        onValueChange = {
//                            if (it.length <= 1000) {
//                                content = it
//                            }
//                        },
//                        modifier = Modifier
////                            .fillMaxWidth(0.75f)
////                            .fillMaxHeight(0.72f)
//                            .padding(40.dp)
//                            .align(Alignment.Center)
//
//                            .verticalScroll(scrollState),
//                        textStyle = TextStyle(
//                            color = Color.White,
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Bold,
//                            textDecoration = TextDecoration.None,
//                            lineHeight = 28.8.sp),
//                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
//                    ) {
//                        // 기본 텍스트가 없으면 빈칸으로 표시
//                        if (content.isEmpty()) {
//                            Text(
//                                text = "입력하세요...",
//                                color = Color.White,
//                                style = TextStyle(color = Color.White)
//                            )
//                        } else {
//                            it()
//                        }
//                    }
//
//                    Text(
//                        modifier = Modifier
//                            .align(Alignment.BottomCenter)
//                            .padding(bottom = 12.dp),
//                        text = "${content.length} / 1000",
//                        color = Color.White,
//                        fontSize = 14.sp
//                    )
//                }
//            }
//            var tagText by remember { mutableStateOf("") }
//            var tags by remember { mutableStateOf(listOf<String>()) }
//            val tagHint: List<Pair<String, Int>> = listOf(
//                "UX기획" to 6,
//                "UX리서처" to 4,
//                "UIUX를 공부하는 효율적인 방법" to 3,
//                "UI디자인이에요" to 2,
//                "UI가독성" to 1
//            )
//            if (tags.isNotEmpty()) {
//                LazyRow(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 10.dp)
//                ) {
//                    items(tags) { tag ->
//                        TagChip(tag = tag) {
//                            tags = tags.filterNot { it == tag }
//                        }
//                    }
//                }
//            }
//            Row(modifier = Modifier.fillMaxWidth()) {
//                OutlinedTextField(
//                    value = tagText,
//                    onValueChange = { tagText = it },
//                    placeholder = { Text(text = "태그를 입력해주세요",fontSize = 16.sp) },
//                    colors = TextFieldDefaults.outlinedTextFieldColors(
//                        focusedBorderColor = Primary,
//                        unfocusedBorderColor = Color.Gray,
//                        cursorColor = Primary
//                    ),
//                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(56.dp)
//                        .padding(end = 8.dp),
//                    trailingIcon = {
//                        if (tagText.isNotBlank()) {
//                            IconButton(onClick = {
//                                tags = tags + tagText
//                                tagText = ""
//                            }) {
//                                Icon(
//                                    painter = painterResource(R.drawable.ic_add),
//                                    contentDescription = "Add Tag"
//                                )
//                            }
//                        } else {
//                            Spacer(modifier = Modifier.size(24.dp))
//                        }
//                    },
//                    keyboardOptions = KeyboardOptions.Default.copy(
//                        imeAction = ImeAction.Done
//                    ),
//                    keyboardActions = KeyboardActions(
//                        onDone = {
//                            if (tagText.isNotBlank()) {
//                                tags = tags + tagText
//                                tagText = ""
//                            }
//                        }
//                    )
//                )
//
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            if (tagText != "") {
//                Text("#관련 태그")
//                FlowRow(
//                    mainAxisSpacing = 8.dp,
//                    crossAxisSpacing = 8.dp,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    tagHint.forEach { tagHint ->
//                        TagHintChip(tagHint = tagHint.first, tagHint.second)
//                    }
//                }
//            }
//        }
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
fun uriToBitmap(context: Context, uri: Uri): Bitmap {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    return BitmapFactory.decodeStream(inputStream) ?: throw IllegalArgumentException("Cannot decode URI: $uri")
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
fun ContentCard(addPostViewModel: AddPostViewModel, imgType: ImgTypeEnum, selectedImageForDefault: String?, selectedImageForGallery: Bitmap?, content: String, onContentChanged: (String) -> Unit, fontEnum: FontEnum) {
    val scrollState = rememberScrollState()
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
                    .align(Alignment.Center)
                    .verticalScroll(scrollState),
                textStyle = TextStyle(color = Color.White,
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
                    }),
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
//    val gradientBrush = Brush.verticalGradient(
//        colors = listOf(Color.Black.copy(alpha = 0f), Color.Black.copy(alpha = 0.6f)),
//        startY = 0f,
//        endY = 60f // 그라데이션의 높이를 60dp로 설정
//    )
//
//    var text by remember { mutableStateOf("") }
//    val scrollState = rememberScrollState()
//    var textCount by remember { mutableStateOf(0) }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .aspectRatio(1 / 0.9f),
//        shape = RoundedCornerShape(40.dp),
//        onClick = { }
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            ImageLoader("")
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier
//                    .fillMaxWidth(0.75f)
//                    .fillMaxHeight(0.72f)
//            ) {
//                Row(modifier = Modifier.fillMaxSize()) {
//                    Box(modifier = Modifier
//                        .weight(1f)
//                        .verticalScroll(scrollState)) {
//                        TextField(
//                            value = text,
//                            onValueChange = { newText -> text = newText },
//                            label = { androidx.compose.material3.Text("Enter text") },
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                }
//            }
//            androidx.compose.material3.Text(
//                text = "${textCount}/1000자",
//                fontWeight = FontWeight.SemiBold,
//                fontSize = 14.sp
//            )
//        }

//            Box(
//                modifier = Modifier
//                    .background(
//                        Color.Black.copy(alpha = 0.7f),
//                        shape = RoundedCornerShape(24.dp)
//                    )
//                    .fillMaxWidth(0.75f)
//                    .align(Alignment.Center)
//                    .padding(4.dp)
//            ) {
//                Text(
//                    modifier = Modifier
//                        .align(Alignment.Center)
//                        .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
//                    text = item.content,
//                    color = Color.White,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold,
//                    maxLines = 4,
//                    overflow = TextOverflow.Ellipsis,
//                    lineHeight = 28.8.sp,
//                )
//            }
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(60.dp)
//                    .background(gradientBrush)
//                    .align(Alignment.BottomCenter)
//            )
//            Box(
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(end = 26.dp, bottom = 24.dp)
//            ) {
//                LatestCardInfo(item)
//            }
//        }
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


    Log.d("tag", isSyllable.toString())
    return isSyllable
}

@Composable
fun keyboardAsState() : State<Boolean> {
    val keyboardState = remember { mutableStateOf(false) }
    val view = LocalView.current
    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            keyboardState.value = ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
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
        contentDescription = "카드 이미지",
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
        contentDescription = "카드 이미지",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}