package com.example.sunnyweather.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.ui.weather.WeatherActivity

class PlaceAdapter(private val fragment: placeFragment,private val placeList : List<Place>)
    : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    inner class ViewHolder(view : View)  : RecyclerView.ViewHolder(view) {
        val placeName : TextView = view.findViewById(R.id.placeName)
        val placeAddress : TextView = view.findViewById(R.id.placeAddress)
    }
//在onCreateViewHolder()方法中，当点击了任何子项布局时，在跳转到
//WeatherActivity之前，先调用PlaceViewModel的savePlace()方法来存储选中的城市
    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): PlaceAdapter.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.place_item,p0,false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }
            val place  = placeList[position]
            val activity = fragment.activity
            if (activity is  WeatherActivity  ){
                activity.findViewById<DrawerLayout>(R.id.drawerLayout).closeDrawers()
                activity.viewModel.locationLng = place.location.lng
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.placeName = place.name
                activity.refreshWeather()
            }else{
                val intent = Intent(p0.context, WeatherActivity::class.java)
                intent.putExtra("location_lng",place.location.lng)
                intent.putExtra("location_lat",place.location.lat)
                intent.putExtra("place_name",place.name)
                fragment.viewModel.savePlace(place)
                fragment.startActivity(intent)
                fragment.activity?.finish()
            }
            fragment.viewModel.savePlace(place)
            //完成存储place功能

        }

        return holder
    }

    override fun onBindViewHolder(p0: PlaceAdapter.ViewHolder, p1: Int) {
        val place = placeList[p1]
        p0.placeName.text = place.name
        p0.placeAddress.text = place.address

    }

    override fun getItemCount(): Int {
        return placeList.size
    }

}
