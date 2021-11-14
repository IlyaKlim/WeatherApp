package com.example.weather.presenter

interface ViewController {
    fun enableProgress(isEnabled: Boolean)
    fun launchTodayWeatherFragment()
    fun launchErrorView(errorText:String,iconId:Int)
}