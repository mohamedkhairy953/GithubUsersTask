package com.khairy.shared_models.models

import com.khairy.database.entities.ProfileEntity


/**
 * [ProfileResponse] conversion to [ProfileEntity]
 */
fun ProfileResponse.asCached() = ProfileEntity(
    id = id,
    followers = followers,
    following = following,
    name = name ?: "",
    company = company ?: "",
    blog = blog
)

/**
 * [ProfileEntity] conversion to [Profile]
 */
fun ProfileEntity.asPortfolio() = Profile(
    id = id,
    followers = followers,
    following = following,
    name = name,
    company = company,
    blog = blog
)

