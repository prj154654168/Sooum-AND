package com.sooum.android.domain.usecase.detail

import com.sooum.android.domain.model.PostCommentCardRequestDataModel
import com.sooum.android.domain.repository.DetailRepository
import javax.inject.Inject

class PostCommentCardUseCase @Inject constructor(private val repository: DetailRepository) {
    suspend operator fun invoke(
        cardId: Long,
        postCommentCardRequest: PostCommentCardRequestDataModel
    ) = repository.postCommentCard(cardId, postCommentCardRequest)
}