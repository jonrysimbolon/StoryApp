package com.jonrysimbolonstory.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.jonrysimbolonstory.R

class LoadingDialog {
    private var dialog: Dialog? = null
    private var constraintLayout: ConstraintLayout? = null

    fun init(context: Context) {
        context.let {
            dialog = Dialog(it)
            constraintLayout = ConstraintLayout(it)
            val inflater = LayoutInflater.from(it)
            val dialogView = inflater.inflate(R.layout.dialog_loading, constraintLayout)
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

    fun show(show: Boolean = true) {
        dialog?.let {
            if (show) {
                it.show()
            } else {
                it.dismiss()
            }
        }
    }
}
