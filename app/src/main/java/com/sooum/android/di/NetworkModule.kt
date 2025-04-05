package com.sooum.android.di

import com.sooum.android.Constants
import com.sooum.android.Constants.BASE_URL
import com.sooum.android.data.remote.AppVersionApi
import com.sooum.android.data.remote.AuthInterceptor
import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.remote.NotificationApi
import com.sooum.android.data.remote.ProfileApi
import com.sooum.android.data.remote.ReportApi
import com.sooum.android.data.remote.TagAPI
import com.sooum.android.data.remote.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor()) // accessToken만 붙임
            .authenticator(TokenAuthenticator()) // 401 나오면 자동 재요청
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }) // 마지막에 추가
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
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

    @Singleton
    @Provides
    fun getNotificationApiInstance(retrofit: Retrofit) : NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }

    @Singleton
    @Provides
    fun getAppVersionApiInstance(retrofit: Retrofit) : AppVersionApi {
        return retrofit.create(AppVersionApi::class.java)
    }
}