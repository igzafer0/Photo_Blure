package com.igzafer.photoblure

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeViewModel : ViewModel() {
    lateinit var currentBitmap: Bitmap
    var blackWhite = false;
    val doneBitmap: MutableLiveData<Bitmap> = MutableLiveData()

    fun doFactor(kernel: FloatArray) {
        CoroutineScope(Dispatchers.Default).launch {
            val offset = 2;
            val width: Int = currentBitmap.width
            val height: Int = currentBitmap.height
            val pixels = IntArray(width * height)
            val newPixels = IntArray(width * height)
            currentBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            for (i in 0 until width * height) {
                newPixels[i] = pixels[i];
            }


            for (i in offset until width - offset) {
                for (j in offset until height - offset) {
                    val resultP = IntArray(3);
                    resultP[0] = 0;//red
                    resultP[1] = 0;//green
                    resultP[2] = 0;//blue
                    for (mi in (-offset)..(offset)) {
                        for (mj in (-offset)..(offset)) {


                            val resultPixel = pixels[(i + mi) * width + (j + mj)]
                            var red = resultPixel and 0x00ff0000 shr 16;
                            var green = resultPixel and 0x0000ff00 shr 8;
                            var blue = resultPixel and 0x000000ff;
                            if (blackWhite) {
                                val grey = (0.2126 * red + 0.7152 * green + 0.0722 * blue).toInt();
                                red = grey;
                                green = grey;
                                blue = grey;
                            }
                            val karnelIndex =
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
                val newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                newBmp.setPixels(newPixels, 0, width, 0, 0, width, height)
                doneBitmap.postValue(newBmp)

            }

        }


    }

}