package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.SunnyWeatherNetWork
import kotlinx.coroutines.Dispatchers
//
object Repository {
    //指定成了/Dispatchers.IO，这样代码块中的所有代码就都运行在子线程中了
    fun searchPlaces(query : String) = liveData(Dispatchers.IO){
        val resutl = try {
            val placeResponse = SunnyWeatherNetWork.searchPlaces(query)
            if (placeResponse.status == "ok" ){
                val place = placeResponse.places
                Result.success(place)
            }else{
                Result.failure(RuntimeException("response status is" +
                        "${placeResponse.status}"))
            }
        }catch (e : Exception){
            Result.failure<List<Place>>(e)
        }
        //Result.success()方法来包装获
        //取的城市数据列表，否则使用Result.failure()方法来包装一个异常信息。最后使用一个
        //emit()方法将包装的结果发射出去，
        emit(resutl)
    }
}