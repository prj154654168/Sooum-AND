package com.sooum.android.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sooum.android.enums.ButtonHeightEnum
import com.sooum.android.enums.ButtonTypeEnum
import com.sooum.android.ui.theme.AppTextStyles
import com.sooum.android.ui.theme.Gray600
import com.sooum.android.ui.theme.GrayBlack
import com.sooum.android.ui.theme.GrayWhite

@Composable
fun CommonSingleButtonDialog(
    show: Boolean,
    title: String,
    message : String,
    onDismiss: () -> Unit,
    dismissText : String
) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GrayWhite
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = 22.dp,
                        bottom = 14.dp,
                        start = 14.dp,
                        end = 14.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = AppTextStyles.body1Bold,
                        color = GrayBlack,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message,
                        style = AppTextStyles.body2Regular,
                        color = Gray600,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CommonButton(
                            text = dismissText,
                            buttonType = ButtonTypeEnum.Primary,
                            buttonHeightType = ButtonHeightEnum.PopUp,
                            onClick = onDismiss,
                            enabled = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommonDoubleButtonDialog(
    show: Boolean,
    title: String,
    message : String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    dismissText:  String,
    confirmText : String
) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GrayWhite
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = 22.dp,
                        bottom = 14.dp,
                        start = 14.dp,
                        end = 14.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = AppTextStyles.body1Bold,
                        color = GrayBlack,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message,
                        style = AppTextStyles.body2Regular,
                        color = Gray600,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CommonButton(
                            text = dismissText,
                            buttonType = ButtonTypeEnum.Secondary,
                            buttonHeightType = ButtonHeightEnum.PopUp,
                            onClick = onDismiss,
                            enabled = true,
                            modifier = Modifier.weight(1f)
                        )
                        CommonButton(
                            text = confirmText,
                            buttonType = ButtonTypeEnum.Primary,
                            buttonHeightType = ButtonHeightEnum.PopUp,
                            onClick = onConfirm,
                            enabled = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}