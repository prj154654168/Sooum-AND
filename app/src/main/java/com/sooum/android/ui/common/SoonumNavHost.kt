package com.sooum.android.ui.common


import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sooum.android.ui.ProfileScreen
import com.sooum.android.ui.TagScreen
import com.sooum.android.ui.AddPostScreen
import com.sooum.android.ui.DetailScreen
import com.sooum.android.ui.HomeScreen
import com.sooum.android.ui.ReportScreen

@Composable
fun SoonumNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        modifier = Modifier.padding(top = 64.dp),//topbar만큼 위로 뛰우김
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = SoonumNav.Home.screenRoute) {
            HomeScreen(navController)
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
        composable(route = "${PostNav.Detail.screenRoute}/{cardId}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")
            DetailScreen(navController, cardId)
        }
        composable(route = "${PostNav.Report.screenRoute}/{cardId}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId").toString()
            ReportScreen(navController, cardId)
        }
    }
}
