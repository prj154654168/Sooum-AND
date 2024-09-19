package com.sooum.android.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sooum.android.AddPostScreen
import com.sooum.android.HomeScreen
import com.sooum.android.ProfileScreen
import com.sooum.android.TagScreen

@Composable
fun SoonumNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {

        composable(route = SoonumNav.Home.screenRoute) {
            HomeScreen()
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
    }
}
