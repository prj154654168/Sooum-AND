package com.sooum.android.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.sooum.android.R
import com.sooum.android.User
import com.sooum.android.ui.common.LogInNav
import com.sooum.android.ui.common.SoonumBottomNavigation
import com.sooum.android.ui.common.SoonumNav
import com.sooum.android.ui.common.SoonumNavHost
import com.sooum.android.ui.theme.SoonumTheme
import com.sooum.android.ui.viewmodel.LogInViewModel
import com.sooum.android.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        val mainViewModel : MainViewModel by viewModels()

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "splash"
            ) {
                composable("splash") {
                    SplashScreen(navController,mainViewModel)
                }
                composable("main") {
                    Main(mainViewModel)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("sooum-channel", "sooum", importance)

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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
fun SplashScreen(navController: NavController, mainViewModel: MainViewModel) {
    val android_id = Settings.Secure.getString(
        LocalContext.current.getContentResolver(),
        Settings.Secure.ANDROID_ID
    )
    // val viewModel: LogInViewModel = viewModel()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        // 서버 호출 (예시로 delay로 가정)
        mainViewModel.login(android_id, context)
    }
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 권한이 허용된 경우 위치를 가져옵니다.
            fetchSingleLocation(context, fusedLocationProviderClient, onLocationReceived = { location ->
                User.userInfo.latitude = location?.latitude
                User.userInfo.longitude = location?.longitude
                navController.navigate("main") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    } // 백 스택 비우기
                    launchSingleTop = true // 중복된 화면 생성 방지
                }
            })
        }
        else {
            navController.navigate("main") {
                popUpTo(navController.graph.id) {
                    inclusive = true
                } // 백 스택 비우기
                launchSingleTop = true // 중복된 화면 생성 방지
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

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

}

private fun fetchSingleLocation(context: Context, fusedLocationProviderClient: FusedLocationProviderClient, onLocationReceived: (Location?) -> Unit) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null // Optional CancellationToken, null로 설정 가능
        ).addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(location)
                Log.d("123", "위도: ${location.latitude}, 경도: ${location.longitude}")
            }
        }.addOnFailureListener { exception ->
            onLocationReceived(null)
            Log.e("123", "위치를 가져오는 중 오류 발생: ${exception.message}")
        }
    } else {
        onLocationReceived(null)
        Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()

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

    SoonumTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                bottomBar = {
                    if (SoonumNav.isMainRoute(currentRoute) == 1) {
                        SoonumBottomNavigation(navController)
                    }
                    if (SoonumNav.isMainRoute(currentRoute) == 4) {
                        SoonumBottomNavigation(navController)
                    }
                },

                topBar = {//top bar 추후 수정 필요
                    if (SoonumNav.isMainRoute(currentRoute) == 1) {
                        TopAppBar(
                            title = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_logo),
                                    contentDescription = "앱 로고",
                                    modifier = Modifier
                                        .width(93.dp)
                                        .height(18.dp)
                                )
                            },
                            actions = {
                                IconButton(onClick = {
                                    /* 버튼 클릭 이벤트 */
                                }) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_alarm),
                                        contentDescription = "Alarm",
                                        colorFilter = ColorFilter.tint(colorResource(R.color.gray01))
                                    )
                                }
                            },
                            modifier = Modifier.padding(
                                horizontal = 4.dp,
                                vertical = 2.dp
                            )
                        )
                    }
                },
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding))

                if (mainViewModel.login == 1) {
                    SoonumNavHost(
                        navController = navController,
                        startDestination = SoonumNav.Home.screenRoute
                    )
                }else{
                    SoonumNavHost(
                        navController = navController,
                        startDestination = LogInNav.LogIn.screenRoute
                    )
                }
            }
        }
    }
}