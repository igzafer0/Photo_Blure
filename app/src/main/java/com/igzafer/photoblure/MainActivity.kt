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

        viewModel.currentBitmap.observe(this) {


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
    }

    fun deneme(it: Bitmap) {
        Log.d("winter", it.width.toString())
        binding.ImageView.setImageBitmap(it)
        var blackWhite = true;
        val offset = 2;
        // array size = (offset*2)+1
        val kernel = floatArrayOf(
            -1.0f, -2.0f, -4.0f, -2.0f, -1.0f,
            -2.0f, -4.0f, -8.0f, -4.0f, -2.0f,
             0.0f,  0.0f,  0.0f,  0.0f,  0.0f,
             1.0f,  2.0f,  4.0f,  2.0f,  1.0f,
             2.0f,  4.0f,  8.0f,  4.0f,  2.0f
        )
        //val kernel = floatArrayOf(
        //    -1.0f, -1.0f, -1.0f,
        //    -1.0f, 8.0f, -1.0f,
        //    -1.0f, -1.0f, -1.0f
        //)
        //val kernel = floatArrayOf(
        //    1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
        //    1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f,
        //    1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f
        //)

        val width: Int = it.width
        val height: Int = it.height
        val pixels = IntArray(width * height)
        val newPixels = IntArray(width * height)
        it.getPixels(pixels, 0, width, 0, 0, width, height);
        for (i in 0 until width * height) {
            newPixels[i] = 0;
        }
        CoroutineScope(Dispatchers.Default).launch {

            for (i in offset until width - offset) {
                for (j in offset until height - offset) {
                    var resultP = IntArray(3);
                    resultP[0] = 0;//red
                    resultP[1] = 0;//green
                    resultP[2] = 0;//blue
                    for (mi in (-offset)..(offset)) {
                        for (mj in (-offset)..(offset)) {


                            val resultPixel = pixels[(i + mi) * width + (j + mj)]
                            // int değeri 4 byte yer kaplar
                            // rgba daki her bir değer (red) 1byte yer kaplar
                            // yani 1 int in içine 0-255 e kadar olan rgba değerleri atanabilir
                            // bu int in içinde sadece r(red) yi almak istersek 3. byte hariç tüm bitleri sıfırlamamız
                            // sonra 3. byte nin 1. byte nin yerine kadar kaydırmamız gerekir
                            // *Pixel and 0x00ff0000* 3. byte hariç dier byte leri sıfırlar
                            // *shr 16* 16 bit(2 byte) sağ doğru kaydırı

                            val red = resultPixel and 0x00ff0000 shr 16;
                            val green = resultPixel and 0x0000ff00 shr 8;
                            val blue = resultPixel and 0x000000ff;
                            if(blackWhite) {
                                var grey = (0.2126 * red + 0.7152 * green + 0.0722 * blue).toInt();
                                red = grey;
                                green = grey;
                                blue = grey;
                            }
                            var karnelIndex =
                                (mi + offset) * ((offset * 2) + 1) + (mj + offset)
                            resultP[0] += (red * kernel[karnelIndex]).toInt();
                            resultP[1] += (green * kernel[karnelIndex]).toInt();
                            resultP[2] += (blue * kernel[karnelIndex]).toInt();
                        }
                    }
                    for (k in resultP.indices) {
                        if (resultP[k] < 0) resultP[k] = 0;
                        if (resultP[k] > 255) resultP[k] = 255;
                    }
                    var rgbToInt: Int = 255 shl 24;
                    rgbToInt += resultP[0] shl 16;
                    rgbToInt += resultP[1] shl 8;
                    rgbToInt += resultP[2];
                    newPixels[i * width + j] = rgbToInt;
                }
            }
            CoroutineScope(Dispatchers.Main).launch {
                val newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                newBmp.setPixels(newPixels, 0, width, 0, 0, width, height)
                binding.ImageView.setImageBitmap(newBmp)
            }
        }

    }

    private var currentPhotoPath: String = ""
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val options = BitmapFactory.Options()
                val imageBitmap = BitmapFactory.decodeFile(currentPhotoPath, options)
                if (imageBitmap != null) {
                    deneme(imageBitmap)
                }

            }
        }

}
