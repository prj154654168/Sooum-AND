package com.sooum.android.domain.usecase.version

import com.sooum.android.domain.repository.AppVersionRepository
import javax.inject.Inject

class AppVersionUseCase @Inject constructor(
    private val repository : AppVersionRepository
) {
    suspend operator fun invoke():String {
        return repository.getAppVersion()
    }
}
