package com.sooum.android.domain.usecase.homefeed

import com.sooum.android.domain.repository.HomeFeedRepository
import javax.inject.Inject

class PopularityFeedUseCase @Inject constructor (private val repository: HomeFeedRepository) {
    suspend operator fun invoke(
        latitude: Double?,
        longitude: Double?
    ) = repository.getPopularityCardList(latitude, longitude)
}