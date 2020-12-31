package edu.utap.quiz_me.api

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApi {

    // GET coroutine based functions
    @GET("api_token.php?command=request")
    suspend fun getToken() : TokenResponse

    @GET("api.php?amount=1&type=boolean")
    suspend fun getQuestion(@Query("difficulty") level: String, @Query("token") token: String): TriviaResponse

    // Data classes for the responses
    data class TokenResponse(val token: String)
    data class TriviaResponse(val results: List<TriviaQuestion>)

    companion object {
        // Base URL
        var url = HttpUrl.Builder()
            .scheme("https")
            .host("opentdb.com")
            .build()

        // Public create function that ties together the base URL and the private GET functions
        fun create(): TriviaApi = create(url)
        private fun create(httpUrl: HttpUrl): TriviaApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TriviaApi::class.java)
        }
    }

}