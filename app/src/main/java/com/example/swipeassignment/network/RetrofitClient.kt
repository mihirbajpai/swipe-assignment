package com.example.swipeassignment.network

import com.example.swipeassignment.service.ProductService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://app.getswipe.in/api/public/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val productService: ProductService by lazy {
        retrofit.create(ProductService::class.java)
    }

}


