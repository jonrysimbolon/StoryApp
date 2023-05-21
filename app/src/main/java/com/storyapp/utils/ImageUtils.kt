package com.storyapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class ImageUtils {
    companion object {
        private const val MAXIMAL_SIZE = 1024 * 1024
        private const val PHOTO = "photo"
        private const val REQUEST_TYPE_IMAGE = "image/jpeg"
    }

    suspend fun imageMultipart(fileAddPhoto: File?): MultipartBody.Part =
        withContext(Dispatchers.Default) {
            val file = reduceFileImage(fileAddPhoto as File)
            val requestImageFile = file.asRequestBody(REQUEST_TYPE_IMAGE.toMediaType())
            return@withContext MultipartBody.Part.createFormData(
                PHOTO,
                file.name,
                requestImageFile
            )
        }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val myFile = File.createTempFile(PREFIX_FILE, SUFFIX_FILE, context.cacheDir)

        contentResolver.openInputStream(selectedImg)?.use { input ->
            myFile.outputStream().use { output ->
                val buf = ByteArray(1024)
                var len: Int
                while (input.read(buf).also { len = it } > 0) {
                    output.write(buf, 0, len)
                }
            }
        }

        return myFile
    }

    private suspend fun reduceFileImage(file: File): File = withContext(Dispatchers.Default) {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int

        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        var bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size

        while (streamLength > MAXIMAL_SIZE && compressQuality > 0) {
            compressQuality -= 5
            bmpStream.reset()

            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
        }

        file.outputStream().use { outputStream ->
            outputStream.write(bmpPicByteArray)
        }

        return@withContext file
    }
}