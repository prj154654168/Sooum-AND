package com.sooum.android.domain.usecase.tag

import com.sooum.android.domain.repository.TagRepository
import javax.inject.Inject

class FavoriteTagUseCase @Inject constructor(private val repository: TagRepository) {
    suspend operator fun invoke(last: String?) =  repository.getFavoriteTag(last)
}