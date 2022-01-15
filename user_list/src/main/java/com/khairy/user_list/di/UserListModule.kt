package com.khairy.user_list.di

import com.khairy.database.AppDatabase
import com.khairy.database.daos.NotesDao
import com.khairy.database.daos.ProfileDao
import com.khairy.database.daos.UserDao
import com.khairy.user_list.model.remote.UserListWebService
import com.khairy.user_list.model.repo.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class UserListModule {
    @Provides
    fun provideWebService(retrofit: Retrofit): UserListWebService =
        retrofit.create(UserListWebService::class.java)

    @Provides
    fun provideRepo(
        db: AppDatabase,
        webService: UserListWebService
    ) =
        UserRepositoryImpl(db, webService)
}