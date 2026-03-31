package com.example.sunnyweather.logic.network

import android.util.Log
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {
    private const val MAX_RETRIES = 3
    private const val RETRY_DELAY_MS = 1200L

    private val weatherService = ServiceCreator.create(WeatherService::class.java)

    suspend fun getDailyWeather(lng: String, lat: String) =
        weatherService.getDailyWeather(lng, lat).awaitWithRetry()

    suspend fun getRealtimeWeather(lng: String, lat: String) =
        weatherService.getRealtimeWeather(lng, lat).awaitWithRetry()

    private val placeService = ServiceCreator.create(PlaceService::class.java)

    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).awaitWithRetry()

    private suspend fun <T> Call<T>.awaitWithRetry(
        maxRetries: Int = MAX_RETRIES,
        retryDelayMs: Long = RETRY_DELAY_MS
    ): T {
        var lastError: Throwable? = null
        repeat(maxRetries + 1) { attempt ->
            val call = if (attempt == 0) this else clone()
            try {
                return call.awaitOnce()
            } catch (e: Exception) {
                lastError = e
                val isLastAttempt = attempt == maxRetries
                Log.w(
                    "SunnyWeatherNetWork",
                    "request attempt ${attempt + 1}/${maxRetries + 1} failed: ${call.request().url()}",
                    e
                )
                if (!isLastAttempt) {
                    delay(retryDelayMs)
                }
            }
        }
        throw lastError ?: RuntimeException("request failed with unknown error")
    }

    private suspend fun <T> Call<T>.awaitOnce(): T {
        return suspendCoroutine { continuation ->
            Log.d("SunnyWeatherNetWork", "start request: ${request().url()}")
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        continuation.resume(body)
                    } else {
                        val requestUrl = call.request().url().toString()
                        val code = response.code()
                        val message = response.message()
                        val errorBody = try {
                            response.errorBody()?.string().orEmpty()
                        } catch (e: Exception) {
                            "failed to read errorBody: ${e.message}"
                        }
                        Log.e(
                            "SunnyWeatherNetWork",
                            "response body is null, url=$requestUrl, code=$code, message=$message, errorBody=$errorBody"
                        )
                        continuation.resumeWithException(
                            RuntimeException(
                                "response body is null, code=$code, message=$message, url=$requestUrl, errorBody=$errorBody"
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    Log.e(
                        "SunnyWeatherNetWork",
                        "request failed, url=${call.request().url()}",
                        t
                    )
                    continuation.resumeWithException(t)
                }
            })
        }
    }


}
