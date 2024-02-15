package com.example.swipeassignment.postdata.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import com.example.swipeassignment.postdata.data.AddProductRepository

@SuppressLint("StaticFieldLeak")
class AddProductViewModel constructor(ctx: Context) : ViewModel() {


    private val addProductRepository = AddProductRepository(ctx)


    val connectionError: LiveData<String>
        get() = addProductRepository.connectionError


    val response: LiveData<String>
        get() = addProductRepository.serverResponse


    fun rest() {
        addProductRepository.restAddProductVariables()

    }

    fun upload(
        product_name: String,
        product_type: String,
        price: String,
        tax: String,
        fileUri: Uri
    ) {
        addProductRepository.uploadProduct(
            product_name,
            product_type,
            price,
            tax,
            fileUri
        )
    }
}