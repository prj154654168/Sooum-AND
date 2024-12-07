package com.sooum.android.domain.usecase.tag

import com.sooum.android.domain.repository.TagRepository
import javax.inject.Inject

class TagFeedUseCase @Inject constructor(private val repository: TagRepository) {
    suspend operator fun invoke(
        tagId: String,
        latitude: Double?,
        longitude: Double?,
        laskPk: Long?
    ) = repository.getTagFeedList(tagId, latitude, longitude, laskPk)
}