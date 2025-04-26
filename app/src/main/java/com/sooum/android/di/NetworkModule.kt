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
import com.sooum.android.data.remote.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Dns
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
            .dns(Dns.SYSTEM) // 명시적 DNS 사용 (UnknownHostException 방지)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true) // 연결 실패시 재시도 켜주기
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }) //가장 먼저 추가 (요청/응답 로깅)
            .addInterceptor(AuthInterceptor()) // accessToken 붙이는 인터셉터
            .authenticator(TokenAuthenticator()) // 인증 실패시 새 토큰 발급
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

    @Singleton
    @Provides
    fun getUserApiInstance(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
}