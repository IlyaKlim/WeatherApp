package com.example.weather.network.repository

import com.example.weather.network.retrofit.WeatherApi
import rx.schedulers.Schedulers

class WeatherRepository(private val weatherApi: WeatherApi) {
    fun getWeather(lat: Double, lon: Double) =
        weatherApi.getWeather(lat = lat, lon = lon)
            .doOnError {
                val text = it.message
            }.subscribeOn(Schedulers.io())
}