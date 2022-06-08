package com.igzafer.photoblure

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class HomeViewModel : ViewModel() {
    val currentBitmap: MutableLiveData<Bitmap> = MutableLiveData()



}