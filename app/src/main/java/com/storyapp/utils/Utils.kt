package com.storyapp.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar

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