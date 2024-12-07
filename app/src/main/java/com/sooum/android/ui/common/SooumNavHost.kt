package com.sooum.android.ui.common


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sooum.android.ui.AddPostScreen
import com.sooum.android.ui.DetailScreen
import com.sooum.android.ui.HomeScreen
import com.sooum.android.ui.myprofile.ModifyProfileScreen
import com.sooum.android.ui.myprofile.MyProfileScreen
import com.sooum.android.ui.ReportScreen
import com.sooum.android.ui.myprofile.SettingScreen
import com.sooum.android.ui.TagListScreen
import com.sooum.android.ui.TagScreen
import com.sooum.android.ui.myprofile.EnterUserCodeScreen
import com.sooum.android.ui.myprofile.MakeUserCodeScreen
import com.sooum.android.ui.myprofile.MyCommentHistoryScreen
import com.sooum.android.ui.myprofile.NoticeScreen
import com.sooum.android.ui.myprofile.UserDeleteScreen
import com.sooum.android.ui.onboarding.AgreeScreen
import com.sooum.android.ui.onboarding.LogInProfileScreen
import com.sooum.android.ui.onboarding.LogInScreen
import com.sooum.android.ui.onboarding.NickNameScreen

@Composable
fun SoonumNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        modifier = Modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = SoonumNav.Home.screenRoute) {
            HomeScreen(navController)
        }

        composable(route = SoonumNav.AddPost.screenRoute) {
            AddPostScreen(navController)
        }

        composable(
            route = "addCommentCard/{cardId}",
            arguments = listOf(navArgument("cardId") { type = NavType.StringType })
        )
        { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")
            AddPostScreen(navController, cardId)
        }

        composable(route = SoonumNav.Tag.screenRoute) {
            TagScreen(navController)
        }
        composable(route = SoonumNav.Profile.screenRoute) {
            MyProfileScreen(navController)
        }
        composable(route = "${PostNav.Detail.screenRoute}/{cardId}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")
            DetailScreen(navController, cardId)
        }
        composable(route = "${PostNav.Report.screenRoute}/{cardId}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId").toString()
            ReportScreen(navController, cardId)
        }
        composable(route = LogInNav.LogIn.screenRoute) {
            LogInScreen(navController)
        }
        composable(route = LogInNav.Agree.screenRoute) {
            AgreeScreen(navController)
        }
        composable(route = LogInNav.NickName.screenRoute) {
            NickNameScreen(navController)
        }
        composable(route = LogInNav.LogInProfile.screenRoute) {
            LogInProfileScreen(navController)
        }

        composable(route = MyProfile.ProfileModify.screenRoute) {
            ModifyProfileScreen(navController)
        }
        composable(route = MyProfile.Setting.screenRoute) {
            SettingScreen(navController)
        }
        composable(route = MyProfile.MyCommentHistory.screenRoute) {
            MyCommentHistoryScreen(navController)
        }
        composable(route = MyProfile.Notice.screenRoute) {
            NoticeScreen(navController)
        }
        composable(route = MyProfile.UserDelete.screenRoute) {
            UserDeleteScreen(navController)
        }
        composable(route = MyProfile.MakeUserCode.screenRoute) {
            MakeUserCodeScreen(navController)
        }
        composable(route = MyProfile.EnterUserCode.screenRoute) {
            EnterUserCodeScreen(navController)
        }
        composable(route = "${TagNav.TagList.screenRoute}/{tagId}") { backStackEntry ->
            val tagId = backStackEntry.arguments?.getString("tagId").toString()
            TagListScreen(navController, tagId)
        }
    }
}
