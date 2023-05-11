package com.cookiss.jobseekerapp.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

class CommonUtils {

    fun hideSoftKeyboard(view: View) {
        try {
            val inputMethodManager =
                view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        } catch (ignored: Exception) {
        }
    }
}