package com.example.jokebot.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import com.example.jokebot.R

class SettingsActivity : AppCompatActivity() {
    companion object {
        val PREFERENCE_NAME = "com.example.jokebot.screens.shared_preferences"
    }
    private lateinit var radioGroup: RadioGroup
    private var index: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val preferences = getSharedPreferences(PREFERENCE_NAME ,MODE_PRIVATE)
        index = preferences.getInt("lang", 0)

        radioGroup = findViewById(R.id.languages_radio_group)
        checkLanguage()
    }

    private fun checkLanguage() {
        when (index) {
            0 -> radioGroup.check(R.id.radio_btn_english)
            1 -> radioGroup.check(R.id.radio_btn_italian)
            2 -> radioGroup.check(R.id.radio_btn_french)
            3 -> radioGroup.check(R.id.radio_btn_german)
            4 -> radioGroup.check(R.id.radio_btn_japanese)
        }
    }

    fun saveSetting(view: android.view.View) {
        val editor = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit()
        when (radioGroup.checkedRadioButtonId) {
            R.id.radio_btn_english -> editor.putInt("lang", 0)
            R.id.radio_btn_italian -> editor.putInt("lang", 1)
            R.id.radio_btn_french -> editor.putInt("lang", 2)
            R.id.radio_btn_german -> editor.putInt("lang", 3)
            R.id.radio_btn_japanese -> editor.putInt("lang", 4)
        }
        editor.apply()

        Toast.makeText(this, "Settings saved.", Toast.LENGTH_LONG).show()
        finish()
    }

}