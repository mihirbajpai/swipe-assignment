package com.example.swipeassignment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.swipeassignment.viewmodel.ProductViewModel
import com.example.swipeassignment.viewmodel.ProductViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BottomSheetDialogFragment() : BottomSheetDialogFragment() {

    private lateinit var editTextProductName: EditText
    private lateinit var editTextProductType: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var editTextTax: EditText
    private lateinit var buttonChooseImage: Button
    private lateinit var imageViewPreview: ImageView
    private lateinit var buttonSubmit: Button

    private lateinit var selectedFile: Uri


    private lateinit var viewModel: ProductViewModel


    private var Permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_layout, container, false)

        editTextProductName = view.findViewById(R.id.editTextProductName)
        editTextProductType = view.findViewById(R.id.editTextProductType)
        editTextPrice = view.findViewById(R.id.editTextPrice)
        editTextTax = view.findViewById(R.id.editTextTax)
        buttonChooseImage = view.findViewById(R.id.buttonChooseImage)
        imageViewPreview = view.findViewById(R.id.imageViewPreview)
        buttonSubmit = view.findViewById(R.id.buttonSubmit)

        buttonChooseImage.setOnClickListener {
            pickImage()
        }

        buttonSubmit.setOnClickListener {
            val name = editTextProductName.text.toString().trim()
            val type = editTextProductType.text.toString().trim()
            val price = editTextPrice.text.toString().trim()
            val tax = editTextTax.text.toString().trim()
            if (name.isEmpty() || type.isEmpty() || price.isEmpty() || tax.isEmpty()){
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                upload(name, type, price, tax, selectedFile)
            }
        }

        val viewModelFactory = ProductViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProductViewModel::class.java)

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null && data.data != null) {
                        selectedFile = data.data!!
                        imageViewPreview.setImageURI(selectedFile)
                        imageViewPreview.visibility = View.VISIBLE
                    }
                }
            }

        return view
    }


    fun upload(name: String, type: String, price: String, tax: String, selectedFile: Uri) {
        viewModel.viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    viewModel.upload(name, type, price, tax, selectedFile)
                }
                dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun pickImage() {
        if (hasPermissions(requireContext(), Permissions)) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), Permissions, REQUEST_EXTERNAL_STORAGE_PERMISSION
            )
        }
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    context, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    companion object {
        const val REQUEST_EXTERNAL_STORAGE_PERMISSION = 101
    }
}
