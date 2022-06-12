package com.igzafer.photoblure

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.igzafer.photoblure.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: HomeViewModel

    val karnels = listOf(
        floatArrayOf(
            -1.0f, -2.0f, -4.0f, -2.0f, -1.0f,
            -2.0f, -4.0f, -8.0f, -4.0f, -2.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            1.0f, 2.0f, 4.0f, 2.0f, 1.0f,
            2.0f, 4.0f, 8.0f, 4.0f, 2.0f
        ),
        floatArrayOf(
            1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f,
            1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f,
            1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f,
            1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f,
            1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f, 1.0f / 25.0f,
        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        viewModel.doneBitmap.observe(this) {
            binding.ImageView.setImageBitmap(it)
        }

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.names,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.spinner.adapter = adapter
        }
        binding.takePicture.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    100
                )
            } else {
                val fileName = "photo";
                val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val imageFile = File.createTempFile(fileName, ".jpg", storageDirectory)
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                currentPhotoPath = imageFile.absolutePath
                val uri = FileProvider.getUriForFile(
                    applicationContext,
                    "com.igzafer.photoblure",
                    imageFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startForResult.launch(takePictureIntent)

            }

        }
        binding.doIt.setOnClickListener {
            if (binding.defaultKarnel.isChecked) {
                viewModel.doFactor(karnels[binding.spinner.selectedItemPosition])

            } else {
                try {
                    val karnel = floatArrayOf(
                        binding.number1.text.toString().toFloat(),
                        binding.number2.text.toString().toFloat(),
                        binding.number3.text.toString().toFloat(),
                        binding.number4.text.toString().toFloat(),
                        binding.number5.text.toString().toFloat(),
                        binding.number6.text.toString().toFloat(),
                        binding.number7.text.toString().toFloat(),
                        binding.number8.text.toString().toFloat(),
                        binding.number9.text.toString().toFloat(),
                        binding.number10.text.toString().toFloat(),
                        binding.number11.text.toString().toFloat(),
                        binding.number12.text.toString().toFloat(),
                        binding.number13.text.toString().toFloat(),
                        binding.number14.text.toString().toFloat(),
                        binding.number15.text.toString().toFloat(),
                        binding.number16.text.toString().toFloat(),
                        binding.number17.text.toString().toFloat(),
                        binding.number18.text.toString().toFloat(),
                        binding.number19.text.toString().toFloat(),
                        binding.number20.text.toString().toFloat(),
                        binding.number21.text.toString().toFloat(),
                        binding.number22.text.toString().toFloat(),
                        binding.number23.text.toString().toFloat(),
                        binding.number24.text.toString().toFloat(),
                        binding.number25.text.toString().toFloat(),
                    )
                    viewModel.doFactor(karnel)
                } catch (e: Exception) {

                }

            }
        }
        binding.blackWhite.setOnClickListener {
            viewModel.blackWhite = binding.blackWhite.isChecked
        }
        binding.defaultKarnel.setOnClickListener {
            binding.userKarnelLl.visibility =
                if (binding.defaultKarnel.isChecked) View.GONE else View.VISIBLE
        }
    }


    private var currentPhotoPath: String = ""
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val options = BitmapFactory.Options()
                val imageBitmap = BitmapFactory.decodeFile(currentPhotoPath, options)
                if (imageBitmap != null) {
                    val resized = Bitmap.createScaledBitmap(imageBitmap, 720, 720, true);
                    viewModel.currentBitmap = resized
                    binding.ImageView.setImageBitmap(resized)
                    binding.doIt.isEnabled = true
                }

            }
        }

}