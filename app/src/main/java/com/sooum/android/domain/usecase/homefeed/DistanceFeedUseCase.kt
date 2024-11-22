package com.sooum.android.domain.usecase.homefeed

import com.sooum.android.domain.repository.HomeFeedRepository
import com.sooum.android.enums.DistanceEnum
import javax.inject.Inject

class DistanceFeedUseCase @Inject constructor (private val repository: HomeFeedRepository) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        distanceFilter: DistanceEnum
    ) = repository.getDistanceCardList(latitude, longitude, distanceFilter)
}