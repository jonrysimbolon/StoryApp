package com.jonrysimbolonstory.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.jonrysimbolonstory.remote.response.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val dateFormatFromServer = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val PREFIX_FILE = "temp_"
const val SUFFIX_FILE = ".jpg"
const val TEXT_PLAIN_TYPE = "text/plain"
const val STORY_DATABASE = "story_database"
const val BEARER = "Bearer"

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}

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

fun showValidToken(token: String): String = BEARER.plus(" ").plus(token)
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

fun descImage(desc: String): RequestBody = desc.toRequestBody(TEXT_PLAIN_TYPE.toMediaType())

fun isValidAddPhoto(
    file: File?,
    tie_Desc: TextInputEditText,
    errorFile: (Boolean) -> Unit,
    ifEmptyDesc: String,
    locationCb: MaterialCheckBox,
    lat: Double?,
    lon: Double?,
    errorLocation: (Boolean) -> Unit
): Boolean {
    return when {
        file == null -> {
            errorFile(true)
            false
        }

        tie_Desc.text == null || tie_Desc.text?.isEmpty() == true -> {
            tie_Desc.setText(ifEmptyDesc)
            false
        }

        locationCb.isChecked -> {
            if (lat != null && lon != null) {
                true
            } else {
                errorLocation(true)
                false
            }
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