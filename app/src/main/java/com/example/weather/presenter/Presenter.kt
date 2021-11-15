package com.example.weather.presenter

import com.example.weather.R
import com.example.weather.network.repository.WeatherRepository
import com.example.weather.network.retrofit.model.Model
import com.example.weather.network.retrofit.model.WeatherModel
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class Presenter(private val weatherRepository: WeatherRepository) {
    private var viewController: ViewController? = null
    private var latLon: List<Double> = listOf()
    private var weatherModel: Model? = null
    private val subscriber: Observer<Model> = object : Observer<Model> {


        override fun onComplete() {
            viewController?.enableProgress(false)
            viewController?.launchTodayWeatherFragment()
        }

        override fun onError(e: Throwable) {
            viewController?.launchErrorView(e.message ?: "", R.drawable.ic_error)
        }

        override fun onNext(t: Model) {
            weatherModel = t
        }

        override fun onSubscribe(d: Disposable) {
            viewController?.enableProgress(true)
        }

    }

    fun attachView(viewController: ViewController) {
        this.viewController = viewController
    }

    fun getWeather() {
        if (latLon.isNotEmpty()) {
            weatherRepository.getWeather(lat = latLon[0], lon = latLon[1]).subscribe(subscriber)
        }
    }

    fun setLocation(lat: Double, lon: Double) {
        latLon = listOf(lat, lon)
    }

    fun getTodayWeather(): WeatherModel? {
        return weatherModel?.list?.get(0)
    }

    fun getForecastWeather(): List<WeatherModel>? {
        return weatherModel?.list
    }

    fun getCity() = weatherModel?.city
}