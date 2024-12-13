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
    const val PROFILE_AGREE = "이용약관 및 개인정보 처리 방침"
    const val NICKNAME = "닉네임"
    const val LOG_IN_PROFILE = "로그인 사진"
    const val PROFILE_MODIFY = "프로필 수정"
    const val SETTING = "설정"
    const val TAG_LIST = "태그 리스트"
    const val COMMENT_HISTORY = "덧글 히스토리"
    const val NOTICE = "공시사항"
    const val USER_DELETE = "계정 탈퇴"
    const val USER_CODE_MAKE = "계정 코드 발급"
    const val USER_CODE_ENTER = "계정 코드 입력"
    const val FOLLOWER = "팔로워"
    const val FOLLOWING = "팔로잉"
    const val DIF_FOLLOWER = "남의 팔로워"
    const val DIF_FOLLOWING = "남의 팔로잉"
    const val DIF_PROFILE = "남의 프로필"
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
                TAG, PROFILE -> 4
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
    data object DifProfile : PostNav(NavigationRouteName.DIF_PROFILE)

    data object DifFollower : PostNav(NavigationRouteName.DIF_FOLLOWER)
    data object DifFollowing : PostNav(NavigationRouteName.DIF_FOLLOWING)

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

    data object MyCommentHistory : LogInNav(NavigationRouteName.COMMENT_HISTORY)
    data object Notice : LogInNav(NavigationRouteName.NOTICE)
    data object UserDelete : LogInNav(NavigationRouteName.USER_DELETE)

    data object MakeUserCode : LogInNav(NavigationRouteName.USER_CODE_MAKE)

    data object EnterUserCode : LogInNav(NavigationRouteName.USER_CODE_ENTER)

    data object Follower : LogInNav(NavigationRouteName.FOLLOWER)

    data object Following : LogInNav(NavigationRouteName.FOLLOWING)

    data object ProfileAgree : LogInNav(NavigationRouteName.PROFILE_AGREE)
}

sealed class TagNav(
    val screenRoute: String,
) {
    data object TagList : TagNav(NavigationRouteName.TAG_LIST)
}
