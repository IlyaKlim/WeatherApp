package com.example.weather.presenter

import androidx.lifecycle.MutableLiveData
import com.example.weather.network.repository.WeatherRepository
import com.example.weather.network.retrofit.model.Model
import com.example.weather.network.retrofit.model.WeatherModel
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class Presenter(private val weatherRepository: WeatherRepository) {
    private var viewController: ViewController? = null
    private var latLon: List<Double> = listOf()
    private var weatherModel: Model? = null
    private val onWeatherReceivedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val subscriber: Observer<Model> = object : Observer<Model> {


        override fun onComplete() {
            viewController?.enableProgress(false)
            viewController?.launchTodayWeatherFragment()
        }

        override fun onError(e: Throwable) {
            viewController?.loadOnError(e.message ?: "")
        }

        override fun onNext(t: Model) {
            viewController?.enableProgress(false)
            weatherModel = t
        }

        override fun onSubscribe(d: Disposable) {
        }

    }

    fun attachView(viewController: ViewController) {
        this.viewController = viewController
    }

    fun getWeather() {
        if (latLon.isNotEmpty()) {
            weatherRepository.getWeather(lat = latLon[0], lon = latLon[1])
                .observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber)
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

    fun getCity() = weatherModel?.city
}