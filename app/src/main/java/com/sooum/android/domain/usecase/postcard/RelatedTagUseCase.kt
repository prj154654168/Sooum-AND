package com.sooum.android.domain.usecase.postcard

import com.sooum.android.domain.repository.PostCardRepository
import javax.inject.Inject

class RelatedTagUseCase @Inject constructor(private val repository : PostCardRepository) {
    suspend operator fun invoke(
        keyword : String,
        size: Int
    ) = repository.getRelatedTag(keyword, size)
}