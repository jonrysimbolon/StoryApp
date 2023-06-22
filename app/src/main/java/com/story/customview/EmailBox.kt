package com.story.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.story.R

class EmailBox(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs),
    TextWatcher {

    init {
        addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
        if (!s.toString().isValidEmail()) {
            showError()
        } else {
            hideError()
        }
    }

    private fun showError() {
        error = context.getString(R.string.error_email)
    }

    private fun hideError() {
        error = null
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}

fun String.isValidEmail(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()
