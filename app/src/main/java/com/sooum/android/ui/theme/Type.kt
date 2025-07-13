package com.sooum.android.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.sooum.android.R

object AppTextStyles {
    val head1Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.003).em,
        color = GrayBlack
    )
    val head1Regular = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.003).em,
        color = GrayBlack
    )
    val head2Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.48.sp,
        letterSpacing = (-0.003).em,
        color = GrayBlack
    )
    val head2Regular = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.48.sp,
        letterSpacing = (-0.003).em,
        color = GrayBlack
    )
    val body1Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.004).em,
        color = GrayBlack
    )
    val body1Regular = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.004).em,
        color = GrayBlack
    )
    val body2Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 19.6.sp,
        letterSpacing = (-0.004).em,
        color = GrayBlack
    )
    val body2Regular = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 19.6.sp,
        letterSpacing = (-0.004).em,
        color = GrayBlack
    )
    val body3Bold = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.8.sp,
        letterSpacing = (-0.004).em,
        color = GrayBlack
    )
    val body3Regular = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.8.sp,
        letterSpacing = (-0.004).em,
        color = GrayBlack
    )
    val caption = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = (-0.004).em,
        color = GrayBlack
    )
    val body1BoldHandWriting = TextStyle(
        fontFamily = HandWriting,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 25.2.sp,
        letterSpacing = (-0.004).em,
        color = GrayBlack
    )
    val body1RegularHandWriting = TextStyle(
        fontFamily = HandWriting,
        fontWeight = FontWeight.Light,
        fontSize = 18.sp,
        lineHeight = 25.2.sp,
        letterSpacing = (-0.004).em,
        color = GrayBlack
    )
}


val Pretendard = FontFamily(
    Font(R.font.pretendard_semi_bold, FontWeight.SemiBold),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_regular, FontWeight.Normal),
)

val HandWriting = FontFamily(
    Font(R.font.hand_write_bold, FontWeight.Bold),
    Font(R.font.hand_write_light, FontWeight.Light),
)