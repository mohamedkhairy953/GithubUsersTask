package com.khairy.user_list.model.repo


import com.khairy.core.helpers.network.Resource
import com.khairy.core.helpers.network.networkBoundResource
import com.khairy.database.AppDatabase
import com.khairy.shared_models.models.FIRST_USER_ID
import com.khairy.shared_models.models.User
import com.khairy.shared_models.models.asPortfolio
import com.khairy.user_list.model.asUser
import com.khairy.user_list.model.asUserCached
import com.khairy.user_list.model.remote.UserListWebService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

const val DOWNLOAD_USERS_PER_REQUEST = 30


class UserRepositoryImpl(
    private val database: AppDatabase,
    private val userListWebService: UserListWebService
)  {

     suspend fun getUsers(users: List<User>): Flow<Resource<List<User>>> =
        networkBoundResource(
            fromCache = {
                database.userDao().queryAllUntil(users.size + DOWNLOAD_USERS_PER_REQUEST).map {
                    it.map { user ->
                        user.asUser().apply {
                            this.profile = database.profileDao().getProfileById(user.id)?.asPortfolio()
                            this.notes = database.notesDao().getNotesByUserId(user.id)?.value ?: ""
                        }
                    }
                }
            },
            requestCall = { userListWebService.getUsersList(users.lastOrNull()?.id ?: FIRST_USER_ID) },
            saveInCache = { usersResponse -> database.userDao().insertAll(usersResponse.map { it.asUserCached() }) }
        )

     suspend fun searchUsers(input: String): Flow<Resource<List<User>>> = flow {
        emit(
            Resource.Success(
                //By login
                database.userDao().searchByLogin(input).map { user ->
                    user.asUser().apply {
                        this.profile = database.profileDao().getProfileById(user.id)?.asPortfolio()
                        this.notes = database.notesDao().getNotesByUserId(user.id)?.value ?: ""
                    }
                }.toList().plus(
                    //By notes
                    database.notesDao().searchByInput(input).map { notes ->
                        database.userDao().getUserById(notes.id).first().asUser().apply {
                            this.profile = database.profileDao().getProfileById(this.id)?.asPortfolio()
                            this.notes = notes.value
                        }
                    })
            )
        )
    }
}
