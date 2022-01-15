package com.khairy.user_profile.di

import com.khairy.database.AppDatabase
import com.khairy.user_profile.model.remote.UserProfileWebService
import com.khairy.user_profile.model.repo.ProfileRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class UserProfileModule {
    @Provides
    fun provideWebService(retrofit: Retrofit): UserProfileWebService =
        retrofit.create(UserProfileWebService::class.java)

    @Provides
    fun provideRepo(
        db: AppDatabase,
        webService: UserProfileWebService
    ) =
        ProfileRepositoryImpl(db, webService)
}