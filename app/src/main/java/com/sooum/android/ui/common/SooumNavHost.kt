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
import com.sooum.android.ui.DifFollowerScreen
import com.sooum.android.ui.DifFollowingScreen
import com.sooum.android.ui.FollowScreen
import com.sooum.android.ui.FollowingScreen
import com.sooum.android.ui.HomeScreen
import com.sooum.android.ui.DifProfileScreen
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
import com.sooum.android.ui.myprofile.ProfileAgreeScreen
import com.sooum.android.ui.myprofile.UserDeleteScreen
import com.sooum.android.ui.onboarding.AgreeScreen
import com.sooum.android.ui.onboarding.LogInProfileScreen
import com.sooum.android.ui.onboarding.LogInScreen
import com.sooum.android.ui.onboarding.NickNameScreen

@Composable
fun SooumNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        modifier = Modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = SooumNav.Home.screenRoute) {
            HomeScreen(navController)
        }

        composable(route = SooumNav.AddPost.screenRoute) {
            AddPostScreen(navController)
        }

        composable(
            route = "addCommentCard/{cardId}/{storyExpirationTime}",
            arguments = listOf(
                navArgument("cardId") {type = NavType.StringType},
                navArgument("storyExpirationTime") {type = NavType.StringType}
            )
        )
        { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")
            val storyExpirationTime = backStackEntry.arguments?.getString("storyExpirationTime")
            AddPostScreen(navController, cardId, storyExpirationTime)
        }

        composable(route = SooumNav.Tag.screenRoute) {
            TagScreen(navController)
        }
        composable(route = SooumNav.Profile.screenRoute) {
            MyProfileScreen(navController)
        }
        composable(route = "${PostNav.Detail.screenRoute}/{cardId}") { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")
            DetailScreen(navController, cardId)
        }
        composable(route = "${PostNav.DifProfile.screenRoute}/{memberId}") { backStackEntry ->
            val memberId = backStackEntry.arguments?.getString("memberId")
            DifProfileScreen(navController, memberId)
        }

        composable(route = "${PostNav.DifFollower.screenRoute}/{profileOwnerPk}") { backStackEntry ->
            val profileOwnerPk = backStackEntry.arguments?.getString("profileOwnerPk").toString().toLong()
            DifFollowerScreen(navController, profileOwnerPk)
        }

        composable(route = "${PostNav.DifFollowing.screenRoute}/{profileOwnerPk}") { backStackEntry ->
            val profileOwnerPk = backStackEntry.arguments?.getString("profileOwnerPk").toString().toLong()
            DifFollowingScreen(navController, profileOwnerPk)
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
        composable(route = MyProfile.Follower.screenRoute) {
            FollowScreen(navController)
        }
        composable(route = MyProfile.Following.screenRoute) {
            FollowingScreen(navController)
        }
        composable(route = MyProfile.ProfileAgree.screenRoute) {
            ProfileAgreeScreen(navController)
        }
        composable(route = "${TagNav.TagList.screenRoute}/{tagId}") { backStackEntry ->
            val tagId = backStackEntry.arguments?.getString("tagId").toString()
            TagListScreen(navController, tagId)
        }
    }
}
