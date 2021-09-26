package com.example.jokebot.model

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.example.jokebot.R

class ProgressButton(private var context: Context, view: View) {
    private var progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
    private var textView = view.findViewById<TextView>(R.id.button_text)

    fun buttonActivated() {
        progressBar.visibility = View.VISIBLE
        textView.text = context.getString(R.string.please_wait)
    }

    fun buttonFinished() {
        progressBar.visibility = View.GONE
        textView.text = context.getString(R.string.joke_text)
    }
}