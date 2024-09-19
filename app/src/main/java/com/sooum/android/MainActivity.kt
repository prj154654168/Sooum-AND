package com.sooum.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.sooum.android.ui.common.SoonumBottomNavigation
import com.sooum.android.ui.common.SoonumNav
import com.sooum.android.ui.common.SoonumNavHost
import com.sooum.android.ui.theme.SoonumTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoonumTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController = rememberNavController()
                    Scaffold(bottomBar = {
                        SoonumBottomNavigation(navController)
                    }) { innerPadding ->
                        SoonumNavHost(
                            navController = navController,
                            startDestination = SoonumNav.Home.screenRoute,
                            modifier = Modifier.padding(innerPadding)
                        )
                        LazyColumn() {
                            items(5000) {
                                Text(text = "하위하위하위하위하위하위", fontSize = 25.sp)
                            }
                        }//test 화면
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Text(text = "Home")
}

@Composable
fun AddPostScreen() {
    Text(text = "AddPost")
}

@Composable
fun TagScreen() {
    Text(text = "Tag")
}

@Composable
fun ProfileScreen() {
    Text(text = "Profile")
}
