package com.igzafer.photoblure

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.igzafer.photoblure.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
            val width: Int = it.width
            val height: Int = it.height
            val pixels = IntArray(width * height)
            it.getPixels(pixels, 0, width, 0, 0, width, height);
            if (it != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val alpha = 0xFF shl 24 // ?bitmap?24?
                    for (i in 0 until width) {
                        for (j in 0 until height) {
                          for (mi in 0..3){
                              for (mj in 0..3){

                              }
                          }
                        }
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        val newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                        newBmp.setPixels(pixels, 0, width, 0, 0, width, height)
                        binding.ImageView.setImageBitmap(newBmp)
                    }
                }

            }
        })
        binding.takePicture.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startForResult.launch(Intent(takePictureIntent))
                }
            } else {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startForResult.launch(Intent(takePictureIntent))
            }

        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data!!
                val imageBitmap = intent.extras!!.get("data") as Bitmap
                viewModel.currentBitmap.value = imageBitmap
            }
        }

}