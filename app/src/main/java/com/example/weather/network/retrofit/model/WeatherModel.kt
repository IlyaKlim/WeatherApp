package com.example.weather.network.retrofit.model

import com.google.gson.annotations.SerializedName

data class Model(
    val list: List<WeatherModel>
)

data class WeatherModel(
    val main: Main,
    val weather: List<Weather>,
    @SerializedName("dt_txt")
    val date:String

)
data class Weather(
    val main:String,
    val icon:String
)

data class Main(
    val temp:String
)


