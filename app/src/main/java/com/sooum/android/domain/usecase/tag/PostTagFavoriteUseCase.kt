package com.sooum.android.domain.usecase.tag

import com.sooum.android.domain.repository.TagRepository
import javax.inject.Inject

class PostTagFavoriteUseCase @Inject constructor(private val repository: TagRepository) {
    suspend operator fun invoke(tagId: String) = repository.postTagFavorite(tagId)
}