package com.sooum.android.di

import com.sooum.android.Constants.BASE_URL
import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.remote.ReportApi
import com.sooum.android.data.remote.TagAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun getRetrofitInstance() : Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun getCardApiInstance(retrofit: Retrofit) : CardApi {
        return retrofit.create(CardApi::class.java)
    }

    @Singleton
    @Provides
    fun getReportApiInstance(retrofit: Retrofit) : ReportApi {
        return retrofit.create(ReportApi::class.java)
    }

    @Singleton
    @Provides
    fun getTagApiInstance(retrofit: Retrofit) : TagAPI {
        return retrofit.create(TagAPI::class.java)
    }
}