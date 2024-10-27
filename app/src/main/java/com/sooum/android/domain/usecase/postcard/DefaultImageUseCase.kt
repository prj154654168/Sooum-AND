package com.sooum.android.domain.usecase.postcard

import com.sooum.android.domain.repository.PostCardRepository
import javax.inject.Inject

class DefaultImageUseCase @Inject constructor(private val repository: PostCardRepository) {
    suspend operator fun invoke(
        previousImgsName : String?
    ) = repository.getDefaultImage(previousImgsName)
}