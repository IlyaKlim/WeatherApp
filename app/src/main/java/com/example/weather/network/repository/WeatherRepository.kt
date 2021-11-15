package com.example.weather.network.repository

import com.example.weather.network.retrofit.WeatherApi
import com.example.weather.network.retrofit.model.Model
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WeatherRepository(private val weatherApi: WeatherApi) {
    fun getWeather(lat: Double, lon: Double): Observable<Model> =
        weatherApi.getWeather(lat = lat, lon = lon).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()
        )
}