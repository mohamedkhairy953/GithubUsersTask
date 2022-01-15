package com.khairy.user_profile.model.repo

import com.khairy.core.helpers.network.Resource
import com.khairy.core.helpers.network.networkBoundResource
import com.khairy.database.AppDatabase
import com.khairy.database.entities.NotesEntity
import com.khairy.shared_models.models.User
import com.khairy.shared_models.models.asCached
import com.khairy.shared_models.models.asPortfolio
import com.khairy.shared_models.models.asUser
import com.khairy.user_profile.model.remote.UserProfileWebService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class ProfileRepositoryImpl(
    private val database: AppDatabase,
    private val userProfileWebService: UserProfileWebService
) {


    suspend fun getUserPortfolioData(
        userId: Long,
        userLogin: String
    ): Flow<Resource<User>> = networkBoundResource(
        fromCache = {
            database.userDao().getUserById(userId).map { user ->
                user.asUser().apply {
                    this.profile = database.profileDao().getProfileById(user.id)?.asPortfolio()
                    this.notes = database.notesDao().getNotesByUserId(user.id)?.value ?: ""
                }
            }
        },
        requestCall = { userProfileWebService.getPortfolioData(userLogin) },
        saveInCache = { item -> database.profileDao().insert(item.asCached()) }
    )

    suspend fun updateUserNotes(notes: NotesEntity) {
        database.notesDao().insert(notes)
    }
}