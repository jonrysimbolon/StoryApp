package com.jonrysimbolonstory.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textview.MaterialTextView
import com.jonrysimbolonstory.R

class StoryFailureDialog {

    private var dialog: Dialog? = null
    private var constraintLayout: ConstraintLayout? = null

    private var reloadAction: (() -> Unit)? = null
    private var logoutAction: (() -> Unit)? = null

    fun init(context: Context) {
        context.let {
            dialog = Dialog(it)
            constraintLayout = ConstraintLayout(it)
            val inflater = LayoutInflater.from(it)
            val dialogView = inflater.inflate(R.layout.dialog_error, constraintLayout)
            val reloadBtn = dialogView.findViewById<Button>(R.id.reloadBtn)
            val logoutBtn = dialogView.findViewById<Button>(R.id.logoutBtn)
            reloadBtn.setOnClickListener {
                reloadAction?.invoke()
                show(false)
            }
            logoutBtn.setOnClickListener {
                logoutAction?.invoke()
                show(false)
            }

            dialog?.setContentView(dialogView)
            dialog?.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setDimAmount(1f)
                setFlags(
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND
                )
            }
            dialog?.setCancelable(false)
        }
    }

    fun setDescription(description: String) {
        val descriptionTextView = dialog?.findViewById<MaterialTextView>(R.id.descFailed)
        descriptionTextView?.text = description
    }

    fun show(show: Boolean = true) {
        dialog?.let {
            if (show) {
                it.show()
            } else {
                it.dismiss()
            }
        }
    }

    fun setReloadClickListener(function: (() -> Unit)?) {
        this.reloadAction = function
    }

    fun setLogoutClickListener(function: (() -> Unit)?) {
        this.logoutAction = function
    }
}