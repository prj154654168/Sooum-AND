package com.sooum.android.domain.usecase.user

import com.sooum.android.domain.repository.UserRepository
import javax.inject.Inject

class RsaKeyUseCase @Inject constructor(private val repository: UserRepository){
    suspend operator fun invoke() = repository.getRsaKey()
}