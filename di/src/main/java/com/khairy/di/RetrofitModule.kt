package com.khairy.di

import android.content.Context
import android.net.TrafficStats
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.khairy.core.helpers.retrofit.NetworkConnectionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val MAX_REQUEST_AT_A_TIME = 1

@InstallIn(SingletonComponent::class)
@Module
object RetrofitModule {
    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder().create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, @ApplicationContext appContext: Context): Retrofit {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = MAX_REQUEST_AT_A_TIME

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(90, TimeUnit.SECONDS)
            .dispatcher(dispatcher)
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }
}