package edu.utap.quiz_me.api

class Repository(private val api: TriviaApi) {

    suspend fun getToken(): String {
        return api.getToken().token
    }

    suspend fun getQuestion(difficulty: String, token: String): TriviaQuestion {
        return api.getQuestion(difficulty, token).results[0]
    }
}