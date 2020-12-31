package edu.utap.quiz_me

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
    private var difficultyLevel = "Easy"
    private var question = MutableLiveData<TriviaQuestion>()
    private val triviaApi = TriviaApi.create()
    private val repository = Repository(triviaApi)
    private var token: String = "" // API uses this token to ensure no duplicate questions to the same user

    init {
        getToken()
    }

    fun setDifficulty(level: String) {
        difficultyLevel = when(level.toLowerCase(Locale.getDefault())) {
            "easy" -> "easy"
            "medium" -> "medium"
            "hard" -> "hard"
            else -> "medium"
        }
        Log.d(javaClass.simpleName, "level $level / difficulty $difficultyLevel")
    }

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
            question.postValue(repository.getQuestion(difficultyLevel, token))
            Log.d(javaClass.simpleName, "question ${question.value}")
        }
    }

    fun observeQuestion(): LiveData<TriviaQuestion> {
        return question
    }
}