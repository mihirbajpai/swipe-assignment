package com.example.swipeassignment.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.swipeassignment.model.Product
import com.example.swipeassignment.service.ProductService
import com.example.swipeassignment.utils.FileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProductRepository(private val ctx: Context) {

    suspend fun fetchProducts(): List<Product> {
        return ProductService.getInstance().getProducts()
    }

    val connectionError = MutableLiveData("")
    val serverResponse = MutableLiveData("")


    fun restAddProductVariables() {
        connectionError.value = ""
        serverResponse.value = ""

    }


    @SuppressLint("SuspiciousIndentation")
    fun uploadProduct(
        product_name: String,
        product_type: String,
        price: String,
        tax: String,
        fileUri: Uri
    ) {


        val fileToSend = prepareFilePart("files[]", fileUri)
        val productNameRequestBody: RequestBody =
            product_name.toRequestBody("text/plain".toMediaTypeOrNull())
        val productTypeRequestBody: RequestBody =
            product_type.toRequestBody("text/plain".toMediaTypeOrNull())
        val priceRequestBody: RequestBody = price.toRequestBody("text/plain".toMediaTypeOrNull())
        val taxRequestBody: RequestBody = tax.toRequestBody("text/plain".toMediaTypeOrNull())

        ProductService.getInstance().addProduct(
            productNameRequestBody,
            productTypeRequestBody,
            priceRequestBody,
            taxRequestBody,
            fileToSend
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.body() != null && response.isSuccessful) {
                    try {
                        if (response.code() == 200) {
                            serverResponse.value = "uploaded"
                            Toast.makeText(ctx, "Uploaded", Toast.LENGTH_SHORT).show()

                        } else {

                            connectionError.value = response.errorBody().toString()
                            Toast.makeText(
                                ctx,
                                "error..." + response.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: Exception) {
                        connectionError.value = e.message.toString()
                        Toast.makeText(ctx, e.toString(), Toast.LENGTH_SHORT).show()

                    }
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                connectionError.value = t.message.toString()
            }
        })

    }


    private fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part {


        val file: File = FileUtils.getFile(ctx, fileUri)

        val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull());
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }


}