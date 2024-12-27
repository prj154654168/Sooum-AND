package com.sooum.android.domain.usecase.homefeed

import androidx.paging.PagingData
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.repository.HomeFeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LatestFeedUseCase @Inject constructor(
    private val repository: HomeFeedRepository
) {
    operator fun invoke(
        latitude: Double?,
        longitude: Double?
    ): Flow<PagingData<SortedByLatestDataModel.Embedded.LatestFeedCard>> = repository.getLatestCardList(latitude, longitude)
}