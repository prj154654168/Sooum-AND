package com.sooum.android.domain.usecase.profile

import com.sooum.android.domain.model.DeleteUserBody
import com.sooum.android.domain.repository.MyProfileRepository
import javax.inject.Inject

class DeleteUserUserCase @Inject constructor(private val repository: MyProfileRepository) {
    suspend operator fun invoke(
        deleteUserBody: DeleteUserBody,
    ) = repository.deleteUser(deleteUserBody)
}