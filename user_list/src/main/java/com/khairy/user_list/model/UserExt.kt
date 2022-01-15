package com.khairy.user_list.model

import com.khairy.database.entities.UserEntity
import com.khairy.shared_models.models.User

/**
 * [UserListResponse] conversion to [UserEntity]
 */
fun UserListResponse.asUserCached() = UserEntity(
    id = id,
    login = login,
    url = url,
    avatarUrl = avatarUrl
)

/**
 * [UserEntity] conversion to [User]
 */
fun UserEntity.asUser() = User(
    id = id,
    login = login,
    url = url,
    avatarUrl = avatarUrl
)