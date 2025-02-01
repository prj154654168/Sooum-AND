package com.sooum.android.domain.usecase.tag

import androidx.paging.PagingData
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.TagFeedDataModel
import com.sooum.android.domain.repository.HomeFeedRepository
import com.sooum.android.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagFeedUseCase @Inject constructor(private val repository: TagRepository) {
    operator fun invoke(
        tagId: String,
        latitude: Double?,
        longitude: Double?,
    ): Flow<PagingData<TagFeedDataModel.Embedded.TagFeedCardDto>> = repository.getTagFeedList(tagId, latitude, longitude)
}