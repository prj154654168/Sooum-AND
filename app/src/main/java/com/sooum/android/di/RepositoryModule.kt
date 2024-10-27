package com.sooum.android.di

import com.sooum.android.data.repository.HomeFeedRepositoryImpl
import com.sooum.android.domain.repository.HomeFeedRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun homeFeedRepository(homeFeedRepositoryImpl: HomeFeedRepositoryImpl) : HomeFeedRepository
}