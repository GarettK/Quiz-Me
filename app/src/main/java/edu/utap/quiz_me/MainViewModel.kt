package edu.utap.quiz_me

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utap.quiz_me.api.Repository
import edu.utap.quiz_me.api.TriviaApi
import edu.utap.quiz_me.api.TriviaQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel : ViewModel() {
    private var difficulty = MutableLiveData<String>()
    private var question = MutableLiveData<TriviaQuestion>()
    private val triviaApi = TriviaApi.create()
    private val repository = Repository(triviaApi)
    private var token: String = "" // API uses this token to ensure no duplicate questions to the same user
    private var maxHighscore = MutableLiveData(0)
    private var currentHighscore = MutableLiveData(0)

    init {
        getToken()
        difficulty.value = "easy"
    }

    // Load Old Highscore Section////////////////
    fun loadOldHighscore(oldScore: Int) {
        maxHighscore.value = oldScore
    }

    //Set Difficulty, MaxHighscore, CurrentHighscore section////////////////////
    fun setMaxHighscore() {
        if (currentHighscore.value!! > maxHighscore.value!!) {
            maxHighscore.value = currentHighscore.value
        }
    }

    fun incrementCurrentHighscore(incrementAmount: Int) {
        currentHighscore.value = currentHighscore.value?.plus(incrementAmount) ?: incrementAmount
        val value = currentHighscore.value
        currentHighscore.value = value
        Log.d(javaClass.simpleName, "currentHighscore ${currentHighscore.value}")
    }

    fun resetCurrentHighscore() {
        currentHighscore.value = 0
    }

    fun setDifficulty(level: String) {
        difficulty.value = when(level.toLowerCase(Locale.getDefault())) {
            "easy" -> "easy"
            "medium" -> "medium"
            "hard" -> "hard"
            else -> "medium"
        }
        Log.d(javaClass.simpleName, "level $level / difficulty $difficulty")
    }


    //Network GET Requests//////////////////////////
    private fun getToken() {
        viewModelScope.launch (
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
            token = repository.getToken()
            Log.d(javaClass.simpleName, "token $token")
        }
    }

    fun getQuestion() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
            question.postValue(repository.getQuestion(difficulty.value!!, token))
            Log.d(javaClass.simpleName, "question ${question.value}")
        }
    }

    //Observe Section////////////////////////////
    fun observeQuestion(): LiveData<TriviaQuestion> {
        return question
    }

    fun observeDifficulty(): LiveData<String> {
        return difficulty
    }

    fun observeCurrentHighscore(): LiveData<Int> {
        return currentHighscore
    }

    fun observeMaxHighscore(): LiveData<Int> {
        return maxHighscore
    }

    //Observe Once (Get) Section/////////////////////
    fun getMaxHighscore(): Int {
        return maxHighscore.value!!
    }

    fun getcurrentHighscore(): Int {
        return currentHighscore.value!!
    }
}