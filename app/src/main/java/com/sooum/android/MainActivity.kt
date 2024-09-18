package com.sooum.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
                    val bottomNavigationItems = listOf(
                        SoonumNav.Home,
                        SoonumNav.AddPost,
                        SoonumNav.Tag,
                        SoonumNav.Profile,
                    )
                    val navController = rememberNavController()
                    Scaffold(bottomBar = {
                        NavigationBar {

                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route


                            bottomNavigationItems.forEach { screen ->
                                NavigationBarItem(
                                    label = {
                                        Text(text = stringResource(id = screen.title))
                                    },
                                    selected = currentRoute == screen.screenRoute,
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedTextColor = Color.Red,
                                        selectedIconColor = Color.Red,
                                        unselectedTextColor = Color.Blue,
                                        unselectedIconColor = Color.Blue,
                                        indicatorColor = Color.White,
                                    ),
                                    onClick = {
                                        navController.navigate(screen.screenRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = screen.icon),
                                            contentDescription = screen.screenRoute
                                        )
                                    },
                                )
                            }
                        }
                    }) { innerPadding ->
                        SoonumNavHost(
                            navController = navController,
                            startDestination = SoonumNav.Home.screenRoute,
                            modifier = Modifier.padding(innerPadding)
                        )
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
