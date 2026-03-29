package com.example.sunnyweather.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetWork {
    private val placeService = ServiceCreator.create<PlaceService>()
    suspend fun searchPlaces(query : String) = placeService.searchPlaces(query).await()
    private suspend fun <T> Call<T>.await():T{
        return suspendCoroutine { continuation->
            //continuation协程暂停后，恢复它的“遥控器”
            //发起网络请求 JSON数据返回 自动转换为PlaceResponse对象 触发OnResponse（）
            enqueue(object : Callback<T>{
                override fun onResponse(p0: Call<T?>?, p1: Response<T?>?) {
                    val body = p1?.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response is null")
                    )
                }
                override fun onFailure(p0: Call<T?>?, p1: Throwable?) {
                    if (p1 != null) {
                        continuation.resumeWithException(p1)
                    }
                }

            })
        }
    }
}