package com.example.jokebot.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.jokebot.R
import com.example.jokebot.api.Retrofit
import com.example.jokebot.api.RetrofitService
import com.example.jokebot.model.Joke
import com.example.jokebot.model.ProgressButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var api: RetrofitService
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var button: View
    private lateinit var progressButton: ProgressButton
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var language = Locale.US

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.getInstance()
        api = retrofit?.create(RetrofitService::class.java)!!

        button = findViewById(R.id.joke_button)
        button.setOnClickListener { v -> onClick(v) }

        progressButton = ProgressButton(this, button)

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val possibleMatches =
                        result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!!

                    var matches = false
                    for (s in possibleMatches) {
                        val containsFunny = s.matches(Regex(".*funny.*"))
                        val containsJoke = s.matches(Regex(".*joke.*"))
                        if (containsFunny || containsJoke) {
                            matches = true
                            fetchJoke()
                        }
                    }

                    if (!matches) {
                        textToSpeech.speak(
                            "I'm sorry, I can't help you with that",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                    }

                }
            }
    }

    private fun initTTS() {
        textToSpeech = TextToSpeech(applicationContext) { status ->
            run {
                if (status != TextToSpeech.ERROR) {
                    updateLang()
                    textToSpeech.language = language
                    textToSpeech.voice = Voice("en-us-x-sfg#male_2-local", Locale.US, 400,200,true, setOf("male"))
                }
            }
        }
    }

    private fun tellJoke(joke: String) {
        textToSpeech.speak(joke, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun fetchJoke() {
        progressButton.buttonActivated()

        val call = api.getJoke("Programming", "religious,political")
        call.enqueue(object : Callback<Joke> {
            override fun onResponse(call: Call<Joke>, response: Response<Joke>) {
                progressButton.buttonFinished()

                if (response.isSuccessful) {
                    val obj = response.body()
                    val joke =
                        if (obj?.joke != null) obj.joke else "${obj?.setup} $${obj?.delivery}"
                    tellJoke(joke)
                }
            }

            override fun onFailure(call: Call<Joke>, t: Throwable) {
                progressButton.buttonFinished()
                showToast("Something went wrong: ${t.localizedMessage}")
            }

        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun updateLang() {
        val preferences = getSharedPreferences(SettingsActivity.PREFERENCE_NAME, MODE_PRIVATE)
        when (preferences.getInt("lang", 0)) {
            0 -> language = Locale.US
            1 -> language = Locale.ITALIAN
            2 -> language = Locale.FRENCH
            3 -> language = Locale.GERMAN
            4 -> language = Locale.JAPANESE
        }
    }

    private fun startRecognizerActivity() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "I'm listening")
        activityResultLauncher.launch(intent)
    }

    private fun startSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(v: View?) {
//        Log.d("TAG", "onClick: ${textToSpeech.isLanguageAvailable(language)}")
//        Log.d("TAG", "onClick: ${language}")
        if (textToSpeech.isSpeaking) {
            showToast("Chill for one moment")
        } else {
            fetchJoke()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_settings) {
//            startSettingsActivity()
            showToast("Coming soon.")
        } else if (item.itemId == R.id.menu_voice_command) {
            startRecognizerActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        initTTS()
    }

    override fun onPause() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onPause()

    }
}