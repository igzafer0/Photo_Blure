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
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.igzafer.photoblure.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        viewModel.currentBitmap.observe(this, Observer { it ->
            binding.ImageView.setImageBitmap(it)
            //burayı sen ekle üsendim

        })
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
    }

    private var currentPhotoPath: String = ""
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val options = BitmapFactory.Options()
                val imageBitmap = BitmapFactory.decodeFile(currentPhotoPath, options)
                viewModel.currentBitmap.value = imageBitmap
            }
        }

}