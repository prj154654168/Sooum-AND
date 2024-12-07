package com.sooum.android.domain.model

data class MyCommentCardDataModel(
    val _embedded: Embedded,
    val _links: LinksX,
    val status: Status
){
    data class Next(
        val href: String
    )
    data class MyCommentCardDto(
        val _links: Links,
        val backgroundImgUrl: BackgroundImgUrl,
        val content: String,
        val font: String,
        val fontSize: String,
        val id: String
    )
    data class Embedded(
        val myCommentCardDtoList: List<MyCommentCardDto>
    )
    data class BackgroundImgUrl(
        val href: String
    )
    data class LinksX(
        val next: Next
    )
    data class Links(
        val detail: Detail
    )
    data class Detail(
        val href: String
    )
}