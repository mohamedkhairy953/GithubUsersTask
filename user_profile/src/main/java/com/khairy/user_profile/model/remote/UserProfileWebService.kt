package com.khairy.user_profile.model.remote

import com.khairy.shared_models.models.ProfileResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface UserProfileWebService {
    @GET("users/{user_login}")
    suspend fun getPortfolioData(@Path("user_login") user_login: String): ProfileResponse
}