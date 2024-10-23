package com.sooum.android.model

import com.google.gson.annotations.SerializedName

data class DefaultImageDataModel(
    @SerializedName("_embedded")
    val embedded: Embedded,
    @SerializedName("_links")
    val links: Links,
    val status: Status
) {
    data class Embedded(
        val imgUrlInfoList: List<ImgUrlInfo>
    ) {
        data class ImgUrlInfo(
            val imgName: String,
            val url: Url
        ) {
            data class Url(
                val href: String
            )
        }
    }

    data class Links(
        val next: Next
    ) {
        data class Next(
            val href: String
        )
    }

    data class Status(
        val httpCode: Int,
        val httpStatus: String,
        val responseMessage: String
    )
}