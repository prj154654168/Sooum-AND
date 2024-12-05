package com.sooum.android.domain.model

data class MyFeedCardDataModel(
    val _embedded: Embedded,
    val status: Status
){
    data class Embedded(
        val myFeedCardDtoList: List<MyFeedCardDto>
    )
    data class MyFeedCardDto(
        val _links: Links,
        val backgroundImgUrl: BackgroundImgUrl,
        val content: String,
        val font: String,
        val fontSize: String,
        val id: String
    )
    data class Links(
        val detail: Detail
    )

    data class Detail(
        val href: String
    )
    data class BackgroundImgUrl(
        val href: String
    )
}