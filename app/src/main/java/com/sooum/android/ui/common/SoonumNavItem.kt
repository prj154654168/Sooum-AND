package com.sooum.android.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sooum.android.R

object NavigationRouteName {
    const val MAIN_HOME = "메인홈"
    const val ADD_POST = "글추가"
    const val TAG = "태그"
    const val PROFILE = "프로필"
    const val DETAIL = "상세화면"
}

sealed class SoonumNav(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val screenRoute: String,
) {
    data object Home : SoonumNav(R.string.main_home, R.drawable.ic_home, NavigationRouteName.MAIN_HOME)
    data object AddPost :
        SoonumNav(R.string.add_post, R.drawable.ic_add_post, NavigationRouteName.ADD_POST)

    data object Tag : SoonumNav(R.string.tag, R.drawable.ic_tag, NavigationRouteName.TAG)
    data object Profile :
        SoonumNav(R.string.profile, R.drawable.ic_profile, NavigationRouteName.PROFILE)
}
sealed class PostNav(
    val screenRoute: String,
) {
    data object Detail : PostNav(NavigationRouteName.DETAIL)
}