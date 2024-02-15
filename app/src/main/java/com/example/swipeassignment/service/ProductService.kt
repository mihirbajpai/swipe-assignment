package com.example.swipeassignment.service

import com.example.swipeassignment.model.Product
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ProductService {
    @GET("get")
    suspend fun getProducts(): List<Product>


    @Multipart
    @POST("add")
    fun addProduct(
        @Part("product_name") product_name: RequestBody?,
        @Part("product_type") product_type: RequestBody?,
        @Part("price") price: RequestBody?,
        @Part("tax") tax: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Call<ResponseBody>


    companion object {
        var retrofitService: ProductService? = null
        fun getInstance(): ProductService {
            if (retrofitService == null) {
                val gson = GsonBuilder()
                    .setLenient()
                    .create()
                val client = OkHttpClient()

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://app.getswipe.in/api/public/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                retrofitService = retrofit.create(ProductService::class.java)
            }
            return retrofitService!!
        }

    }
}