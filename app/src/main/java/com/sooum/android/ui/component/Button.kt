package com.sooum.android.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sooum.android.enums.ButtonHeightEnum
import com.sooum.android.enums.ButtonTypeEnum
import com.sooum.android.ui.theme.AppTextStyles
import com.sooum.android.ui.theme.Gray300
import com.sooum.android.ui.theme.Gray600
import com.sooum.android.ui.theme.GrayWhite
import com.sooum.android.ui.theme.Primary300

@Composable
fun CommonButton(
    text: String,
    buttonType: ButtonTypeEnum,
    buttonHeightType: ButtonHeightEnum,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val textColor = when (buttonType) {
        ButtonTypeEnum.Primary -> GrayWhite
        ButtonTypeEnum.Secondary -> Gray600
    }

    val backgroundColor = when (buttonType) {
        ButtonTypeEnum.Primary -> Primary300
        ButtonTypeEnum.Secondary -> Gray300
    }

    val buttonHeight = when (buttonHeightType) {
        ButtonHeightEnum.Large -> 50.dp
        ButtonHeightEnum.PopUp -> 46.dp
        ButtonHeightEnum.Small -> 40.dp
    }

    val buttonRadius = when (buttonHeightType) {
        ButtonHeightEnum.Large -> 10.dp
        ButtonHeightEnum.PopUp -> 10.dp
        ButtonHeightEnum.Small -> 6.dp
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(buttonHeight),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(buttonRadius)
    ) {
        Text(text = text, style = AppTextStyles.body1Bold, color = textColor)
    }
}
