package com.storyapp.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.storyapp.R
import com.storyapp.model.StoryModel
import com.storyapp.remote.response.Response
import com.storyapp.remote.response.Story
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val MAXIMAL_SIZE = 1000000
const val dateFormatFromServer = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
fun dateFormat(dateStr: String): String {
    val inputFormat = SimpleDateFormat(dateFormatFromServer, Locale.getDefault())

    val outputFormat =
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault())

    try {
        val date: Date = inputFormat.parse(dateStr) ?: return ""
        return outputFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return ""
}

fun convertStoryToStoryModel(story: Story): StoryModel = StoryModel(
    story.createdAt,
    story.description,
    story.id,
    story.lat,
    story.lon,
    story.name,
    story.photoUrl
)

fun showValidToken(token: String): String = "Bearer $token"
fun responseGsonPattern(gson: Gson, json: String?): Response =
    gson.fromJson(json, Response::class.java)

fun String.showSnackBarAppearBriefly(view: View) {
    Snackbar.make(view, this, Snackbar.LENGTH_SHORT).show()
}

fun ImageView.play() {
    ObjectAnimator.ofFloat(this, View.TRANSLATION_X, -30f, 30f)
        .apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
}

fun doAnimation(imageView: ImageView, btn: Button, vararg view: View) {

    imageView.play()

    val viewAnimate = mutableListOf<ObjectAnimator>()

    val animSet = mutableListOf<AnimatorSet>()
    val sizeHalfView = view.size / 2

    view.forEach {
        viewAnimate.add(ObjectAnimator.ofFloat(it, View.ALPHA, 1f).setDuration(500))
    }

    for (j in 0 until sizeHalfView) {
        val animatorSet = AnimatorSet().apply {
            playTogether(viewAnimate[j * 2], viewAnimate[j * 2 + 1])
        }
        animSet.add(animatorSet)
    }

    val buttonAnimate = ObjectAnimator.ofFloat(btn, View.ALPHA, 1f).setDuration(500)

    AnimatorSet().apply {
        playSequentially(*animSet.toTypedArray(), buttonAnimate)
        start()
    }
}

fun reduceFileImage(file: File): File {
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

    FileOutputStream(file).use { outputStream ->
        outputStream.write(bmpPicByteArray)
    }

    return file
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver = context.contentResolver
    val myFile = File.createTempFile("temp_", ".jpg", context.cacheDir)

    contentResolver.openInputStream(selectedImg)?.use { input ->
        FileOutputStream(myFile).use { output ->
            val buf = ByteArray(1024)
            var len: Int
            while (input.read(buf).also { len = it } > 0) {
                output.write(buf, 0, len)
            }
        }
    }

    return myFile
}


fun isValidAddPhoto(
    file: File?,
    tie_Desc: TextInputEditText,
    errorFile: (Boolean) -> Unit,
    errorDesc: () -> String
): Boolean{
    return when{
        file == null -> {
            errorFile(true)
            false
        }
        tie_Desc.text == null || tie_Desc.text?.isEmpty() == true -> {
            tie_Desc.error = errorDesc()
            false
        }
        else -> true
    }
}

fun isValidLogin(
    emailBox: EditText,
    passBox: EditText,
    errorEmail: () -> String,
    errorPass: () -> String,
): Boolean {
    return when {
        emailBox.text.isEmpty() || emailBox.error != null -> {
            emailBox.error = errorEmail()
            false
        }

        passBox.text.isEmpty() || passBox.error != null -> {
            passBox.error = errorPass()
            false
        }

        else -> true
    }
}

fun isValidRegistration(
    nameBox: EditText,
    emailBox: EditText,
    passBox: EditText,
    errorName: () -> String,
    errorEmail: () -> String,
    errorPass: () -> String,
): Boolean {
    return when {
        nameBox.text.isEmpty() -> {
            nameBox.error = errorName()
            false
        }

        emailBox.text.isEmpty() || emailBox.error != null -> {
            emailBox.error = errorEmail()
            false
        }

        passBox.text.isEmpty() || passBox.error != null -> {
            passBox.error = errorPass()
            false
        }

        else -> true
    }
}