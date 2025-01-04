package com.sooum.android.domain.usecase.homefeed

import com.sooum.android.domain.model.SortedByPopularityDataModel
import com.sooum.android.domain.repository.HomeFeedRepository
import javax.inject.Inject

class PopularityFeedUseCase @Inject constructor (private val repository: HomeFeedRepository) {
    operator suspend fun invoke(
        latitude: Double?,
        longitude: Double?
    ): List<SortedByPopularityDataModel.Embedded.PopularFeedCard> = repository.getPopularityCardList(latitude, longitude)
}