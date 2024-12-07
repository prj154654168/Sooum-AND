package com.sooum.android.domain.model

data class NoticeDataModel(
    val _embedded: Embedded,
    val _links: Links,
    val status: Status
){
    data class Embedded(
        val noticeDtoList: List<NoticeDto>
    )

    data class NoticeDto(
        val id: Int,
        val noticeType: String,
        val noticeDate: String,
        val title: String,
        val link: String
    )

    data class Links(
        val self: Link
    )

    data class Link(
        val href: String
    )

    data class Status(
        val httpCode: Int,
        val httpStatus: String,
        val responseMessage: String
    )
}

