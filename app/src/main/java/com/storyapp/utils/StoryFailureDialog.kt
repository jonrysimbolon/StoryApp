package com.storyapp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textview.MaterialTextView
import com.storyapp.R

class StoryFailureDialog(
    context: Context
) {

    private val dialog: Dialog = Dialog(context)
    private var function: (() -> Unit)? = null

    init {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_error, ConstraintLayout(context))
        val reloadBtn = dialogView.findViewById<AppCompatButton>(R.id.reloadBtn)
        reloadBtn.setOnClickListener {
            function?.invoke()
            show(false)
        }
        dialog.setContentView(dialogView)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.5f)
            setFlags(
                WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND
            )
        }
        dialog.setCancelable(false)
    }

    fun setDescription(description: String) {
        val descriptionTextView = dialog.findViewById<MaterialTextView>(R.id.descFailed)
        descriptionTextView.text = description
    }

    fun show(show: Boolean) {
        if (show) {
            dialog.show()
        } else {
            dialog.dismiss()
        }
    }

    fun setOnClick(function: (() -> Unit)?) {
        this.function = function
    }
}