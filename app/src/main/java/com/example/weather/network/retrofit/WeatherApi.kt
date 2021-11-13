package com.example.weather.network.retrofit

import com.example.weather.network.retrofit.model.Model
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast?appid=e936597f5e40a85ec97c88e1836ac9c7&units=metric")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Observable<Model>
}