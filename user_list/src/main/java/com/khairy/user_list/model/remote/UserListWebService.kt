package com.khairy.user_list.model.remote

import com.khairy.user_list.model.UserListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UserListWebService {


    @GET("users")
    suspend fun getUsersList(@Query("since") since: Long = 0): List<UserListResponse>
}