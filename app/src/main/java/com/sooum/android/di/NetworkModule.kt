package com.sooum.android.di

import android.content.Context
import android.util.Log
import com.sooum.android.Constants.BASE_URL
import com.sooum.android.SooumApplication
import com.sooum.android.data.remote.AuthInterceptor
import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.remote.ProfileApi
import com.sooum.android.data.remote.ReportApi
import com.sooum.android.data.remote.TagAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun getRetrofitInstance() : Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // 요청/응답 본문까지 출력
        }
        Log.e("AuthInterceptor","1")
        // OkHttpClient에 인터셉터 추가
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor())
            .build()

        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
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

    @Singleton
    @Provides
    fun getProfileApiInstance(retrofit: Retrofit) : ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }
}