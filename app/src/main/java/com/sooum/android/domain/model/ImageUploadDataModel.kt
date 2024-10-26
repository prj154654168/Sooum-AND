package com.sooum.android.domain.model

data class ImageUploadDataModel(
    val imgName: String,
    val status: Status,
    val url: DefaultImageDataModel.Embedded.ImgUrlInfo.Url
)