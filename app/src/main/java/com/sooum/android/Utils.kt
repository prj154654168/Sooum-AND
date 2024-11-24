package com.sooum.android

import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

object Utils {
    val cropOption = CropImageOptions(
        imageSourceIncludeCamera = false,
        fixAspectRatio = true,
        aspectRatioX = 10,
        aspectRatioY = 9,
        allowFlipping = false,
        allowRotation = false,
        guidelines = CropImageView.Guidelines.OFF
    )
}