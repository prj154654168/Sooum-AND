package com.sooum.android.di

import com.sooum.android.Constants.BASE_URL
import com.sooum.android.data.remote.AppVersionApi
import com.sooum.android.data.remote.AuthInterceptor
import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.remote.NotificationApi
import com.sooum.android.data.remote.ProfileApi
import com.sooum.android.data.remote.ReportApi
import com.sooum.android.data.remote.TagAPI
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
    @Singleton
    @Provides
    fun getRetrofitInstance() : Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor())
            // 타임아웃 설정 추가
            .connectTimeout(30, TimeUnit.SECONDS)  // 연결 타임아웃 30초
            .readTimeout(30, TimeUnit.SECONDS)     // 읽기 타임아웃 30초
            .writeTimeout(30, TimeUnit.SECONDS)    // 쓰기 타임아웃 30초
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