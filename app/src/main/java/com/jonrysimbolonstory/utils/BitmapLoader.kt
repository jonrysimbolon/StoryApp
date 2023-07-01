package com.jonrysimbolonstory.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream
import java.net.URL

class BitmapLoader(private val options: BitmapFactory.Options) {
    fun loadBitmapFromUrl(url: String): Bitmap? {
        val inputStream: InputStream = URL(url).openStream()
        return BitmapFactory.decodeStream(inputStream, null, options)
    }
}
