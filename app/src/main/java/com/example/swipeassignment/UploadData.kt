package com.example.swipeassignment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.swipeassignment.viewmodel.ProductViewModel
import com.example.swipeassignment.viewmodel.ProductViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadData : AppCompatActivity() {
    private lateinit var permReqLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var viewModel: ProductViewModel
    private lateinit var selectedFile: Uri
    private lateinit var tvFile: TextView
    private lateinit var prodName: EditText
    private lateinit var prodType: EditText
    private lateinit var prodPrice: EditText
    private lateinit var prodTax: EditText

    private var Permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private lateinit var uploadBtn: Button
    private lateinit var pickBtn: Button

    private lateinit var imageViewPreview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_data)

        tvFile = findViewById(R.id.textView)
        prodName = findViewById(R.id.editTextProductName)
        prodType = findViewById(R.id.editTextProductType)
        prodPrice = findViewById(R.id.editTextPrice)
        prodTax = findViewById(R.id.editTextTax)

        uploadBtn = findViewById(R.id.buttonSubmit)
        pickBtn = findViewById(R.id.buttonChooseImage)

        imageViewPreview = findViewById(R.id.imageViewPreview)

        val viewModelFactory = ProductViewModelFactory(this)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(ProductViewModel::class.java)

        viewModel.response.observe(this, Observer {
            if (it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.rest()
            }
        })

        viewModel.connectionError.observe(this, Observer {
            if (it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.rest()
            }
        })

        permReqLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val granted = permissions.entries.all { it.value }
                if (granted) {
                    selectImage()
                } else {
                    Toast.makeText(this@UploadData, "No Permission Granted", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        pickBtn.setOnClickListener {
            pick()
        }
        uploadBtn.setOnClickListener {
            val name = prodName.text.toString().trim()
            val type = prodType.text.toString().trim()
            val price = prodPrice.text.toString().trim()
            val tax = prodTax.text.toString().trim()
            upload(name, type, price, tax)
        }
    }

    private fun upload(name: String, type: String, price: String, tax: String) {
        Toast.makeText(this, "uploading...", Toast.LENGTH_SHORT).show()

        viewModel.viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    viewModel.upload(name, type, price, tax, selectedFile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun pick() {
        if (hasPermissions(this@UploadData, Permissions)) {
            selectImage()
        } else {
            permReqLauncher.launch(Permissions)
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedFile = result.data?.data!!
                imageViewPreview.setImageURI(selectedFile)
                imageViewPreview.visibility = View.VISIBLE
                pickBtn.visibility = View.GONE
                tvFile.text = selectedFile.path.toString()
            }
        }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        resultLauncher.launch(intent)
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
}
