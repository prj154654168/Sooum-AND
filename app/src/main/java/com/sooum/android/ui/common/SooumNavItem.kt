package com.sooum.android.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sooum.android.R
import com.sooum.android.ui.common.NavigationRouteName.ADD_POST
import com.sooum.android.ui.common.NavigationRouteName.MAIN_HOME
import com.sooum.android.ui.common.NavigationRouteName.PROFILE
import com.sooum.android.ui.common.NavigationRouteName.REPORT
import com.sooum.android.ui.common.NavigationRouteName.TAG

object NavigationRouteName {
    const val MAIN_HOME = "메인홈"
    const val ADD_POST = "글추가"
    const val TAG = "태그"
    const val PROFILE = "프로필"
    const val DETAIL = "상세화면"
    const val REPORT = "신고하기"
    const val LON_IN = "로그인"
    const val AGREE = "약관동의"
    const val NICKNAME = "닉네임"
    const val LOG_IN_PROFILE = "로그인 사진"
    const val PROFILE_MODIFY = "프로필 수정"
    const val SETTING = "설정"
    const val TAG_LIST = "태그 리스트"
}

sealed class SoonumNav(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val screenRoute: String,
) {
    data object Home : SoonumNav(R.string.main_home, R.drawable.ic_home, MAIN_HOME)
    data object AddPost :
        SoonumNav(R.string.add_post, R.drawable.ic_add_post, ADD_POST)

    data object Tag : SoonumNav(R.string.tag, R.drawable.ic_nav_tag, TAG)
    data object Profile :
        SoonumNav(R.string.profile, R.drawable.ic_profile, PROFILE)

    companion object {
        fun isMainRoute(route: String?): Int {
            return when (route) {
                MAIN_HOME -> 1
                ADD_POST, REPORT -> 3
                else -> 2
            }//top bar 추후 수정 필요
        }
    }
}

sealed class PostNav(
    val screenRoute: String,
) {
    data object Detail : PostNav(NavigationRouteName.DETAIL)
    data object Report : PostNav(NavigationRouteName.REPORT)
}

sealed class LogInNav(
    val screenRoute: String,
) {
    data object LogIn : LogInNav(NavigationRouteName.LON_IN)
    data object Agree : LogInNav(NavigationRouteName.AGREE)
    data object NickName : LogInNav(NavigationRouteName.NICKNAME)
    data object LogInProfile : LogInNav(NavigationRouteName.LOG_IN_PROFILE)
}


sealed class MyProfile(
    val screenRoute: String,
) {
    data object ProfileModify : LogInNav(NavigationRouteName.PROFILE_MODIFY)
    data object Setting : LogInNav(NavigationRouteName.SETTING)
}

sealed class TagNav(
    val screenRoute: String,
) {
    data object TagList : TagNav(NavigationRouteName.TAG_LIST)
}
