package com.sooum.android.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.sooum.android.R
import com.sooum.android.User
import com.sooum.android.ui.common.SoonumBottomNavigation
import com.sooum.android.ui.common.SoonumNav
import com.sooum.android.ui.common.SoonumNavHost
import com.sooum.android.ui.theme.SoonumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "splash"
            ) {
                composable("splash") {
                    SplashScreen(navController)
                }
                composable("main") {
                    Main()
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
@Composable
fun SplashScreen(navController: NavController) {
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
    GetUserLocation { location ->
        User.userInfo.latitude = location?.latitude
        User.userInfo.longitude = location?.longitude
        navController.navigate("main")
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
fun Main() {
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

                SoonumNavHost(
                    navController = navController,
                    startDestination = SoonumNav.Home.screenRoute
                )
            }
        }

    }
}


@Composable
fun TagScreen() {
    Text(text = "Tag")
}

@Composable
fun ProfileScreen() {
    Text(text = "Profile")
}