package com.example.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.Place
import com.google.gson.Gson

object PlaceDao {
    //将place对象存储到SharedPreferences文件中
    fun savePlace(place : Place){
        sharedPreferences().edit {
            putString("place",Gson().toJson(place))
        }
    }
    //先读取到Json字符串然后转换为Place对象
    fun getSavedPlace() : Place {
        val placeJson = sharedPreferences().getString("place","")
        return Gson().fromJson(placeJson,Place::class.java)
    }
    //判断是否存储
    fun isPlaceSaved() = sharedPreferences().contains("place")
    private fun sharedPreferences() =
        SunnyWeatherApplication.context.
        getSharedPreferences("sunny_weather",
        Context.MODE_PRIVATE)
}