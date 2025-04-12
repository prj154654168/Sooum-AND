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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.sooum.android.R
import com.sooum.android.User
import com.sooum.android.ui.common.LogInNav
import com.sooum.android.ui.common.NotificationNav
import com.sooum.android.ui.common.SooumBottomNavigation
import com.sooum.android.ui.common.SooumNav
import com.sooum.android.ui.common.SooumNavHost
import com.sooum.android.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val navController = rememberNavController()

            val android_id = Settings.Secure.getString(
                LocalContext.current.getContentResolver(),
                Settings.Secure.ANDROID_ID
            )

            mainViewModel.fetchAppVersion(this)
            mainViewModel.login(android_id, {
//                mainViewModel.fetchUnreadNotificationCount()
            })

            if (mainViewModel.isLoading == 1) {
                SooumNavHost(
                    navController = navController,
                    startDestination = NotificationNav.Notification.screenRoute,
                    mainViewModel
                )
                mainViewModel.isLoading = 2
            } else {
                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen(navController, mainViewModel)
                    }
                    composable("main") {
                        Main(mainViewModel)
                    }
                }
            }
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
    mainViewModel: MainViewModel,
) {
    val android_id = Settings.Secure.getString(
        LocalContext.current.getContentResolver(),
        Settings.Secure.ANDROID_ID
    )

    val context = LocalContext.current
    val permissions =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS)

    // 앱 버전 다이얼로그
    AppVersionDialog(mainViewModel.showDialogVersion) { updateValue ->
        if(updateValue) {
            // 업데이트 진행할 시
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
            context.startActivity(intent)
            exitProcess(0)
        }else {
            // 업데이트 진행 안할 시
            exitProcess(0)
        }
    }

    LaunchedEffect(Unit) {
        // 서버 호출 (예시로 delay로 가정)
//        mainViewModel.login(android_id, context, {
//            mainViewModel.fetchUnreadNotificationCount()
//        })

    }


    val fusedLocationProviderClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("123", "12345") // 권한 허용됨
            fetchSingleLocation(context, fusedLocationProviderClient) { location ->
                if (location != null) {
                    Log.d("123", "위치 가져왔음")
                    User.userInfo.latitude = location.latitude
                    User.userInfo.longitude = location.longitude
                }
                if (!mainViewModel.showDialogVersion.value) {
                    navController.navigate("main") {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }

            }
        } else {
            Log.d("123", "권한 거부됨")
            if (!mainViewModel.showDialogVersion.value) {
                navController.navigate("main") {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    // POST_NOTIFICATIONS 권한 런처
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // 알림 권한 결과 이후 → 위치 권한 확인 후 위치 요청 또는 바로 이동
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            fetchSingleLocation(context, fusedLocationProviderClient) { location ->
                if (location != null) {
                    Log.d("123", "위치 가져왔음")
                    User.userInfo.latitude = location.latitude
                    User.userInfo.longitude = location.longitude
                }
                if (!mainViewModel.showDialogVersion.value) {
                    navController.navigate("main") {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
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
            modifier = Modifier
                .width(235.dp)
                .height(45.dp),
            tint = Color.White
        )
    }


//    GetUserLocation { location ->
//        User.userInfo.latitude = location?.latitude
//        User.userInfo.longitude = location?.longitude
//        navController.navigate("main")
//    }

    // 권한 요청 실행
//    LaunchedEffect(Unit) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//        } else {
//            fetchSingleLocation(context, fusedLocationProviderClient) { location ->
//                if (location != null) {
//                    Log.d("123", "위치 가져왔음")
//                    User.userInfo.latitude = location.latitude
//                    User.userInfo.longitude = location.longitude
//                }
//                if (!mainViewModel.showDialogVersion.value) {
//                    navController.navigate("main") {
//                        popUpTo(navController.graph.id) { inclusive = true }
//                        launchSingleTop = true
//                    }
//                }
//            }
//        }
//    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 알림 권한부터 요청
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // 알림 권한이 이미 있는 경우 → 위치 권한 확인
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    fetchSingleLocation(context, fusedLocationProviderClient) { location ->
                        if (location != null) {
                            Log.d("123", "위치 가져왔음")
                            User.userInfo.latitude = location.latitude
                            User.userInfo.longitude = location.longitude
                        }
                        if (!mainViewModel.showDialogVersion.value) {
                            navController.navigate("main") {
                                popUpTo(navController.graph.id) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        } else {
            // Android 13 미만 → 바로 위치 권한만 처리
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                fetchSingleLocation(context, fusedLocationProviderClient) { location ->
                    if (location != null) {
                        Log.d("123", "위치 가져왔음")
                        User.userInfo.latitude = location.latitude
                        User.userInfo.longitude = location.longitude
                    }
                    if (!mainViewModel.showDialogVersion.value) {
                        navController.navigate("main") {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }


}

private fun fetchSingleLocation(
    context: Context,
    fusedLocationProviderClient: FusedLocationProviderClient,
    onLocationReceived: (Location?) -> Unit
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        onLocationReceived(null)
        Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        return
    }

    // 우선 getCurrentLocation 시도
    fusedLocationProviderClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        null
    ).addOnSuccessListener { location ->
        if (location != null) {
            Log.d("fetchSingleLocation", "위도: ${location.latitude}, 경도: ${location.longitude}")
            onLocationReceived(location)
        } else {
            Log.d("fetchSingleLocation", "getCurrentLocation 실패, requestLocationUpdates 시도")
            // 만약 최근 위치가 없다면 requestLocationUpdates 사용
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 5000
                fastestInterval = 2000
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val newLocation = locationResult.lastLocation
                    if (newLocation != null) {
                        Log.d(
                            "fetchSingleLocation",
                            "새 위치 가져옴: ${newLocation.latitude}, ${newLocation.longitude}"
                        )
                        onLocationReceived(newLocation)
                        // 위치 요청 중지
                        fusedLocationProviderClient.removeLocationUpdates(this)
                    }
                }
            }

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }.addOnFailureListener { exception ->
        Log.e("fetchSingleLocation", "위치를 가져오는 중 오류 발생: ${exception.message}")
        onLocationReceived(null)
    }
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

//    SoonumTheme {
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                if (SooumNav.isMainRoute(currentRoute) == 1) {
                    SooumBottomNavigation(navController)
                }
                if (SooumNav.isMainRoute(currentRoute) == 4) {
                    SooumBottomNavigation(navController)
                }
            },

            topBar = {
//                if (SooumNav.isMainRoute(currentRoute) == 1) {
//                    TopAppBar(
//                        title = {
//                            Image(
//                                painter = painterResource(id = R.drawable.ic_logo),
//                                contentDescription = "앱 로고",
//                                modifier = Modifier
//                                    .width(93.dp)
//                                    .height(18.dp)
//                            )
//                        },
//                        actions = {
//                            Image(
//                                painter = if (mainViewModel.unreadNotificationCount.value == 0) {
//                                    painterResource(R.drawable.ic_alarm)
//                                } else {
//                                    painterResource(R.drawable.ic_alarm_2)
//                                },
//                                contentDescription = null,
//                                modifier = Modifier
//                                    .padding(end = 20.dp)
//                                    .clickable(
//                                        interactionSource = remember { MutableInteractionSource() },
//                                        indication = null
//                                    ) {
//                                        navController.navigate(NotificationNav.Notification.screenRoute)
//                                    }
//                            )
//                        },
//                        modifier = Modifier.padding(
//                            horizontal = 4.dp,
//                            vertical = 2.dp
//                        )
//                    )
//                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding))

            if (mainViewModel.login == 1) {
                SooumNavHost(
                    navController = navController,
                    startDestination = SooumNav.Home.screenRoute,
                    mainViewModel
                )
            } else {
                SooumNavHost(
                    navController = navController,
                    startDestination = LogInNav.LogIn.screenRoute,
                    mainViewModel
                )
            }
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