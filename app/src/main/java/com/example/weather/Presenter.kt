package com.example.weather

import androidx.lifecycle.MutableLiveData
import com.example.weather.network.repository.WeatherRepository
import com.example.weather.network.retrofit.model.Model
import com.example.weather.network.retrofit.model.WeatherModel

class Presenter(private val weatherRepository: WeatherRepository) {
    private var latLon: List<Double> = listOf()
    private var weatherModel: Model? = null
    private val onWeatherReceivedLiveData:MutableLiveData<Boolean> = MutableLiveData()
    fun getWeather() {
        if (latLon.isNotEmpty()) {
            weatherRepository.getWeather(lat = latLon[0], lon = latLon[1]).subscribe {
                weatherModel = it
                onWeatherReceivedLiveData.postValue(true)
            }
        }
    }

    fun getWeatherReceivedLifeData() = onWeatherReceivedLiveData

    fun setLocation(lat: Double, lon: Double) {
        latLon = listOf(lat, lon)
    }

    fun getTodayWeather(): WeatherModel? {
        return weatherModel?.list?.get(0)
    }

    fun getForecastWeather(): List<WeatherModel>? {
        return weatherModel?.list
    }
}