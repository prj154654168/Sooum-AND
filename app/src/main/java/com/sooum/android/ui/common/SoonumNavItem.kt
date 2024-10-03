package com.sooum.android.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sooum.android.R
import com.sooum.android.ui.common.NavigationRouteName.ADD_POST
import com.sooum.android.ui.common.NavigationRouteName.MAIN_HOME
import com.sooum.android.ui.common.NavigationRouteName.PROFILE
import com.sooum.android.ui.common.NavigationRouteName.TAG

object NavigationRouteName {
    const val MAIN_HOME = "메인홈"
    const val ADD_POST = "글추가"
    const val TAG = "태그"
    const val PROFILE = "프로필"
    const val DETAIL = "상세화면"
    const val REPORT = "신고하기"
}

sealed class SoonumNav(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val screenRoute: String,
) {
    data object Home : SoonumNav(R.string.main_home, R.drawable.ic_home, MAIN_HOME)
    data object AddPost :
        SoonumNav(R.string.add_post, R.drawable.ic_add_post, ADD_POST)

    data object Tag : SoonumNav(R.string.tag, R.drawable.ic_tag, TAG)
    data object Profile :
        SoonumNav(R.string.profile, R.drawable.ic_profile, PROFILE)

    companion object {
        fun isMainRoute(route: String?): Boolean {
            return when (route) {
                MAIN_HOME, ADD_POST, TAG, PROFILE -> true
                else -> false
            }
        }
    }
}

sealed class PostNav(
    val screenRoute: String,
) {
    data object Detail : PostNav(NavigationRouteName.DETAIL)
    data object Report : PostNav(NavigationRouteName.REPORT)
}