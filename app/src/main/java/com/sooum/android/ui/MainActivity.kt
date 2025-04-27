package com.sooum.android.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.sooum.android.FcmEventBus
import com.sooum.android.R
import com.sooum.android.SooumApplication
import com.sooum.android.User
import com.sooum.android.enums.UserStatusEnum
import com.sooum.android.ui.common.LogInNav
import com.sooum.android.ui.common.SooumBottomNavigation
import com.sooum.android.ui.common.SooumNav
import com.sooum.android.ui.common.SooumNavHost
import com.sooum.android.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)

        // 계정 이관
        // MainActivity (or any LifecycleOwner)
        lifecycleScope.launch {
            // STARTED 이상 상태가 유지되는 동안에만 블록이 실행
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // transferEventFlow 가 emit 될 때마다 처리
                FcmEventBus.transferEventFlow.collect { event ->
                    Log.d("FCM", "MainActivity에서 계정 이관 이벤트 감지")

                    // 전체 데이터 삭제
                    SooumApplication().clearAllPrefs()

                    // MainActivity 재실행 (모두 클리어)
                    val intent = Intent(this@MainActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
            }
        }


        //로그인 성공했음 화면 네비 다시 이어서 시작

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val navController = rememberNavController()

            SooumNavHost(
                navController = navController,
                startDestination = "splash",
                mainViewModel = mainViewModel,
            )

//            if (mainViewModel.isLoading == 1) {
//                SooumNavHost(
//                    navController = navController,
//                    startDestination = NotificationNav.Notification.screenRoute,
//                    mainViewModel
//                )
//                mainViewModel.isLoading = 2
//            } else {
//                NavHost(
//                    navController = navController,
//                    startDestination = "splash"
//                ) {
//                    composable("splash") {
//                        SplashScreen(navController, mainViewModel)
//                    }
//                    composable("main") {
//                        Main(mainViewModel)
//                    }
//                }
//            }
        }

//        requestNotificationPermission()
    }
    fun ComponentActivity.requestNotificationPermission() {
        // Android 13(API 33) 이상인지 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한 요청 다이얼로그 띄우기
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // TODO: 알림 타고 들어왔을때 처리
    private fun handleIntent(intent: Intent?) {
        intent ?: return
        Log.d("MainActivity", "handleIntent 호출됨, extras=${intent.extras}")
        if (intent.getBooleanExtra("fromPush", false)) {
            val type = intent.getStringExtra("notificationType")
            val notificationId = intent.getStringExtra("notificationId")
            val targetCardId = intent.getStringExtra("targetCardId")
            Log.d("MainActivity", "푸시 클릭으로 진입: type=$type, id=$notificationId, cardId=$targetCardId")
            // TODO: 여기서 딥링크/화면 이동 처리
        }
    }

    // 권한 요청 결과 처리
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 사용자가 알림 권한을 허용함
            Log.d("Permission", "알림 권한 허용됨")
        } else {
            // 사용자가 알림 권한을 거부함
            Log.d("Permission", "알림 권한 거부됨")
        }
    }

}

/*
경우의 수
1. 첫 실행, 권한 물어보고 허락하면 데이터 로딩 후 메인 넘어가기
2. 첫 실행, 권한 물어보고 거부하면 데이터 로딩 후 메인 넘어가기
3. 실행 이력 있음, 권한 있으면 데이터 로딩 후 메인 넘어가기
4. 실행 이력 있음, 권한 없으면 데이터 로딩 후 메인 넘어가기

위치 권한이 있으면 스플래시 2초 후 바로 메인 홈
위치 권한이 없으면 스플래시 or 권한 요청 화면에서 권한 요청 dialog 띄우기

만약 위치 권한이 없을 때, 거리별 탭으로 들어가면 아래 만든 커스텀 다이얼로그 띄우고 취소 누르면 그냥 dismiss, 설정 누르면 위치 권한 dialog 띄우기
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SplashScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // nextScreen 관찰
    val nextScreen by mainViewModel.nextScreen.collectAsState()

    // 위치 권한 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        fetchLocationAndProceed(context, fusedLocationProviderClient, navController, nextScreen)
    }

    // 알림 권한 런처
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        requestLocationPermission(context, permissionLauncher, fusedLocationProviderClient, navController, nextScreen)
    }

    // 앱 버전 다이얼로그
    AppVersionDialog(mainViewModel.showDialogVersion) { updateValue ->
        if (updateValue) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
            context.startActivity(intent)
            exitProcess(0)
        } else {
            exitProcess(0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.primary_color)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo 이미지",
            modifier = Modifier.width(235.dp).height(45.dp),
            tint = Color.White
        )
    }

    LaunchedEffect(Unit) {
        val needUpdate = mainViewModel.checkAppVersion(context)
        if (needUpdate) {
            mainViewModel.showDialogVersion.value = true
        } else {
            mainViewModel.refactLogin(androidId) { status, dateTime ->
                when (status) {
                    UserStatusEnum.MEMBER -> {
                        mainViewModel.setNextScreen("main")
                        requestPermissions(context, notificationPermissionLauncher, permissionLauncher)
                    }
                    UserStatusEnum.NON_MEMBER -> {
                        mainViewModel.setNextScreen(LogInNav.LogIn.screenRoute)
                        requestPermissions(context, notificationPermissionLauncher, permissionLauncher)
                    }
                    UserStatusEnum.SUSPENDED, UserStatusEnum.RESTRICTED -> {
                        val encodedStatus = Uri.encode(status.name)
                        val encodedExtraInfo = Uri.encode(dateTime ?: "정보 없음")
                        Log.d(
                            "Splash",
                            "encodedStatus : $encodedStatus, encodedExtraInfo : $encodedExtraInfo"
                        )
                        navController.navigate("${LogInNav.LogIn.screenRoute}?status=SUSPENDED&extraInfo=$encodedExtraInfo") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}

// 권한 요청 흐름 (알림 → 위치 순서로 요청)
private fun requestPermissions(
    context: Context,
    notificationPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    } else {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

// 알림 권한 이후 → 위치 권한 요청
private fun requestLocationPermission(
    context: Context,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    fusedLocationProviderClient: FusedLocationProviderClient,
    navController: NavController,
    nextScreen: String?
) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
    ) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    } else {
        fetchLocationAndProceed(context, fusedLocationProviderClient, navController, nextScreen)
    }
}

// 위치 수집 완료 후 다음 화면으로 이동
private fun fetchLocationAndProceed(
    context: Context,
    fusedLocationProviderClient: FusedLocationProviderClient,
    navController: NavController,
    nextScreen: String?
) {
    fetchSingleLocation(context, fusedLocationProviderClient) { location ->
        if (location != null) {
            var latitude = location.latitude
            var longitude = location.longitude

            if (longitude < 0) {
                longitude *= -1
            }

            User.userInfo.latitude = latitude
            User.userInfo.longitude = longitude
        }
        nextScreen?.let {
            navController.navigate(it) {
                popUpTo("splash") { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}

// 위치 수집 로직
private fun fetchSingleLocation(
    context: Context,
    fusedLocationProviderClient: FusedLocationProviderClient,
    onLocationReceived: (Location?) -> Unit
) {
    if (ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        onLocationReceived(null)
        Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        return
    }

    // 1. lastLocation 먼저 시도
    fusedLocationProviderClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                Log.d("fetchSingleLocation", "lastLocation 가져옴: ${location.latitude}, ${location.longitude}")
                onLocationReceived(location)
            } else {
                // 2. 없으면 currentLocation 시도
                fusedLocationProviderClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, null
                ).addOnSuccessListener { currentLocation ->
                    if (currentLocation != null) {
                        Log.d("fetchSingleLocation", "currentLocation 가져옴: ${currentLocation.latitude}, ${currentLocation.longitude}")
                        onLocationReceived(currentLocation)
                    } else {
                        // 3. 그래도 실패하면 새 위치 요청
                        requestNewLocation(context, fusedLocationProviderClient, onLocationReceived)
                    }
                }.addOnFailureListener {
                    onLocationReceived(null)
                }
            }
        }.addOnFailureListener {
            onLocationReceived(null)
        }
}


// 새 위치 요청
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
private fun requestNewLocation(
    context: Context,
    fusedLocationProviderClient: FusedLocationProviderClient,
    onLocationReceived: (Location?) -> Unit
) {
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, // 우선 정확도 높게
        2000L // 2초 요청 주기
    ).apply {
        setWaitForAccurateLocation(true) // 정확한 GPS 잡힐 때까지 기다려라
        setMaxUpdates(1) // 딱 1번만 위치 받아오기
    }.build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            if (location != null) {
                Log.d("requestNewLocation", "새 위치 가져옴: ${location.latitude}, ${location.longitude}")
                onLocationReceived(location)
            } else {
                onLocationReceived(null)
            }
            fusedLocationProviderClient.removeLocationUpdates(this)
        }
    }

    fusedLocationProviderClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}


@Composable
fun RequestLocationPermission(onGranted: () -> Unit, onDenied: () -> Unit) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onGranted()
        } else {
            onDenied()
        }
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            onGranted()
        }
    }
}

@Composable
fun GetUserLocation(onLocationReceived: (Location?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
    val context = LocalContext.current

    RequestLocationPermission(
        onGranted = {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    onLocationReceived(location)
                    Log.d("MainActivity", "location received ${location?.latitude}")
                }
            } else {
                // 권한이 없을 때 null로 전달
                onLocationReceived(null)
            }
        },
        onDenied = {
            // 권한 거부 시 null로 전달
            onLocationReceived(null)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoute =
        listOf(SooumNav.Home.screenRoute, SooumNav.Tag.screenRoute, SooumNav.Profile.screenRoute)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                if (currentRoute in bottomBarRoute) {
                    SooumBottomNavigation(navController)
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding))
            SooumNavHost(
                navController = navController,
                startDestination = SooumNav.Home.screenRoute,
                mainViewModel
            )
        }
    }
}

@Composable
fun AppVersionDialog(
    showDialogState: MutableState<Boolean>,
    onButtonClick: (Boolean) -> Unit,
) {
    if (showDialogState.value) {
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
                        text = "업데이트 안내",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.gray800),
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    androidx.compose.material3.Text(
                        text = "안정적인 서비스를 사용을 위해\n최신버전으로 업데이트해주세요.",
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
                                text = "종료하기",
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
                                text = "업데이트",
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
}
//}