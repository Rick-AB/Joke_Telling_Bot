package com.example.jokebot.model

data class Joke(
    var error: Boolean,
    var category: String,
    var type: String,
    var setup: String,
    var delivery: String,
    var joke: String,
    var flags: Flags,
    var id: Int,
    var safe: Boolean,
    var lang: String
) {

}