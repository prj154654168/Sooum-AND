package com.sooum.android.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sooum.android.R

object NavigationRouteName {
    const val MAIN_HOME = "홈"
    const val ADD_POST = "글추가"
    const val TAG = "태그"
    const val PROFILE = "프로필"
}

sealed class SoonumNav(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val screenRoute: String,
) {
    object Home : SoonumNav(R.string.home, R.drawable.ic_home, NavigationRouteName.MAIN_HOME)
    object AddPost :
        SoonumNav(R.string.add_post, R.drawable.ic_add_post, NavigationRouteName.ADD_POST)

    object Tag : SoonumNav(R.string.tag, R.drawable.ic_tag, NavigationRouteName.TAG)
    object Profile :
        SoonumNav(R.string.profile, R.drawable.ic_profile, NavigationRouteName.PROFILE)
}