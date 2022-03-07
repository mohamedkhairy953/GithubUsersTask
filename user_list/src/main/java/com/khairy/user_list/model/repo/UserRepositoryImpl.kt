package com.khairy.user_list.model.repo


import com.khairy.core.helpers.network.Resource
import com.khairy.core.helpers.network.networkBoundResource
import com.khairy.database.AppDatabase
import com.khairy.shared_models.models.FIRST_USER_ID
import com.khairy.shared_models.models.User
import com.khairy.shared_models.models.asPortfolio
import com.khairy.user_list.model.Product
import com.khairy.user_list.model.asUser
import com.khairy.user_list.model.remote.UserListWebService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val DOWNLOAD_USERS_PER_REQUEST = 30


class UserRepositoryImpl(
    private val database: AppDatabase,
    private val userListWebService: UserListWebService
)  {

     suspend fun getProductList(users: List<User>): Flow<Resource<List<Product>>> =
        networkBoundResource(
            fromCache = {
                database.userDao().queryAll().map {
                    it.map { user ->
                        user.asUser().apply {
                            this.profile = database.profileDao().getProfileById(user.id)?.asPortfolio()
                            this.notes = database.notesDao().getNotesByUserId(user.id)?.value ?: ""
                        }
                    }
                }
            },
            requestCall = { userListWebService.getProductList() },
            saveInCache = { usersResponse -> database.userDao().insertAll(usersResponse.productList) }
        )

}
