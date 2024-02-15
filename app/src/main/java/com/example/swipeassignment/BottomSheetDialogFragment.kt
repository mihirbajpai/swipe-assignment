package com.example.swipeassignment

import android.app.Activity.RESULT_OK
import android.content.Intent
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var editTextProductName: EditText
    private lateinit var editTextProductType: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var editTextTax: EditText
    private lateinit var buttonChooseImage: Button
    private lateinit var imageViewPreview: ImageView
    private lateinit var buttonSubmit: Button
    private var productName = ""
    private var productType = ""
    private var price = ""
    private var tax = ""

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
            openGallery()
        }

        productName = editTextProductName.text.toString()
        productType = editTextProductType.text.toString()
        price = editTextPrice.text.toString()
        tax = editTextTax.text.toString()

        buttonSubmit.setOnClickListener {
            if (productName.isEmpty()) {
                Toast.makeText(context, "Please enter product name first.", Toast.LENGTH_SHORT)
                    .show()
            } else if (productType.isEmpty()) {
                Toast.makeText(context, "Please enter product type first.", Toast.LENGTH_SHORT)
                    .show()
            } else if (price.isEmpty()) {
                Toast.makeText(context, "Please enter price first.", Toast.LENGTH_SHORT)
                    .show()
            } else if (tax.isEmpty()) {
                Toast.makeText(context, "Please enter tax first.", Toast.LENGTH_SHORT)
                    .show()
            } else {

            }
            dismiss()
        }

        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                imageUri = uri
                imageViewPreview.setImageURI(uri)
                imageViewPreview.visibility = View.VISIBLE
                buttonChooseImage.visibility = View.GONE
                buttonSubmit.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 100
    }

}