package com.example.jokebot

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.getInstance()
        api = retrofit?.create(RetrofitService::class.java)!!

        textToSpeech = TextToSpeech(applicationContext) { status ->
            run {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.language = Locale.US
                }
            }
        }

        button = findViewById(R.id.joke_button)
        button.setOnClickListener { v -> onClick(v) }

        progressButton = ProgressButton(this, button)
    }

    override fun onClick(v: View?) {
        if (textToSpeech.isSpeaking) {
            showToast("Chill for one moment")
        } else {
            fetchJoke()
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
                    val joke = if (obj?.joke != null) obj.joke else "${obj?.setup} $${obj?.delivery}"
                    tellJoke(joke)
                }
            }

            override fun onFailure(call: Call<Joke>, t: Throwable) {
                showToast("Something went wrong: ${t.localizedMessage}")
            }

        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onPause() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onPause()

    }
}