package com.khairy.shared_models.models

import com.khairy.database.entities.UserEntity


fun UserEntity.asUser() = User(
    id = id,
    login = login,
    url = url,
    avatarUrl = avatarUrl
)