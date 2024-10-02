package com.sooum.android.ui.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sooum.android.AddPostScreen
import com.sooum.android.HomeScreen
import com.sooum.android.ProfileScreen
import com.sooum.android.TagScreen
import com.sooum.android.ui.DetailScreen

@Composable
fun SoonumNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
    isVisible: Boolean,
    scrollState: LazyListState,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = SoonumNav.Home.screenRoute) {
            HomeScreen(isVisible, scrollState, navController)
        }

        composable(route = SoonumNav.AddPost.screenRoute) {
            AddPostScreen()
        }

        composable(route = SoonumNav.Tag.screenRoute) {
            TagScreen()
        }
        composable(route = SoonumNav.Profile.screenRoute) {
            ProfileScreen()
        }
        composable(route = PostNav.Detail.screenRoute) {
            DetailScreen()
        }
    }
}
