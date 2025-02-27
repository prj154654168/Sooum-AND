package com.sooum.android.data.repository

import com.sooum.android.data.remote.AppVersionApi
import com.sooum.android.domain.repository.AppVersionRepository
import javax.inject.Inject

class AppVersionRepositoryImpl @Inject constructor(
    private val appVersionApi: AppVersionApi
):AppVersionRepository {
    override suspend fun getAppVersion(currentVersion:String): String {
        val responseBody = appVersionApi.getAppVersion(currentVersion)
        return responseBody.string()
    }
}