package com.ptit.signlanguage.base

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


fun EditText.onTextChange() = callbackFlow<String> {
    val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            trySend(s.toString())
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
    addTextChangedListener(listener)
    awaitClose {
        removeTextChangedListener(listener)
        Log.d("Test", "Close")

    }
}