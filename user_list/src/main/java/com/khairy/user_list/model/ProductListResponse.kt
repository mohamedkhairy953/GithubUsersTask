package com.khairy.user_list.model


import com.google.gson.annotations.SerializedName

data class ProductListResponse(
    @SerializedName("items")
    val productList: List<Product>?
)