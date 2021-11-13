package com.example.weather.di

import com.example.weather.network.repository.WeatherRepository
import com.example.weather.network.retrofit.WeatherApi
import com.example.weather.presenter.Presenter
import com.example.weather.view.activities.MainActivity
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val mainModule = module {
    scope<MainActivity> {
        scoped { Presenter(weatherRepository = get()) }
    }
}

val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun provideRetrofit(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }
    single { provideRetrofit(retrofit = get()) }
    single { WeatherRepository(weatherApi = get()) }
}