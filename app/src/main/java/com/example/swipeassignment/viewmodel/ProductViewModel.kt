package com.example.swipeassignment.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.swipeassignment.model.Product
import com.example.swipeassignment.repository.ProductRepository

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    fun fetchProducts() {
        viewModelScope.launch {
            _products.value = repository.fetchProducts()
        }
    }


    val connectionError: LiveData<String>
        get() = repository.connectionError


    val response: LiveData<String>
        get() = repository.serverResponse


    fun rest() {
        repository.restAddProductVariables()

    }

    fun upload(
        product_name: String,
        product_type: String,
        price: String,
        tax: String,
        fileUri: Uri
    ) {
        repository.uploadProduct(
            product_name,
            product_type,
            price,
            tax,
            fileUri)
    }

}