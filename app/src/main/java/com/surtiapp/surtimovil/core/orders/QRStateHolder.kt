package com.surtiapp.surtimovil.core.orders

import android.graphics.Bitmap
import kotlinx.coroutines.flow.MutableStateFlow

object QRStateHolder {
    val qrImage = MutableStateFlow<Bitmap?>(null)

    fun setQR(bmp: Bitmap?) {
        qrImage.value = bmp
    }
}
