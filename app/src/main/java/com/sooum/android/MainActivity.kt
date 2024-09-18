package com.sooum.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sooum.android.ui.common.SoonumNav
import com.sooum.android.ui.common.SoonumNavHost
import com.sooum.android.ui.theme.Gray
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.theme.SoonumTheme
import com.sooum.android.ui.theme.White100
import com.sooum.android.ui.theme.White70
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

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
                        NavigationBar(
                            modifier = Modifier
                                .padding(20.dp)
                                .navigationBarsPadding()
                                .graphicsLayer {
                                    shape = RoundedCornerShape(
                                        100.dp
                                    )
                                    clip = true

                                }, containerColor = White70
                        ) {

                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route

                            bottomNavigationItems.forEach { screen ->
                                val isSelected = currentRoute == screen.screenRoute
                                NavigationBarItem(
                                    interactionSource = NoRippleInteractionSource,
                                    label = {
                                        Text(
                                            text = stringResource(id = screen.title),
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    selected = isSelected,
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Primary,
                                        selectedTextColor = White100,
                                        selectedIconColor = White100,
                                        unselectedTextColor = Gray,
                                        unselectedIconColor = Gray,

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
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .drawCircleBehind(if (isSelected) Primary else Color.Transparent),
                                )
                            }
                        }
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
                        }
                    }


                }
            }
        }
    }
}

private object NoRippleInteractionSource : MutableInteractionSource {

    override val interactions: Flow<Interaction> = emptyFlow()

    override suspend fun emit(interaction: Interaction) = Unit

    override fun tryEmit(interaction: Interaction) = true
}

fun Modifier.drawCircleBehind(
    color: Color,
) = drawBehind {
    drawCircle(color = color)
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
