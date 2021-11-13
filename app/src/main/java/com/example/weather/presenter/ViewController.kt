package com.example.weather.presenter

interface ViewController {
    fun loadOnError(message: String)
    fun enableProgress(isEnabled: Boolean)
    fun launchTodayWeatherFragment()
}