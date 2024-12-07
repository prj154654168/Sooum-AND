package com.sooum.android.domain.model

import com.google.gson.annotations.SerializedName

data class FavoriteTagDataModel(
    @SerializedName("_embedded")
    val embedded: Embedded,
    val status: Status
) {
    data class Embedded(
        val favoriteTagList: List<FavoriteTag>
    ) {
        data class FavoriteTag(
            @SerializedName("_links")
            val links: Links,
            val id: String,
            val previewCards: List<PreviewCard>,
            val tagContent: String,
            val tagUsageCnt: String
        ) {
            data class Links(
                @SerializedName("tag-feed")
                val tagFeed: TagFeed
            ) {
                data class TagFeed(
                    val href: String
                )
            }

            data class PreviewCard(
                @SerializedName("_links")
                val links: Links,
                val backgroundImgUrl: BackgroundImgUrl,
                val content: String,
                val id: String
            ) {
                data class Links(
                    val detail: Detail
                ) {
                    data class Detail(
                        val href: String
                    )
                }

                data class BackgroundImgUrl(
                    val href: String
                )
            }
        }
    }

    data class Status(
        val httpCode: Int,
        val httpStatus: String,
        val responseMessage: String
    )
}