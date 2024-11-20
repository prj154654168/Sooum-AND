package com.sooum.android.domain.usecase.postcard

import com.sooum.android.domain.repository.PostCardRepository
import com.sooum.android.enums.FontEnum
import com.sooum.android.enums.ImgTypeEnum
import javax.inject.Inject

class FeedCardUseCase @Inject constructor(private val repository: PostCardRepository) {
    suspend operator fun invoke(
        isDistanceShared: Boolean,
        latitude: Double?,
        longitude: Double?,
        isPublic: Boolean,
        isStory: Boolean,
        content: String,
        font: FontEnum,
        imgType: ImgTypeEnum,
        imgName: String,
        feedTags: List<String>
    ) = repository.postFeedCard(
        isDistanceShared, latitude, longitude, isPublic, isStory, content, font, imgType, imgName, feedTags
    )
}