package com.example.weather

import com.example.weather.network.repository.WeatherRepository

class Presenter(private val weatherRepository: WeatherRepository) {
    fun getWeather(lat: Double, lon: Double) = weatherRepository.getWeather(lat = lat, lon = lon)
}