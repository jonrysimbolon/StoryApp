package com.storyapp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.storyapp.R

class LoadingDialog(context: Context) {

    private val dialog: Dialog = Dialog(context)

    init {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_loading, ConstraintLayout(context))
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

    fun show(show: Boolean) {
        if(show) {
            dialog.show()
        }else{
            dialog.dismiss()
        }
    }
}
