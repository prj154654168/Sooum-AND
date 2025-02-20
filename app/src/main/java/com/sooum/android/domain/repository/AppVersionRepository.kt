package com.sooum.android.domain.repository

interface AppVersionRepository {
    suspend fun getAppVersion():String
}