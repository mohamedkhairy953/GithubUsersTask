package com.khairy.user_list.model.remote

import com.khairy.user_list.model.ProductListResponse
import retrofit2.http.GET

interface UserListWebService {


    @GET("users")
    suspend fun getProductList(): ProductListResponse
}