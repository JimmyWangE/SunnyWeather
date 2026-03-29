package com.example.sunnyweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//创建一个Retrofit构建器
object ServiceCreator {
    private const val BASE_URL = "https://api.caiyunapp.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        //自动把JSON->Kotlin对象
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    //泛型函数 生成接口实现类
    fun <T> create(serviceClass : Class<T>) : T = retrofit.create(serviceClass)
    //类型擦除 正常需要ServiceCreator.create(WeatherService::class.java)
    //现在只需要ServiceCreator.create<WeatherService>() reified只能用在inline函数里面
    inline fun <reified T> create() : T = create(T::class.java)
}
