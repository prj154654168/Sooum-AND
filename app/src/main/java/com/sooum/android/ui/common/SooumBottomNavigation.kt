package com.sooum.android.ui.common

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sooum.android.ui.theme.Gray1
import com.sooum.android.ui.theme.Primary
import com.sooum.android.ui.theme.White100
import com.sooum.android.ui.theme.White70
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun SooumBottomNavigation(navController: NavHostController) {
    val bottomNavigationItems = listOf(
        SooumNav.Home,
        SooumNav.AddPost,
        SooumNav.Tag,
        SooumNav.Profile,
    )
    NavigationBar(
        modifier = Modifier
            .padding(20.dp)
            .graphicsLayer {
                shape = RoundedCornerShape(
                    100.dp
                )
                clip = true

            }
            .height(70.dp), containerColor = White70
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Primary,
                    selectedTextColor = White100,
                    selectedIconColor = White100,
                    unselectedTextColor = Gray1,
                    unselectedIconColor = Gray1,
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
                        contentDescription = screen.screenRoute,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier
                    .padding(4.dp)
                    .drawCircleBehind(if (isSelected) Primary else Color.Transparent),
            )
        }
    }
}

private object NoRippleInteractionSource : MutableInteractionSource {

    override val interactions: Flow<Interaction> = emptyFlow()

    override suspend fun emit(interaction: Interaction) = Unit

    override fun tryEmit(interaction: Interaction) = true
}//ripple 효과 없애기

fun Modifier.drawCircleBehind(
    color: Color,
) = drawBehind {
    drawCircle(color = color)
}//클릭했을때 동그라미 효과
